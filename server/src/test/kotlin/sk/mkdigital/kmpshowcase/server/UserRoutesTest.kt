package sk.mkdigital.kmpshowcase.server

import sk.mkdigital.kmpshowcase.contracts.ApiVersion
import sk.mkdigital.kmpshowcase.server.config.DatabaseConfig
import sk.mkdigital.kmpshowcase.server.core.security.JwtConfig
import sk.mkdigital.kmpshowcase.server.di.AppDependencies
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import sk.mkdigital.kmpshowcase.server.plugins.configureAuth
import sk.mkdigital.kmpshowcase.server.plugins.configureRateLimit
import sk.mkdigital.kmpshowcase.server.plugins.configureRouting
import sk.mkdigital.kmpshowcase.server.plugins.configureSerialization
import sk.mkdigital.kmpshowcase.server.plugins.configureStatusPages
import io.ktor.client.call.body
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.statement.bodyAsText
import io.ktor.client.request.delete
import io.ktor.client.request.header
import io.ktor.client.request.get
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

class UserRoutesTest {

    companion object {
        private var initialized = false
        private val jwtConfig = JwtConfig(
            secret = "test-secret-at-least-32-bytes-long-for-hs256",
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

    private fun createUser(email: String = "test-${UUID.randomUUID()}@test.com"): Pair<String, String> = runBlocking {
        val user = UserRepositoryImpl().create(email, "password123")
        val token = jwtConfig.generateToken(user.id, user.email)
        email to token
    }

    private fun usersTest(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
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

    @Test
    fun `deleting my account removes it and the token stops working`() = usersTest {
        val (_, token) = createUser()

        val deleted = client.delete("${ApiVersion.BASE}/users/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.NoContent, deleted.status)

        val afterwards = client.get("${ApiVersion.BASE}/users/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.NotFound, afterwards.status, "the row is gone, so the token resolves to nobody")
    }

    @Test
    fun `deleting an account that is already gone still answers no content`() = usersTest {
        val (_, token) = createUser()

        repeat(2) {
            val response = client.delete("${ApiVersion.BASE}/users/me") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }
            assertEquals(
                HttpStatusCode.NoContent,
                response.status,
                "a 404 here reads to a client exactly like a route that does not exist",
            )
        }
    }

    @Test
    fun `a demo account refuses to be deleted`() = usersTest {
        val (_, token) = createUser(email = "test01@mkdigital.sk")

        val response = client.delete("${ApiVersion.BASE}/users/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }

        assertEquals(
            HttpStatusCode.Forbidden,
            response.status,
            "the sign-in screen hands this account out, so any visitor could delete everyone's demo",
        )
        val afterwards = client.get("${ApiVersion.BASE}/users/me") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        assertEquals(HttpStatusCode.OK, afterwards.status, "the account is still there")
    }

    @Test
    fun `deleting an account without auth returns unauthorized`() = usersTest {
        val response = client.delete("${ApiVersion.BASE}/users/me")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `deleting an account with a malformed token returns unauthorized`() = usersTest {
        val response = client.delete("${ApiVersion.BASE}/users/me") {
            header(HttpHeaders.Authorization, "Bearer not-a-jwt")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `sign in with missing fields returns 400 as RFC 9457 problem detail`() = usersTest {
        val response = client.post("${ApiVersion.BASE}/auth/sign-in") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
        assertTrue(
            response.headers[HttpHeaders.ContentType]?.startsWith("application/problem+json") == true,
            "error body must be served as application/problem+json (RFC 9457)",
        )
        val body = response.bodyAsText()
        assertTrue(body.contains("\"status\":400"), "problem detail carries the status")
        assertTrue(body.contains("\"title\""), "problem detail carries a title")
        assertTrue(body.contains("\"detail\""), "problem detail carries a detail")
    }

    @Test
    fun `a field equal to its default is still on the wire`() = usersTest {
        val jsonClient = createClient { install(ContentNegotiation) { json() } }

        val response = jsonClient.post("${ApiVersion.BASE}/auth/sign-up") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"wire-${UUID.randomUUID()}@test.com","password":"Password1@"}""")
        }

        assertTrue(
            "\"demo\"" in response.bodyAsText(),
            "a client that does not share the contract cannot supply the default it never received",
        )
    }

    @Test
    fun `an unknown field in the sign-up body cannot reach the account`() = usersTest {
        val jsonClient = createClient { install(ContentNegotiation) { json() } }
        val email = "signup-${UUID.randomUUID()}@test.com"
        val signedUp = jsonClient.post("${ApiVersion.BASE}/auth/sign-up") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","password":"Password1@"}""")
        }
        assertEquals(HttpStatusCode.Created, signedUp.status)

        // ignoreUnknownKeys drops an extra field rather than rejecting it, so the request DTO's shape is
        // the whole boundary — what it does not declare cannot be bound.
        val extra = jsonClient.post("${ApiVersion.BASE}/auth/sign-up") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"x-${UUID.randomUUID()}@test.com","password":"Password1@","isAdmin":true}""")
        }
        assertEquals(HttpStatusCode.Created, extra.status)
        assertTrue("isAdmin" !in extra.bodyAsText(), "an unbound field must not survive into the account")
    }

    @Test
    fun `sign in with a wrong password returns 401`() = usersTest {
        val (email, _) = createUser()
        val response = client.post("${ApiVersion.BASE}/auth/sign-in") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","password":"wrong-password"}""")
        }
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `repeated sign-in attempts are rate limited with 429`() = usersTest {
        val statuses = (1..SIGN_IN_ATTEMPTS).map {
            client.post("${ApiVersion.BASE}/auth/sign-in") {
                contentType(ContentType.Application.Json)
                setBody("""{"email":"spray@test.com","password":"wrong"}""")
            }.status
        }
        assertTrue(
            statuses.contains(HttpStatusCode.TooManyRequests),
            "sign-in must return 429 once the per-IP rate limit is exceeded",
        )
    }
}

private const val SIGN_IN_ATTEMPTS = 15
