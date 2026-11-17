package sk.mkdigital.kmpshowcase.server

import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UsersTable
import kotlinx.coroutines.test.runTest
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

/**
 * Real Postgres, booted as a deploy meets production — a database made while the column held a free tag defaults it
 * to that tag, which every insert leaving the column out would store.
 */
class LanguageColumnTest {

    @BeforeTest
    fun connect() = PostgresTestDb.connect()

    /**
     * The boot puts the key back as the column's default, so the shared container leaves no free tag to the next class.
     */
    @AfterTest
    fun restore() {
        PostgresTestDb.clear()
        PostgresTestDb.boot()
    }

    @Test
    fun `boot gives a column made for a free tag the key as its default`() = runTest {
        defaultTheColumnToAFreeTag()

        PostgresTestDb.boot()
        val email = "dana-${UUID.randomUUID()}@example.com"
        transaction {
            exec(
                "INSERT INTO ${identity(UsersTable)} (${identity(UsersTable.email)}, " +
                    "${identity(UsersTable.passwordHash)}, ${identity(UsersTable.createdAt)}, " +
                    "${identity(UsersTable.updatedAt)}) VALUES ('$email', '', 0, 0)",
            )
        }

        assertEquals("EN", StoredAccounts.languageOf(email))
    }

    @Test
    fun `a new account stores the key while the database default is a free tag`() = runTest {
        defaultTheColumnToAFreeTag()

        val account = UserRepositoryImpl().create("dana-${UUID.randomUUID()}@example.com", "password123")

        assertEquals("EN", StoredAccounts.languageOf(account.email))
    }

    private fun defaultTheColumnToAFreeTag() = transaction {
        exec(
            "ALTER TABLE ${identity(UsersTable)} ALTER COLUMN ${identity(UsersTable.language)} " +
                "SET DEFAULT '$FREE_TAG_DEFAULT'",
        )
    }
}

/** The default the column held while it took a free tag. */
private const val FREE_TAG_DEFAULT = "en-GB"
