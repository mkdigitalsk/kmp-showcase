package sk.mkdigital.kmpshowcase.server

import sk.mkdigital.kmpshowcase.server.feature.note.persistence.NoteRepositoryImpl
import sk.mkdigital.kmpshowcase.server.feature.note.service.Note
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.test.runTest
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

/** The predicate and the bump live in one statement, so nothing above this layer can prove them. */
class NoteRepositoryTest {

    private val repository = NoteRepositoryImpl()

    @BeforeTest
    fun setup() = PostgresTestDb.connect()

    @AfterTest
    fun clear() = PostgresTestDb.clear()

    private suspend fun owner(): Long =
        UserRepositoryImpl().create("note-repo-${UUID.randomUUID()}@test.com", "password123").id

    private suspend fun note(ownerId: Long, title: String = "Buy milk"): Note =
        repository.create(ownerId, title, "two litres")

    @Test
    fun `a created note starts at version zero and reads back`() = runTest {
        val ownerId = owner()
        val created = note(ownerId)

        assertEquals(0, created.version, "the tag is derived from this, so a fresh row has to be zero")
        assertEquals(created, repository.findByIdAndOwner(created.id, ownerId))
    }

    // Proven by absence: a null from findByIdAndOwner would only show object-level scoping.
    @Test
    fun `the list carries only the caller's rows`() = runTest {
        val mine = owner()
        val theirs = owner()
        val hidden = note(theirs, title = "Not yours")
        note(mine, title = "Mine")

        val listed = repository.findAllByOwner(mine)

        assertFalse(listed.any { it.id == hidden.id })
        assertNull(repository.findByIdAndOwner(hidden.id, mine))
    }

    @Test
    fun `an update matches only the caller's row at the current version`() = runTest {
        val ownerId = owner()
        val stored = note(ownerId)
        val intruder = owner()

        assertNull(repository.update(stored.id, intruder, setOf(0), "theirs", "no"), "the owner is in the predicate")
        assertNull(repository.update(stored.id, ownerId, setOf(1), "stale", "no"), "the version is in it too")
        assertEquals(stored, repository.findByIdAndOwner(stored.id, ownerId), "a refused write leaves nothing behind")

        val updated = repository.update(stored.id, ownerId, setOf(0), "Buy oat milk", "one litre")

        assertEquals("Buy oat milk", updated?.title)
        assertEquals(1, updated?.version, "the bump and the precondition are one statement")
    }

    @Test
    fun `an unconditional update writes whatever the version is`() = runTest {
        val ownerId = owner()
        val stored = note(ownerId)
        repository.update(stored.id, ownerId, setOf(0), "moved on", "content")

        val updated = repository.update(stored.id, ownerId, expectedVersions = null, "unconditional", "content")

        assertEquals(2, updated?.version)
    }

    // The bump is relative to what is stored, not to whichever version of the list matched.
    @Test
    fun `a precondition naming several versions matches on any of them`() = runTest {
        val ownerId = owner()
        val stored = note(ownerId)

        val updated = repository.update(stored.id, ownerId, setOf(0, 5), "either", "content")

        assertEquals(1, updated?.version)
    }

    @Test
    fun `a precondition naming no version matches nothing`() = runTest {
        val ownerId = owner()
        val stored = note(ownerId)

        assertNull(repository.update(stored.id, ownerId, emptySet(), "impossible", "content"))
    }

    // Fails if the version is ever read-then-compared instead of matched in the `UPDATE` predicate.
    @Suppress("InjectDispatcher") // nothing to inject: the racers need real threads, runTest has one
    @Test
    fun `two writers at the same version, one wins`() = runTest {
        val ownerId = owner()
        val stored = note(ownerId)

        val results = coroutineScope {
            listOf("first", "second")
                .map { title -> async(Dispatchers.IO) { repository.update(stored.id, ownerId, setOf(0), title, "c") } }
                .awaitAll()
        }

        assertEquals(1, results.count { it != null }, "the loser gets null, which the service turns into 412")
        assertEquals(1, results.filterNotNull().single().version, "the winner bumped once, not twice")
    }

    @Test
    fun `a delete reaches only the caller's row`() = runTest {
        val ownerId = owner()
        val stored = note(ownerId)
        val intruder = owner()

        assertFalse(repository.delete(stored.id, intruder))
        assertNotNull(repository.findByIdAndOwner(stored.id, ownerId), "another account's delete must not land")

        assertTrue(repository.delete(stored.id, ownerId))
        assertNull(repository.findByIdAndOwner(stored.id, ownerId))
    }
}
