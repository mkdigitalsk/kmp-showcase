package sk.mkdigital.kmpshowcase.server.config

import sk.mkdigital.kmpshowcase.server.feature.note.persistence.NotesTable
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.jetbrains.exposed.v1.jdbc.update
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("DatabaseConfig")

internal object DatabaseConfig {

    fun init(appConfig: ApplicationConfig) {
        logger.info("Initializing database connection...")
        val database = Database.connect(hikari(appConfig))

        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(UsersTable, NotesTable)
            logger.info("Database tables created/verified")

            backfillLastSeen()
        }
    }

    /**
     * An added column arrives at its default, and a last_seen_at of 0 is older than any cutoff — so without this the
     * first inactivity sweep would delete every account that predates it.
     */
    private fun backfillLastSeen() {
        val backfilled = UsersTable.update({ UsersTable.lastSeenAt eq 0 }) {
            it[lastSeenAt] = System.currentTimeMillis()
        }
        if (backfilled > 0) logger.info("Backfilled last_seen_at for $backfilled account(s)")
    }

    private fun hikari(appConfig: ApplicationConfig): HikariDataSource {
        val isH2 = appConfig.useH2()

        val config = HikariConfig().apply {
            if (isH2) {
                logger.info("Using H2 in-memory database (development)")
                driverClassName = "org.h2.Driver"
                jdbcUrl = "jdbc:h2:mem:kmpshowcase;DB_CLOSE_DELAY=-1"
                username = "sa"
                password = ""
            } else {
                logger.info("Using PostgreSQL database (production)")
                driverClassName = "org.postgresql.Driver"
                jdbcUrl = requireJdbcUrl(appConfig.property("database.url").getString())
                username = appConfig.property("database.user").getString()
                password = appConfig.property("database.password").getString()
            }
            maximumPoolSize = MAX_POOL_SIZE
            drainWhenIdle()
            isAutoCommit = false
            validate()
        }
        return HikariDataSource(config)
    }

    /** A Neon URL carries the password, and Hikari prints the whole URL when it fails. */
    private fun requireJdbcUrl(url: String): String {
        require(url.startsWith("jdbc:postgresql://")) {
            "database.url must be a JDBC URL (jdbc:postgresql://host/db?sslmode=require), " +
                "with the user and password in database.user and database.password"
        }
        return url
    }

    /**
     * Hikari's default is a [fixed-size pool](https://github.com/brettwooldridge/HikariCP#frequently-used) held open,
     * so Neon's compute never [scales to zero](https://neon.com/docs/introduction/scale-to-zero) — drained, it can.
     */
    private fun HikariConfig.drainWhenIdle() {
        minimumIdle = MIN_IDLE
        idleTimeout = IDLE_TIMEOUT_MS
    }

    private const val MAX_POOL_SIZE = 10
    private const val MIN_IDLE = 0
    private const val IDLE_TIMEOUT_MS = 60_000L
}
