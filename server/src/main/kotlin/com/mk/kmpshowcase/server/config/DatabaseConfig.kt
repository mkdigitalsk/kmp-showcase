package com.mk.kmpshowcase.server.config

import com.mk.kmpshowcase.server.feature.note.persistence.NotesTable
import com.mk.kmpshowcase.server.feature.user.persistence.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("DatabaseConfig")

object DatabaseConfig {

    fun init() {
        logger.info("Initializing database connection...")
        val database = Database.connect(hikari())

        transaction(database) {
            SchemaUtils.create(UsersTable, NotesTable)
            logger.info("Database tables created/verified")
        }
    }

    private fun hikari(): HikariDataSource {
        val useH2 = System.getenv("USE_H2")?.toBoolean() ?: true

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
                jdbcUrl = System.getenv("DATABASE_URL")
                    ?: "jdbc:postgresql://localhost:5432/kmpshowcase"
                username = System.getenv("DATABASE_USER") ?: "postgres"
                password = System.getenv("DATABASE_PASSWORD") ?: "postgres"
            }
            maximumPoolSize = MAX_POOL_SIZE
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }

    private const val MAX_POOL_SIZE = 10
}
