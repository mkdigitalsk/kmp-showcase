package sk.mkdigital.kmpshowcase.server.feature.user.service

import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UserRepository
import java.time.Instant
import java.time.ZoneOffset
import kotlin.time.Duration
import kotlin.time.Duration.Companion.days

internal val INACTIVITY_LIMIT: Duration = 30.days
internal val PURGE_INTERVAL: Duration = 1.days

/**
 * A sweep rather than a deadline per account: signing in only moves a number, so there is nothing to
 * reschedule, and a restart loses nothing because the row carries the answer. Deleting twice deletes
 * the same rows and then none, so a second instance running it is harmless.
 */
internal class InactiveAccountPurge(
    private val repository: UserRepository,
    private val now: () -> Long = System::currentTimeMillis,
) {
    @Volatile
    var lastRunAt: Long? = null
        private set

    suspend fun run(): Int {
        val deleted = repository.deleteInactiveSince(
            cutoff = startOfDayUtc(now()) - INACTIVITY_LIMIT.inWholeMilliseconds,
            keep = demoAccountEmails(),
        )
        lastRunAt = now()
        return deleted
    }

    // seen 01-01 23:55 → kept through 01-31 → gone 02-01 00:00
    private fun startOfDayUtc(millis: Long): Long =
        Instant.ofEpochMilli(millis).atOffset(ZoneOffset.UTC).toLocalDate().atStartOfDay(ZoneOffset.UTC)
            .toInstant().toEpochMilli()

    // Absence is the failure worth alerting on: a job that stops running never raises an error.
    fun isOverdue(): Boolean {
        val last = lastRunAt ?: return true
        return now() - last > (PURGE_INTERVAL * OVERDUE_TOLERANCE).inWholeMilliseconds
    }

    private companion object {
        const val OVERDUE_TOLERANCE = 2
    }
}
