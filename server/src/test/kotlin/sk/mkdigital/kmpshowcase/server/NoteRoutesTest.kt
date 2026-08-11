package sk.mkdigital.kmpshowcase.server

import sk.mkdigital.kmpshowcase.contracts.ApiVersion
import sk.mkdigital.kmpshowcase.contracts.note.CreateNoteRequestDTO
import sk.mkdigital.kmpshowcase.contracts.note.NoteResponseDTO
import sk.mkdigital.kmpshowcase.contracts.note.UpdateNoteRequestDTO
import sk.mkdigital.kmpshowcase.server.config.DatabaseConfig
import sk.mkdigital.kmpshowcase.server.core.security.JwtConfig
import sk.mkdigital.kmpshowcase.server.di.AppDependencies
import sk.mkdigital.kmpshowcase.server.feature.note.persistence.NotesTable
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UsersTable
import sk.mkdigital.kmpshowcase.server.plugins.configureAuth
import sk.mkdigital.kmpshowcase.server.plugins.configureRateLimit
import sk.mkdigital.kmpshowcase.server.plugins.configureRouting
import sk.mkdigital.kmpshowcase.server.plugins.configureSerialization
import sk.mkdigital.kmpshowcase.server.plugins.configureStatusPages
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import kotlinx.coroutines.runBlocking
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.deleteAll
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.transaction
import org.testcontainers.containers.PostgreSQLContainer
import java.util.UUID
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNotEquals

/**
 * ⚠ Real Postgres, not the H2 the other server tests use. The conditional update is one
 * `UPDATE … RETURNING`, which H2 does not have, and H2 answers a concurrent row update with error
 * 90131 where Postgres re-evaluates the predicate — so "no row returned means stale" would pass or
 * fail here for the wrong reason.
 */
private val PreconditionRequired = HttpStatusCode(428, "Precondition Required")

class NoteRoutesTest {

    companion object {
        private val postgres = PostgreSQLContainer("postgres:16-alpine").apply { start() }
        private var connected = false
        private val jwtConfig = JwtConfig(
            secret = "test-secret-at-least-32-bytes-long-for-hs256",
            issuer = "kmp-showcase",
            audience = "kmp-showcase-users",
        )
    }

    // The schema comes from DatabaseConfig, the same call production boots with, rather than from a
    // create() listing the tables this file happens to need. Listing them here would have passed while
    // the app created no `notes` table at all — which is exactly what shipped.
    @BeforeTest
    fun setup() {
        if (!connected) {
            DatabaseConfig.init(
                MapApplicationConfig(
                    "database.useH2" to "false",
                    "database.url" to postgres.jdbcUrl,
                    "database.user" to postgres.username,
                    "database.password" to postgres.password,
                ),
            )
            connected = true
        }
    }

    @AfterTest
    fun clear() {
        transaction {
            NotesTable.deleteAll()
            UsersTable.deleteAll()
        }
    }

    private fun signedIn(): String = runBlocking {
        val user = UserRepositoryImpl().create("note-${UUID.randomUUID()}@test.com", "password123")
        jwtConfig.generateToken(user.id, user.email)
    }

    private fun notesTest(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        environment { config = MapApplicationConfig() }
        application {
            configureSerialization()
            configureStatusPages()
            configureAuth(jwtConfig)
            configureRateLimit()
            configureRouting(AppDependencies(jwtConfig))
        }
        block()
    }

    private suspend fun ApplicationTestBuilder.createNote(token: String, title: String = "Buy milk"): NoteResponseDTO {
        val client = createClient { install(ContentNegotiation) { json() } }
        val response = client.post("${ApiVersion.BASE}/notes") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(CreateNoteRequestDTO(title = title, content = "two litres"))
        }
        assertEquals(HttpStatusCode.Created, response.status)
        return response.body()
    }

    @Test
    fun `a note round-trips and comes back with its tag`() = notesTest {
        val token = signedIn()
        val created = createNote(token)
        assertEquals("Buy milk", created.title)
        assertEquals("\"0\"", created.etag, "a fresh row is version zero")

        val client = createClient { install(ContentNegotiation) { json() } }
        val listed: List<NoteResponseDTO> = client.get("${ApiVersion.BASE}/notes") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
        assertEquals(listOf(created.id), listed.map { it.id })
    }

    // The filter proven by absence. A 404 on a direct fetch would only prove object-level scoping;
    // this is the assertion that a lower-privilege caller cannot enumerate.
    @Test
    fun `the list never carries another account's notes`() = notesTest {
        val mine = signedIn()
        val theirs = signedIn()
        val hidden = createNote(theirs, title = "Not yours")
        createNote(mine, title = "Mine")

        val client = createClient { install(ContentNegotiation) { json() } }
        val listed: List<NoteResponseDTO> = client.get("${ApiVersion.BASE}/notes") {
            header(HttpHeaders.Authorization, "Bearer $mine")
        }.body()

        assertFalse(listed.any { it.id == hidden.id }, "another account's row must not appear in the list")
    }

    @Test
    fun `another account's note is not found rather than forbidden`() = notesTest {
        val mine = signedIn()
        val theirs = signedIn()
        val hidden = createNote(theirs)

        val client = createClient { install(ContentNegotiation) { json() } }
        val response = client.get("${ApiVersion.BASE}/notes/${hidden.id}") {
            header(HttpHeaders.Authorization, "Bearer $mine")
        }
        assertEquals(HttpStatusCode.NotFound, response.status, "existence is itself sensitive here")
    }

    @Test
    fun `an update carrying the current tag succeeds and bumps it`() = notesTest {
        val token = signedIn()
        val created = createNote(token)

        val client = createClient { install(ContentNegotiation) { json() } }
        val response = client.put("${ApiVersion.BASE}/notes/${created.id}") {
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.IfMatch, created.etag)
            contentType(ContentType.Application.Json)
            setBody(UpdateNoteRequestDTO(title = "Buy oat milk", content = "one litre"))
        }
        assertEquals(HttpStatusCode.OK, response.status)
        val updated: NoteResponseDTO = response.body()
        assertEquals("Buy oat milk", updated.title)
        assertNotEquals(created.etag, updated.etag, "the tag has to move or a stale write would pass")
    }

    @Test
    fun `the second of two writers is refused and gets the current row back`() = notesTest {
        val token = signedIn()
        val created = createNote(token)
        val client = createClient { install(ContentNegotiation) { json() } }

        client.put("${ApiVersion.BASE}/notes/${created.id}") {
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.IfMatch, created.etag)
            contentType(ContentType.Application.Json)
            setBody(UpdateNoteRequestDTO(title = "First", content = "won"))
        }

        val second = client.put("${ApiVersion.BASE}/notes/${created.id}") {
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.IfMatch, created.etag) // the tag they read before the first write landed
            contentType(ContentType.Application.Json)
            setBody(UpdateNoteRequestDTO(title = "Second", content = "lost"))
        }
        assertEquals(HttpStatusCode.PreconditionFailed, second.status)
        assertEquals("First", second.body<NoteResponseDTO>().title, "the loser is handed what is actually stored")
    }

    @Test
    fun `an update with no precondition is refused`() = notesTest {
        val token = signedIn()
        val created = createNote(token)

        val client = createClient { install(ContentNegotiation) { json() } }
        val response = client.put("${ApiVersion.BASE}/notes/${created.id}") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(UpdateNoteRequestDTO(title = "No tag", content = "no"))
        }
        assertEquals(PreconditionRequired, response.status)
    }

    // `*` matches whenever the row exists, so honouring it would be last-write-wins wearing the
    // precondition's uniform — it has to read as no precondition at all.
    @Test
    fun `an update preconditioned on star is refused`() = notesTest {
        val token = signedIn()
        val created = createNote(token)

        val client = createClient { install(ContentNegotiation) { json() } }
        val response = client.put("${ApiVersion.BASE}/notes/${created.id}") {
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.IfMatch, "*")
            contentType(ContentType.Application.Json)
            setBody(UpdateNoteRequestDTO(title = "Star", content = "no"))
        }
        assertEquals(PreconditionRequired, response.status)
    }

    // If-Match compares strongly, so a weak tag can never match — and a gzipping proxy is what turns a
    // strong tag weak in transit, which is the case this guards.
    @Test
    fun `an update preconditioned on a weak tag is refused`() = notesTest {
        val token = signedIn()
        val created = createNote(token)

        val client = createClient { install(ContentNegotiation) { json() } }
        val response = client.put("${ApiVersion.BASE}/notes/${created.id}") {
            header(HttpHeaders.Authorization, "Bearer $token")
            header(HttpHeaders.IfMatch, "W/${created.etag}")
            contentType(ContentType.Application.Json)
            setBody(UpdateNoteRequestDTO(title = "Weak", content = "no"))
        }
        assertEquals(
            PreconditionRequired,
            response.status,
            "a proxy that weakened the tag must not turn a conditional write unconditional",
        )
    }

    @Test
    fun `a blank title is rejected before anything is written`() = notesTest {
        val token = signedIn()
        val client = createClient { install(ContentNegotiation) { json() } }
        val response = client.post("${ApiVersion.BASE}/notes") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(CreateNoteRequestDTO(title = "   ", content = "body"))
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }

    @Test
    fun `notes without a token are unauthorized`() = notesTest {
        assertEquals(HttpStatusCode.Unauthorized, client.get("${ApiVersion.BASE}/notes").status)
    }

    @Test
    fun `notes with a malformed token are unauthorized`() = notesTest {
        val response = client.get("${ApiVersion.BASE}/notes") {
            header(HttpHeaders.Authorization, "Bearer not-a-jwt")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `deleting my note removes it, and deleting another account's does not`() = notesTest {
        val mine = signedIn()
        val theirs = signedIn()
        val hidden = createNote(theirs)
        val own = createNote(mine)
        val client = createClient { install(ContentNegotiation) { json() } }

        assertEquals(
            HttpStatusCode.NotFound,
            client.delete("${ApiVersion.BASE}/notes/${hidden.id}") {
                header(HttpHeaders.Authorization, "Bearer $mine")
            }.status,
        )
        assertEquals(
            HttpStatusCode.NoContent,
            client.delete("${ApiVersion.BASE}/notes/${own.id}") {
                header(HttpHeaders.Authorization, "Bearer $mine")
            }.status,
        )
    }

    // Art 17 is only satisfied if erasure reaches what the account owns, so the cascade is asserted
    // rather than assumed from the schema.
    @Test
    fun `deleting the account takes its notes with it`() = notesTest {
        val token = signedIn()
        val created = createNote(token)
        val client = createClient { install(ContentNegotiation) { json() } }

        assertEquals(
            HttpStatusCode.NoContent,
            client.delete("${ApiVersion.BASE}/users/me") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.status,
        )

        val orphans = transaction {
            NotesTable.selectAll().where { NotesTable.id eq created.id }.count()
        }
        assertEquals(0L, orphans, "the note has to go with the account, not outlive it")
    }
}
