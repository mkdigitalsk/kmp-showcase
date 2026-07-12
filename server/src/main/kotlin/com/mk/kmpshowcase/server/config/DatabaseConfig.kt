package com.mk.kmpshowcase.server.config

import com.mk.kmpshowcase.server.feature.lead.persistence.LeadArtifactsTable
import com.mk.kmpshowcase.server.feature.lead.persistence.LeadsTable
import com.mk.kmpshowcase.server.feature.note.persistence.NotesTable
import com.mk.kmpshowcase.server.feature.user.persistence.UsersTable
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
            // createMissingTablesAndColumns (not create) so additive migrations — the leads.status
            // column + the lead_artifacts table — land on an already-populated DB.
            SchemaUtils.createMissingTablesAndColumns(UsersTable, NotesTable, LeadsTable, LeadArtifactsTable)
            logger.info("Database tables created/verified")
        }
    }

    private fun hikari(appConfig: ApplicationConfig): HikariDataSource {
        val useH2 = appConfig.property("database.useH2").getString().toBoolean()

        val config = HikariConfig().apply {
            if (useH2) {
                logger.info("Using H2 in-memory database (development)")
                driverClassName = "org.h2.Driver"
                jdbcUrl = "jdbc:h2:mem:kmpshowcase;DB_CLOSE_DELAY=-1"
                username = "sa"
                password = ""
            } else {
                logger.info("Using PostgreSQL database (production)")
                driverClassName = "org.postgresql.Driver"
                jdbcUrl = appConfig.property("database.url").getString()
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
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }

    private const val MAX_POOL_SIZE = 10
    private const val MIN_IDLE = 0
    private const val IDLE_TIMEOUT_MS = 60_000L
}
