package sk.mkdigital.kmpshowcase.server.config

import sk.mkdigital.kmpshowcase.server.feature.note.persistence.NotesTable
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.config.ApplicationConfig
import org.jetbrains.exposed.v1.jdbc.Database
import org.jetbrains.exposed.v1.jdbc.SchemaUtils
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("DatabaseConfig")

internal object DatabaseConfig {

    fun init(appConfig: ApplicationConfig) {
        logger.info("Initializing database connection...")
        val database = Database.connect(hikari(appConfig))

        transaction(database) {
            SchemaUtils.createMissingTablesAndColumns(UsersTable, NotesTable)
            logger.info("Database tables created/verified")
        }
    }

    private fun hikari(appConfig: ApplicationConfig): HikariDataSource {
        val isH2 = appConfig.property("database.useH2").getString().toBoolean()

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
                val url = appConfig.property("database.url").getString()
                // A Neon URL carries the password, and Hikari prints the whole URL when it fails.
                require(url.startsWith("jdbc:postgresql://")) {
                    "database.url must be a JDBC URL (jdbc:postgresql://host/db?sslmode=require), " +
                        "with the user and password in database.user and database.password"
                }
                jdbcUrl = url
                username = appConfig.property("database.user").getString()
                password = appConfig.property("database.password").getString()
            }
            maximumPoolSize = MAX_POOL_SIZE
            // Neon scales the compute to zero after ~5 min idle. Hikari's default fixed pool
            // (minimumIdle == maxPoolSize) keeps connections open and recycles them under that
            // window, so the compute never suspends and burns CU-hours 24/7. Drain to zero when
            // idle so Neon can suspend too.
            minimumIdle = MIN_IDLE
            idleTimeout = IDLE_TIMEOUT_MS
            isAutoCommit = false
            validate()
        }
        return HikariDataSource(config)
    }

    private const val MAX_POOL_SIZE = 10
    private const val MIN_IDLE = 0
    private const val IDLE_TIMEOUT_MS = 60_000L
}
