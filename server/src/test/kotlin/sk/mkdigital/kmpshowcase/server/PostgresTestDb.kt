package sk.mkdigital.kmpshowcase.server

import sk.mkdigital.kmpshowcase.server.config.DatabaseConfig
import sk.mkdigital.kmpshowcase.server.feature.note.persistence.NotesTable
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UsersTable
import io.ktor.server.config.MapApplicationConfig
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.postgresql.PostgreSQLContainer

/**
 * ⚠ Postgres, not the H2 the account tests use: H2 has no `UPDATE … RETURNING` and raises 90131 where
 * Postgres re-evaluates the predicate, so "no row returned means stale" would pass here for the wrong reason.
 */
internal object PostgresTestDb {

    private val container = PostgreSQLContainer("postgres:16-alpine").apply { start() }

    private val connection by lazy { boot() }

    /** Idempotent: `Database.connect` registers a global default, so a second call stacks a second pool. */
    fun connect() = connection

    /**
     * The call production boots with, against the tables already there, as a deploy meets them — a SchemaUtils.create()
     * here would pass while the app creates no table. Each call stacks one more pool.
     */
    fun boot() = DatabaseConfig.init(
        MapApplicationConfig(
            "database.useH2" to "false",
            "database.url" to container.jdbcUrl,
            "database.user" to container.username,
            "database.password" to container.password,
        ),
    )

    // Unit, not the row count deleteAll answers with: JUnit rejects an @AfterTest method that returns one.
    fun clear(): Unit = transaction {
        NotesTable.deleteAll()
        UsersTable.deleteAll()
    }
}
