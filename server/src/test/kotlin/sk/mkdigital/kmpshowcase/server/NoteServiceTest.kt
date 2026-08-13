package sk.mkdigital.kmpshowcase.server

import sk.mkdigital.kmpshowcase.server.fake.FakeNoteRepository
import sk.mkdigital.kmpshowcase.server.feature.note.service.Note
import sk.mkdigital.kmpshowcase.server.feature.note.service.NoteService
import sk.mkdigital.kmpshowcase.server.feature.note.service.WriteResult
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs

/** The repository only answers null; deciding *why* it matched nothing is the service's own work. */
class NoteServiceTest {

    private val owner = 7L
    private val stored = Note(
        id = 1,
        ownerId = owner,
        title = "Buy milk",
        content = "two litres",
        createdAt = 0,
        updatedAt = 0,
        version = 2,
    )

    private fun service(vararg rows: Note) = NoteService(FakeNoteRepository(rows.toList()))

    @Test
    fun `an update at the current version is written and bumps it`() = runTest {
        val result = service(stored).update(stored.id, owner, setOf(2), "Buy oat milk", "one litre")

        val written = assertIs<WriteResult.Written>(result)
        assertEquals("Buy oat milk", written.note.title)
        assertEquals(3, written.note.version, "the tag has to move or the next stale write would pass")
    }

    @Test
    fun `an update at a stale version is stale, and carries the current row`() = runTest {
        val result = service(stored).update(stored.id, owner, setOf(1), "Second", "lost")

        val stale = assertIs<WriteResult.Stale>(result)
        assertEquals(stored, stale.current)
    }

    // ⚠ Scoping only the write would leak existence as a 412 — the re-read carries the owner too.
    @Test
    fun `an update to another account's note is not found, never stale`() = runTest {
        val result = service(stored).update(stored.id, ownerId = 99, setOf(2), "theirs", "no")

        assertIs<WriteResult.NotFound>(result)
    }

    @Test
    fun `an update to a missing note is not found`() = runTest {
        val result = service().update(id = 404, ownerId = owner, setOf(2), "gone", "no")

        assertIs<WriteResult.NotFound>(result)
    }

    @Test
    fun `a created note is stored trimmed`() = runTest {
        val created = service().create(owner, "  Buy milk  ", "  two litres  ")

        assertEquals("Buy milk", created.title)
        assertEquals("two litres", created.content)
    }

    @Test
    fun `an updated note is stored trimmed`() = runTest {
        val result = service(stored).update(stored.id, owner, setOf(2), "  Buy oat milk  ", "  one litre  ")

        val written = assertIs<WriteResult.Written>(result)
        assertEquals("Buy oat milk", written.note.title)
        assertEquals("one litre", written.note.content)
    }
}
