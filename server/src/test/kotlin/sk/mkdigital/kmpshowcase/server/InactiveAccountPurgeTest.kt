package sk.mkdigital.kmpshowcase.server

import sk.mkdigital.kmpshowcase.server.feature.user.service.INACTIVITY_LIMIT
import sk.mkdigital.kmpshowcase.server.feature.user.service.InactiveAccountPurge
import sk.mkdigital.kmpshowcase.server.feature.user.service.ThemeMode
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UserRepository
import sk.mkdigital.kmpshowcase.server.feature.user.service.User
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.days

private const val NOW = 1_800_000_000_000L

class InactiveAccountPurgeTest {

    private val repository = FakeUserRepository()
    private var clock = NOW
    private val purge = InactiveAccountPurge(repository) { clock }

    @Test
    fun `an account nobody has opened past the limit is removed`() = runTest {
        repository.add("dormant@test.com", lastSeenAt = NOW - (INACTIVITY_LIMIT + 1.days).inWholeMilliseconds)

        purge.run()

        assertEquals(emptyList(), repository.emails())
    }

    @Test
    fun `an account opened inside the limit is kept`() = runTest {
        repository.add("active@test.com", lastSeenAt = NOW - (INACTIVITY_LIMIT - 1.days).inWholeMilliseconds)

        purge.run()

        assertEquals(listOf("active@test.com"), repository.emails())
    }

    @Test
    fun `a dormant demo account survives, because the sign-in screen hands it out`() = runTest {
        val forever = NOW - (INACTIVITY_LIMIT * 10).inWholeMilliseconds
        repository.add("test01@mkdigital.sk", lastSeenAt = forever)

        purge.run()

        assertEquals(listOf("test01@mkdigital.sk"), repository.emails())
    }

    @Test
    fun `a sweep that has never run reads as overdue`() {
        assertTrue(purge.isOverdue(), "nothing has swept yet, which is the state worth alerting on")
    }

    @Test
    fun `a sweep that stops running reads as overdue`() = runTest {
        purge.run()
        assertFalse(purge.isOverdue())

        clock += 3.days.inWholeMilliseconds

        assertTrue(purge.isOverdue())
    }
}

private class FakeUserRepository : UserRepository {

    private val rows = mutableMapOf<String, Long>()

    fun add(email: String, lastSeenAt: Long) {
        rows[email] = lastSeenAt
    }

    fun emails(): List<String> = rows.keys.sorted()

    // The predicate the SQL runs, not a simplification of it: a fake that ignores either half would let
    // every one of these tests pass while the sweep deleted the wrong rows.
    override suspend fun deleteInactiveSince(cutoff: Long, keep: Set<String>): Int {
        val doomed = rows.filter { (email, lastSeenAt) -> lastSeenAt < cutoff && email !in keep }.keys
        doomed.forEach(rows::remove)
        return doomed.size
    }

    override suspend fun findByEmail(email: String): User? = unused()
    override suspend fun findById(id: Long): User? = unused()
    override suspend fun create(email: String, password: String): User = unused()
    override suspend fun authenticate(email: String, password: String): User? = unused()
    override suspend fun updateThemeMode(id: Long, themeMode: ThemeMode): User? = unused()
    override suspend fun updateLocale(id: Long, locale: String): User? = unused()
    override suspend fun delete(id: Long): Boolean = unused()
    override suspend fun touchLastSeen(id: Long) = unused()

    private fun unused(): Nothing = throw UnsupportedOperationException("not part of the sweep")
}
