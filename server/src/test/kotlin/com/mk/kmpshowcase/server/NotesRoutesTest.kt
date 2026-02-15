package com.mk.kmpshowcase.server

import com.mk.kmpshowcase.server.config.DatabaseConfig
import com.mk.kmpshowcase.server.model.CreateNoteRequest
import com.mk.kmpshowcase.server.model.NoteDTO
import com.mk.kmpshowcase.server.plugins.JwtConfig
import com.mk.kmpshowcase.server.plugins.configureAuth
import com.mk.kmpshowcase.server.plugins.configureRouting
import com.mk.kmpshowcase.server.plugins.configureSerialization
import com.mk.kmpshowcase.server.plugins.configureStatusPages
import com.mk.kmpshowcase.server.repository.UserRepository
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

    private fun createTestUser(): Pair<Long, String> {
        val userRepository = UserRepository()
        val uniqueEmail = "test-${UUID.randomUUID()}@test.com"
        val user = userRepository.create(uniqueEmail, "password123", "Test User")
        val token = JwtConfig.generateToken(user.id, user.email)
        return user.id to token
    }

    @Test
    fun `get notes returns empty list for new user`() = testApplication {
        application {
            configureSerialization()
            configureStatusPages()
            configureAuth()
            configureRouting()
        }

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
        application {
            configureSerialization()
            configureStatusPages()
            configureAuth()
            configureRouting()
        }

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
        application {
            configureSerialization()
            configureStatusPages()
            configureAuth()
            configureRouting()
        }

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val (_, token) = createTestUser()

        // Create test notes
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

        // Search for "Shop" (case-sensitive in H2)
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
        application {
            configureSerialization()
            configureStatusPages()
            configureAuth()
            configureRouting()
        }

        val response = client.get("/api/notes")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}
