package sk.mkdigital.kmpshowcase.data.repository.auth

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.test.runTest
import sk.mkdigital.kmpshowcase.contracts.auth.AuthResponseDTO
import sk.mkdigital.kmpshowcase.contracts.auth.AuthUserDTO
import sk.mkdigital.kmpshowcase.contracts.user.ThemeModeDTO
import sk.mkdigital.kmpshowcase.data.client.AuthClient
import sk.mkdigital.kmpshowcase.data.local.preferences.PersistentPreferences
import sk.mkdigital.kmpshowcase.data.local.preferences.PersistentPreferencesImpl
import sk.mkdigital.kmpshowcase.data.local.preferences.Preferences
import sk.mkdigital.kmpshowcase.data.local.preferences.SessionPreferences
import sk.mkdigital.kmpshowcase.data.local.preferences.SessionPreferencesImpl
import sk.mkdigital.kmpshowcase.data.repository.AuthRepositoryImpl
import sk.mkdigital.kmpshowcase.domain.BaseTest
import sk.mkdigital.kmpshowcase.domain.exceptions.base.ApiException
import sk.mkdigital.kmpshowcase.domain.model.Note
import sk.mkdigital.kmpshowcase.domain.model.NoteSortOption
import sk.mkdigital.kmpshowcase.domain.repository.NoteRepository
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class AuthRepositoryImplTest : BaseTest<AuthRepositoryImpl>() {
    override lateinit var classUnderTest: AuthRepositoryImpl

    private lateinit var persistentPreferences: PersistentPreferences
    private lateinit var sessionPreferences: SessionPreferences
    private lateinit var noteRepository: FakeNoteRepository
    private lateinit var client: FakeAuthClient

    override fun beforeEach() {
        persistentPreferences = PersistentPreferencesImpl(FakePreferences())
        sessionPreferences = SessionPreferencesImpl(FakePreferences())
        noteRepository = FakeNoteRepository()
        client = FakeAuthClient(persistentPreferences, noteRepository)
        classUnderTest = AuthRepositoryImpl(
            client = client,
            preferences = persistentPreferences,
            sessionPreferences = sessionPreferences,
            noteRepository = noteRepository,
        )
    }

    @Test
    fun `sign out clears the token, the push token, both counters and the local notes`() = runTest {
        givenSignedInWithLocalData()

        classUnderTest.signOut()

        assertLocalUserDataCleared()
    }

    @Test
    fun `a store that cannot be cleared does not report the deletion as failed`() = runTest {
        givenSignedInWithLocalData()
        noteRepository.failOnDeleteAll = true

        classUnderTest.deleteAccount()

        assertNull(persistentPreferences.getToken(), "the server erased the account, so the session is over")
    }

    @Test
    fun `a store that cannot be cleared still ends the session`() = runTest {
        givenSignedInWithLocalData()
        noteRepository.failOnDeleteAll = true

        classUnderTest.signOut()

        assertNull(persistentPreferences.getToken(), "a surviving token leaves the person signed in")
    }

    @Test
    fun `sign out keeps the theme mode`() = runTest {
        givenSignedInWithLocalData()

        classUnderTest.signOut()

        assertEquals(THEME_MODE, persistentPreferences.getThemeMode())
    }

    @Test
    fun `delete account calls the endpoint before it clears local data`() = runTest {
        givenSignedInWithLocalData()

        classUnderTest.deleteAccount()

        assertEquals(TOKEN, client.tokenWhenDeleted)
        assertEquals(1L, client.notesWhenDeleted)
        assertLocalUserDataCleared()
    }

    @Test
    fun `delete account keeps local data when the endpoint fails`() = runTest {
        givenSignedInWithLocalData()
        client.failure = ApiException(httpCode = 500, message = "Server error: Internal Server Error")

        assertFailsWith<ApiException> { classUnderTest.deleteAccount() }

        assertEquals(TOKEN, persistentPreferences.getToken())
        assertEquals(FCM_TOKEN, persistentPreferences.getFcmToken())
        assertEquals(PERSISTENT_COUNTER, persistentPreferences.getPersistentCounter())
        assertEquals(SESSION_COUNTER, sessionPreferences.getSessionCounter())
        assertEquals(1L, noteRepository.count())
    }

    @Test
    fun `relaunching keeps the demo flag the server sent`() = runTest {
        persistentPreferences.setToken(TOKEN)
        client.meResponse = authResponse(demo = true)

        classUnderTest.signInWithToken()

        assertTrue(classUnderTest.isDemoAccount(), "the launch path re-reads the account, so it must persist it too")
    }

    @Test
    fun `signing in keeps the demo flag the server sent`() = runTest {
        client.signInResponse = authResponse(demo = true)

        classUnderTest.signIn(EMAIL, PASSWORD)

        assertTrue(classUnderTest.isDemoAccount())
    }

    @Test
    fun `signing in to a normal account clears the previous demo flag`() = runTest {
        client.signInResponse = authResponse(demo = true)
        classUnderTest.signIn(EMAIL, PASSWORD)

        client.signInResponse = authResponse(demo = false)
        classUnderTest.signIn(EMAIL, PASSWORD)

        assertFalse(classUnderTest.isDemoAccount())
    }

    @Test
    fun `signing out drops the demo flag`() = runTest {
        client.signInResponse = authResponse(demo = true)
        classUnderTest.signIn(EMAIL, PASSWORD)

        classUnderTest.signOut()

        assertFalse(classUnderTest.isDemoAccount(), "it would hide the control from whoever signs in next")
    }

    private fun authResponse(demo: Boolean) = AuthResponseDTO(
        token = TOKEN,
        user = AuthUserDTO(
            id = 1L,
            email = EMAIL,
            themeMode = ThemeModeDTO.SYSTEM,
            locale = "en",
            demo = demo,
        ),
    )

    private suspend fun givenSignedInWithLocalData() {
        persistentPreferences.setToken(TOKEN)
        persistentPreferences.setFcmToken(FCM_TOKEN)
        persistentPreferences.setPersistentCounter(PERSISTENT_COUNTER)
        persistentPreferences.setThemeMode(THEME_MODE)
        sessionPreferences.setSessionCounter(SESSION_COUNTER)
        noteRepository.insert(Note(id = 1L, title = "Groceries", content = "Milk", createdAt = 0L))
    }

    private suspend fun assertLocalUserDataCleared() {
        assertNull(persistentPreferences.getToken())
        assertNull(persistentPreferences.getFcmToken())
        assertEquals(0, persistentPreferences.getPersistentCounter())
        assertEquals(0, sessionPreferences.getSessionCounter())
        assertEquals(0L, noteRepository.count())
    }

    private companion object {
        const val TOKEN = "jwt-token"
        const val EMAIL = "demo01@mkdigital.sk"
        const val PASSWORD = "MKDigitalTest1@"
        const val FCM_TOKEN = "fcm-token"
        const val PERSISTENT_COUNTER = 7
        const val SESSION_COUNTER = 3
        const val THEME_MODE = "DARK"
    }
}

private class FakeAuthClient(
    private val preferences: PersistentPreferences,
    private val noteRepository: NoteRepository,
) : AuthClient {

    var failure: Throwable? = null
    var tokenWhenDeleted: String? = null
        private set
    var notesWhenDeleted: Long? = null
        private set

    var signInResponse: AuthResponseDTO? = null
    var meResponse: AuthResponseDTO? = null

    override suspend fun deleteAccount() {
        tokenWhenDeleted = preferences.getToken()
        notesWhenDeleted = noteRepository.count()
        failure?.let { throw it }
    }

    override suspend fun signIn(email: String, password: String): AuthResponseDTO =
        signInResponse ?: unused()

    override suspend fun signUp(email: String, password: String): AuthResponseDTO = unused()
    override suspend fun me(token: String): AuthResponseDTO = meResponse ?: unused()

    private fun unused(): Nothing = throw UnsupportedOperationException("not part of the deletion flow")
}

private class FakeNoteRepository : NoteRepository {

    var failOnDeleteAll: Boolean = false
    private val notes = MutableStateFlow<List<Note>>(emptyList())

    override fun observeAll(sortOption: NoteSortOption): Flow<List<Note>> = notes
    override fun search(query: String, sortOption: NoteSortOption): Flow<List<Note>> = notes
    override suspend fun getById(id: Long): Note? = notes.value.firstOrNull { it.id == id }
    override suspend fun insert(note: Note) { notes.value = notes.value + note }
    override suspend fun update(note: Note) { notes.value = notes.value.map { if (it.id == note.id) note else it } }
    override suspend fun delete(id: Long) { notes.value = notes.value.filterNot { it.id == id } }
    override suspend fun deleteAll() {
        if (failOnDeleteAll) error("the notes table is unreadable")
        notes.value = emptyList()
    }
    override suspend fun count(): Long = notes.value.size.toLong()
}

private class FakePreferences : Preferences {

    override val storageName: String = "fake"

    private val values = mutableMapOf<String, Any>()

    override suspend fun putString(key: String, value: String?) = put(key, value)
    override suspend fun getString(key: String): String? = values[key] as String?

    override suspend fun putBoolean(key: String, value: Boolean?) = put(key, value)
    override suspend fun getBoolean(key: String): Boolean? = values[key] as Boolean?

    override suspend fun putInt(key: String, value: Int?) = put(key, value)
    override suspend fun getInt(key: String): Int? = values[key] as Int?

    override suspend fun putFloat(key: String, value: Float?) = put(key, value)
    override suspend fun getFloat(key: String): Float? = values[key] as Float?

    override suspend fun putLong(key: String, value: Long?) = put(key, value)
    override suspend fun getLong(key: String): Long? = values[key] as Long?

    override suspend fun putDouble(key: String, value: Double?) = put(key, value)
    override suspend fun getDouble(key: String): Double? = values[key] as Double?

    override suspend fun remove(key: String) {
        values.remove(key)
    }

    override suspend fun clear() {
        values.clear()
    }

    private fun put(key: String, value: Any?) {
        if (value == null) values.remove(key) else values[key] = value
    }
}
