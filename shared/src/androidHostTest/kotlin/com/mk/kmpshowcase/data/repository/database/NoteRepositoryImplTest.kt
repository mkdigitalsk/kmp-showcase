package com.mk.kmpshowcase.data.repository.database

import app.cash.sqldelight.driver.jdbc.sqlite.JdbcSqliteDriver
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import com.mk.kmpshowcase.data.database.AppDatabase
import com.mk.kmpshowcase.data.repository.NoteRepositoryImpl
import com.mk.kmpshowcase.domain.model.Note
import com.mk.kmpshowcase.domain.model.NoteSortOption
import com.mk.kmpshowcase.util.TestDispatcherProvider
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NoteRepositoryImplTest {

    private lateinit var database: AppDatabase
    private lateinit var repository: NoteRepositoryImpl

    @BeforeTest
    fun setup() {
        val driver = JdbcSqliteDriver(JdbcSqliteDriver.IN_MEMORY)
        AppDatabase.Schema.create(driver)
        database = AppDatabase(driver)
        repository = NoteRepositoryImpl(database, TestDispatcherProvider())
    }

    @AfterTest
    fun tearDown() = runTest {
        repository.deleteAll()
    }

    @Test
    fun `insert and getById returns correct note`() = runTest {
        val note = createNote(title = "Test Title", content = "Test Content")

        repository.insert(note)

        val result = repository.getById(1)
        assertNotNull(result)
        assertEquals("Test Title", result.title)
        assertEquals("Test Content", result.content)
    }

    @Test
    fun `getById returns null for non-existent id`() = runTest {
        val result = repository.getById(999)
        assertNull(result)
    }

    @Test
    fun `update modifies existing note`() = runTest {
        repository.insert(createNote(title = "Original", content = "Original Content"))

        val updatedNote = Note(id = 1, title = "Updated", content = "Updated Content", createdAt = 1000L)
        repository.update(updatedNote)

        val result = repository.getById(1)
        assertNotNull(result)
        assertEquals("Updated", result.title)
        assertEquals("Updated Content", result.content)
    }

    @Test
    fun `delete removes note from database`() = runTest {
        repository.insert(createNote())

        repository.delete(1)

        val result = repository.getById(1)
        assertNull(result)
    }

    @Test
    fun `deleteAll removes all notes`() = runTest {
        repository.insert(createNote(title = "Note 1"))
        repository.insert(createNote(title = "Note 2"))
        repository.insert(createNote(title = "Note 3"))

        repository.deleteAll()

        val count = repository.count()
        assertEquals(0, count)
    }

    @Test
    fun `count returns correct number of notes`() = runTest {
        assertEquals(0, repository.count())

        repository.insert(createNote(title = "Note 1"))
        assertEquals(1, repository.count())

        repository.insert(createNote(title = "Note 2"))
        assertEquals(2, repository.count())
    }

    @Test
    fun `observeAll with DATE_DESC returns notes ordered by date descending`() = runTest {
        repository.insert(createNote(title = "Old", createdAt = 1000L))
        repository.insert(createNote(title = "New", createdAt = 3000L))
        repository.insert(createNote(title = "Middle", createdAt = 2000L))

        val notes = repository.observeAll(NoteSortOption.DATE_DESC).first()

        assertEquals(3, notes.size)
        assertEquals("New", notes[0].title)
        assertEquals("Middle", notes[1].title)
        assertEquals("Old", notes[2].title)
    }

    @Test
    fun `observeAll with DATE_ASC returns notes ordered by date ascending`() = runTest {
        repository.insert(createNote(title = "Old", createdAt = 1000L))
        repository.insert(createNote(title = "New", createdAt = 3000L))
        repository.insert(createNote(title = "Middle", createdAt = 2000L))

        val notes = repository.observeAll(NoteSortOption.DATE_ASC).first()

        assertEquals(3, notes.size)
        assertEquals("Old", notes[0].title)
        assertEquals("Middle", notes[1].title)
        assertEquals("New", notes[2].title)
    }

    @Test
    fun `observeAll with TITLE_ASC returns notes ordered by title ascending`() = runTest {
        repository.insert(createNote(title = "Banana"))
        repository.insert(createNote(title = "Apple"))
        repository.insert(createNote(title = "Cherry"))

        val notes = repository.observeAll(NoteSortOption.TITLE_ASC).first()

        assertEquals(3, notes.size)
        assertEquals("Apple", notes[0].title)
        assertEquals("Banana", notes[1].title)
        assertEquals("Cherry", notes[2].title)
    }

    @Test
    fun `observeAll with TITLE_DESC returns notes ordered by title descending`() = runTest {
        repository.insert(createNote(title = "Banana"))
        repository.insert(createNote(title = "Apple"))
        repository.insert(createNote(title = "Cherry"))

        val notes = repository.observeAll(NoteSortOption.TITLE_DESC).first()

        assertEquals(3, notes.size)
        assertEquals("Cherry", notes[0].title)
        assertEquals("Banana", notes[1].title)
        assertEquals("Apple", notes[2].title)
    }

    @Test
    fun `search finds notes by title`() = runTest {
        repository.insert(createNote(title = "Shopping List", content = "Milk, Bread"))
        repository.insert(createNote(title = "Meeting Notes", content = "Discuss budget"))
        repository.insert(createNote(title = "Recipe", content = "Shopping ingredients"))

        val results = repository.search("Shopping", NoteSortOption.DATE_DESC).first()

        assertEquals(2, results.size)
        assertTrue(results.any { it.title == "Shopping List" })
        assertTrue(results.any { it.title == "Recipe" })
    }

    @Test
    fun `search finds notes by content`() = runTest {
        repository.insert(createNote(title = "Note 1", content = "Contains keyword here"))
        repository.insert(createNote(title = "Note 2", content = "No match"))
        repository.insert(createNote(title = "Note 3", content = "Another keyword instance"))

        val results = repository.search("keyword", NoteSortOption.DATE_DESC).first()

        assertEquals(2, results.size)
    }

    @Test
    fun `search is case insensitive`() = runTest {
        repository.insert(createNote(title = "UPPERCASE", content = "content"))
        repository.insert(createNote(title = "lowercase", content = "content"))
        repository.insert(createNote(title = "MixedCase", content = "content"))

        val results = repository.search("case", NoteSortOption.DATE_DESC).first()

        assertEquals(3, results.size)
    }

    @Test
    fun `search returns empty list when no matches`() = runTest {
        repository.insert(createNote(title = "Note", content = "Content"))

        val results = repository.search("xyz123nonexistent", NoteSortOption.DATE_DESC).first()

        assertTrue(results.isEmpty())
    }

    @Test
    fun `search with TITLE_ASC sorts results by title ascending`() = runTest {
        repository.insert(createNote(title = "Zebra task", content = "work"))
        repository.insert(createNote(title = "Apple task", content = "work"))
        repository.insert(createNote(title = "Mango task", content = "work"))

        val results = repository.search("task", NoteSortOption.TITLE_ASC).first()

        assertEquals(3, results.size)
        assertEquals("Apple task", results[0].title)
        assertEquals("Mango task", results[1].title)
        assertEquals("Zebra task", results[2].title)
    }

    @Test
    fun `search with DATE_ASC sorts results by date ascending`() = runTest {
        repository.insert(createNote(title = "Work 1", content = "task", createdAt = 3000L))
        repository.insert(createNote(title = "Work 2", content = "task", createdAt = 1000L))
        repository.insert(createNote(title = "Work 3", content = "task", createdAt = 2000L))

        val results = repository.search("task", NoteSortOption.DATE_ASC).first()

        assertEquals(3, results.size)
        assertEquals("Work 2", results[0].title)
        assertEquals("Work 3", results[1].title)
        assertEquals("Work 1", results[2].title)
    }

    @Test
    fun `multiple inserts generate unique auto-incremented ids`() = runTest {
        repository.insert(createNote(title = "Note 1"))
        repository.insert(createNote(title = "Note 2"))
        repository.insert(createNote(title = "Note 3"))

        val note1 = repository.getById(1)
        val note2 = repository.getById(2)
        val note3 = repository.getById(3)

        assertNotNull(note1)
        assertNotNull(note2)
        assertNotNull(note3)
        assertEquals("Note 1", note1.title)
        assertEquals("Note 2", note2.title)
        assertEquals("Note 3", note3.title)
    }

    private fun createNote(
        title: String = "Test Note",
        content: String = "Test Content",
        createdAt: Long = System.currentTimeMillis()
    ) = Note(
        id = 0,
        title = title,
        content = content,
        createdAt = createdAt
    )
}
