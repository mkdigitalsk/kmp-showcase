package com.mk.kmpshowcase.server

import com.mk.kmpshowcase.server.config.DatabaseConfig
import com.mk.kmpshowcase.server.core.security.JwtConfig
import com.mk.kmpshowcase.server.di.AppDependencies
import com.mk.kmpshowcase.server.core.mail.MailConfig
import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.contracts.note.CreateNoteRequestDTO
import com.mk.kmpshowcase.contracts.note.NoteResponseDTO
import com.mk.kmpshowcase.server.feature.user.persistence.UserRepository
import com.mk.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import com.mk.kmpshowcase.server.plugins.configureAuth
import com.mk.kmpshowcase.server.plugins.configureRateLimit
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
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
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
        private val jwtConfig = JwtConfig(
            secret = "test-secret",
            issuer = "kmp-showcase",
            audience = "kmp-showcase-users",
        )
    }

    @BeforeTest
    fun setup() {
        if (!initialized) {
            DatabaseConfig.init(MapApplicationConfig("database.useH2" to "true"))
            initialized = true
        }
    }

    private fun createTestUser(): Pair<Long, String> = runBlocking {
        val userRepository: UserRepository = UserRepositoryImpl()
        val uniqueEmail = "test-${UUID.randomUUID()}@test.com"
        val user = userRepository.create(uniqueEmail, "password123", "Test User")
        val token = jwtConfig.generateToken(user.id, user.email, user.role.name)
        user.id to token
    }

    private fun notesTest(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        environment { config = MapApplicationConfig() }
        application {
            configureSerialization()
            configureStatusPages()
            configureAuth(jwtConfig)
            configureRateLimit()
            configureRouting(AppDependencies(jwtConfig, MailConfig("", 0, "", "", "", "", "")))
        }
        block()
    }

    @Test
    fun `get notes returns empty list for new user`() = notesTest {

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val (_, token) = createTestUser()

        val response = client.get("${ApiVersion.BASE}/notes") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val notes = response.body<List<NoteResponseDTO>>()
        assertTrue(notes.isEmpty())
    }

    @Test
    fun `create note returns created note`() = notesTest {

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val (_, token) = createTestUser()

        val response = client.post("${ApiVersion.BASE}/notes") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(CreateNoteRequestDTO(title = "Test Note", content = "Test content"))
        }

        assertEquals(HttpStatusCode.Created, response.status)
        val note = response.body<NoteResponseDTO>()
        assertEquals("Test Note", note.title)
        assertEquals("Test content", note.content)
    }

    @Test
    fun `search notes filters by title`() = notesTest {

        val client = createClient {
            install(ContentNegotiation) { json() }
        }

        val (_, token) = createTestUser()

        client.post("${ApiVersion.BASE}/notes") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(CreateNoteRequestDTO(title = "Shopping list", content = "Milk, eggs"))
        }
        client.post("${ApiVersion.BASE}/notes") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(CreateNoteRequestDTO(title = "Work tasks", content = "Meeting at 10"))
        }

        val response = client.get("${ApiVersion.BASE}/notes/search?q=Shop") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val notes = response.body<List<NoteResponseDTO>>()
        assertEquals(1, notes.size)
        assertEquals("Shopping list", notes[0].title)
    }

    @Test
    fun `get notes without auth returns unauthorized`() = notesTest {

        val response = client.get("${ApiVersion.BASE}/notes")

        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }
}
