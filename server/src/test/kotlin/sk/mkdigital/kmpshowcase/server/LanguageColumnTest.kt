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
 * Real Postgres, booted as a deploy meets production — an insert leaves the column to the default the database holds,
 * so a database made while the column held a free tag would store that tag for every new account.
 */
class LanguageColumnTest {

    @BeforeTest
    fun connect() = PostgresTestDb.connect()

    @AfterTest
    fun clear() = PostgresTestDb.clear()

    @Test
    fun `boot gives a column made for a free tag the key as its default`() = runTest {
        transaction {
            exec(
                "ALTER TABLE ${identity(UsersTable)} ALTER COLUMN ${identity(UsersTable.language)} " +
                    "SET DEFAULT '$FREE_TAG_DEFAULT'",
            )
        }

        PostgresTestDb.boot()
        val account = UserRepositoryImpl().create("dana-${UUID.randomUUID()}@example.com", "password123")

        assertEquals("EN", StoredAccounts.languageOf(account.email))
    }
}

/** The default the column held while it took a free tag. */
private const val FREE_TAG_DEFAULT = "en-GB"
