package com.mk.kmpshowcase.server.config

import com.mk.kmpshowcase.server.repository.NotesTable
import com.mk.kmpshowcase.server.repository.UsersTable
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction

object DatabaseConfig {

    fun init() {
        val database = Database.connect(hikari())

        transaction(database) {
            SchemaUtils.create(UsersTable, NotesTable)
        }
    }

    private fun hikari(): HikariDataSource {
        val useH2 = System.getenv("USE_H2")?.toBoolean() ?: true

        val config = HikariConfig().apply {
            if (useH2) {
                // H2 in-memory database (development)
                driverClassName = "org.h2.Driver"
                jdbcUrl = "jdbc:h2:mem:kmpshowcase;DB_CLOSE_DELAY=-1"
                username = "sa"
                password = ""
            } else {
                // PostgreSQL (production)
                driverClassName = "org.postgresql.Driver"
                jdbcUrl = System.getenv("DATABASE_URL")
                    ?: "jdbc:postgresql://localhost:5432/kmpshowcase"
                username = System.getenv("DATABASE_USER") ?: "postgres"
                password = System.getenv("DATABASE_PASSWORD") ?: "postgres"
            }
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_REPEATABLE_READ"
            validate()
        }
        return HikariDataSource(config)
    }
}
