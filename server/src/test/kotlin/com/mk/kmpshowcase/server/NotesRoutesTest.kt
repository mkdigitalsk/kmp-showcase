package com.mk.kmpshowcase.server

import com.mk.kmpshowcase.server.config.DatabaseConfig
import com.mk.kmpshowcase.server.core.security.JwtConfig
import com.mk.kmpshowcase.server.di.AppDependencies
import com.mk.kmpshowcase.server.feature.note.api.CreateNoteRequest
import com.mk.kmpshowcase.server.feature.note.api.NoteDTO
import com.mk.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import com.mk.kmpshowcase.server.plugins.configureAuth
import com.mk.kmpshowcase.server.plugins.configureRouting
import com.mk.kmpshowcase.server.plugins.configureSerialization
import com.mk.kmpshowcase.server.plugins.configureStatusPages
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.testing.testApplication
import java.util.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class NotesRoutesTest {

    companion object {
        private var initialized = false
    }

    @BeforeTest
    fun setup() {
        if (!initialized) {
            DatabaseConfig.init()
            initialized = true
        }
    }

    private fun createTestUser(): Pair<Long, String> = runBlocking {
        val userRepository = UserRepositoryImpl()
        val uniqueEmail = "test-${UUID.randomUUID()}@test.com"
        val user = userRepository.create(uniqueEmail, "password123", "Test User")
        val token = JwtConfig.generateToken(user.id, user.email)
        user.id to token
    }

    private fun io.ktor.server.application.Application.testModule() {
        configureSerialization()
        configureStatusPages()
        configureAuth()
        configureRouting(AppDependencies())
    }

    @Test
    fun `get notes returns empty list for new user`() = testApplication {
        application { testModule() }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val (_, token) = createTestUser()

        val response = client.get("/api/notes") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val notes = response.body<List<NoteDTO>>()
        assertTrue(notes.isEmpty())
    }

    @Test
    fun `create note returns created note`() = testApplication {
        application { testModule() }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val (_, token) = createTestUser()

        val response = client.post("/api/notes") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(CreateNoteRequest(title = "Test Note", content = "Test content"))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val note = response.body<NoteDTO>()
        assertEquals("Test Note", note.title)
        assertEquals("Test content", note.content)
    }

    @Test
    fun `search notes filters by title`() = testApplication {
        application { testModule() }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val (_, token) = createTestUser()

        client.post("/api/notes") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(CreateNoteRequest(title = "Shopping list", content = "Milk, eggs"))
        }
        client.post("/api/notes") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(CreateNoteRequest(title = "Work tasks", content = "Meeting at 10"))
        }

        val response = client.get("/api/notes/search?q=Shop") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val notes = response.body<List<NoteDTO>>()
        assertEquals(1, notes.size)
        assertEquals("Shopping list", notes[0].title)
    }

    @Test
    fun `get notes without auth returns unauthorized`() = testApplication {
        application { testModule() }

        val response = client.get("/api/notes")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}
