package sk.mkdigital.kmpshowcase.server

import sk.mkdigital.kmpshowcase.contracts.ApiVersion
import sk.mkdigital.kmpshowcase.server.config.DatabaseConfig
import sk.mkdigital.kmpshowcase.server.core.security.JwtConfig
import sk.mkdigital.kmpshowcase.server.di.AppDependencies
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import sk.mkdigital.kmpshowcase.server.feature.user.persistence.UsersTable
import org.jetbrains.exposed.v1.core.eq
import org.jetbrains.exposed.v1.jdbc.selectAll
import org.jetbrains.exposed.v1.jdbc.transactions.suspendTransaction
import org.jetbrains.exposed.v1.jdbc.update
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
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
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
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
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

    private fun createUser(email: String = "dana-${UUID.randomUUID()}@example.com"): Pair<String, String> =
        runBlocking {
            val user = UserRepositoryImpl().create(email, PASSWORD)
            val token = jwtConfig.generateToken(user.id, user.email)
            email to token
        }

    private suspend fun ApplicationTestBuilder.pickLanguage(token: String, body: String): HttpResponse =
        client.put("${ApiVersion.BASE}/users/me/locale") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(body)
        }

    private suspend fun ApplicationTestBuilder.me(token: String): HttpResponse =
        client.get("${ApiVersion.BASE}/users/me") { header(HttpHeaders.Authorization, "Bearer $token") }

    private fun usersTest(
        proxyKey: String? = null,
        block: suspend ApplicationTestBuilder.() -> Unit,
    ) = testApplication {
        environment { config = MapApplicationConfig() }
        application {
            configureSerialization()
            configureStatusPages()
            configureAuth(jwtConfig)
            configureRateLimit(proxyKey)
            configureRouting(AppDependencies(jwtConfig))
        }
        block()
    }

    @Test
    fun `sign up answers a new account's language by its key`() = usersTest {
        val signedUp = client.post("${ApiVersion.BASE}/auth/sign-up") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"dana-${UUID.randomUUID()}@example.com","password":"Password1@"}""")
        }

        assertEquals(HttpStatusCode.Created, signedUp.status, signedUp.bodyAsText())
        assertLanguageKey("EN", accountOf(signedUp))
    }

    @Test
    fun `sign in answers the account's language by its key`() = usersTest {
        val (email, token) = createUser()
        assertEquals(HttpStatusCode.OK, pickLanguage(token, """{"language":"SK"}""").status)

        val signedIn = client.post("${ApiVersion.BASE}/auth/sign-in") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","password":"$PASSWORD"}""")
        }

        assertEquals(HttpStatusCode.OK, signedIn.status, signedIn.bodyAsText())
        assertLanguageKey("SK", accountOf(signedIn))
    }

    @Test
    fun `the signed in user carries the account's language by its key`() = usersTest {
        val (_, token) = createUser()
        assertLanguageKey("EN", bodyOf(me(token)))

        val picked = pickLanguage(token, """{"language":"SK"}""")

        assertEquals(HttpStatusCode.OK, picked.status, picked.bodyAsText())
        assertLanguageKey("SK", bodyOf(picked))
        assertLanguageKey("SK", bodyOf(me(token)))
    }

    @Test
    fun `an account's language is stored by its key`() = usersTest {
        val (email, token) = createUser()
        assertEquals("EN", StoredAccounts.languageOf(email), "a new account")

        pickLanguage(token, """{"language":"SK"}""")

        assertEquals("SK", StoredAccounts.languageOf(email), "after the account's pick")
    }

    @Test
    fun `a language tag stored before keys reads as its language, and any other as English`() = usersTest {
        val (email, token) = createUser()

        for ((stored, key) in STORED_LANGUAGES) {
            StoredAccounts.storeLanguage(email, stored)

            assertLanguageKey(key, bodyOf(me(token)), "stored '$stored'")
        }
    }

    @Test
    fun `a language pick outside the set is refused`() = usersTest {
        val (email, token) = createUser()

        for (body in OUTSIDE_THE_SET) {
            assertEquals(HttpStatusCode.BadRequest, pickLanguage(token, body).status, body)
        }
        assertEquals("EN", StoredAccounts.languageOf(email), "no refused pick reached the account")
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
    fun `an account that predates the activity column is not treated as inactive`() = usersTest {
        val (email, _) = createUser()
        runBlocking {
            suspendTransaction { UsersTable.update({ UsersTable.email eq email }) { it[lastSeenAt] = 0 } }
        }

        DatabaseConfig.init(MapApplicationConfig("database.useH2" to "true"))

        val lastSeen = runBlocking {
            suspendTransaction {
                UsersTable.selectAll().where { UsersTable.email eq email }.single()[UsersTable.lastSeenAt]
            }
        }
        assertTrue(lastSeen > 0, "a zero here is older than every cutoff, so the first sweep would delete them all")
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
            setBody("""{"email":"dana-${UUID.randomUUID()}@example.com","password":"Password1@"}""")
        }

        assertTrue(
            "\"demo\"" in response.bodyAsText(),
            "a client that does not share the contract cannot supply the default it never received",
        )
    }

    /**
     * ignoreUnknownKeys drops an extra field rather than rejecting it, so the request DTO's shape is the whole
     * boundary — what it does not declare cannot be bound.
     */
    @Test
    fun `an unknown field in the sign up body cannot reach the account`() = usersTest {
        val jsonClient = createClient { install(ContentNegotiation) { json() } }
        val email = "dana-${UUID.randomUUID()}@example.com"
        val signedUp = jsonClient.post("${ApiVersion.BASE}/auth/sign-up") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","password":"Password1@"}""")
        }
        assertEquals(HttpStatusCode.Created, signedUp.status)

        val extra = jsonClient.post("${ApiVersion.BASE}/auth/sign-up") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"dana-${UUID.randomUUID()}@example.com","password":"Password1@","isAdmin":true}""")
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
    fun `repeated sign in attempts are rate limited with 429`() = usersTest {
        val statuses = (1..SIGN_IN_ATTEMPTS).map {
            client.post("${ApiVersion.BASE}/auth/sign-in") {
                contentType(ContentType.Application.Json)
                setBody("""{"email":"dana@example.com","password":"wrong"}""")
            }.status
        }
        assertTrue(
            statuses.contains(HttpStatusCode.TooManyRequests),
            "sign-in must return 429 once the per-IP rate limit is exceeded",
        )
    }

    @Test
    fun `visitors behind our own proxy are counted apart`() = usersTest(proxyKey = PROXY_KEY) {
        val statuses = (1..SIGN_IN_ATTEMPTS).map { visitor ->
            client.post("${ApiVersion.BASE}/auth/sign-in") {
                header("X-Visitor-IP", "203.0.113.$visitor")
                header("X-Proxy-Key", PROXY_KEY)
                contentType(ContentType.Application.Json)
                setBody("""{"email":"dana@example.com","password":"wrong"}""")
            }.status
        }
        assertTrue(
            statuses.none { it == HttpStatusCode.TooManyRequests },
            "each visitor named by our proxy must have a bucket of their own",
        )
    }

    @Test
    fun `a visitor header without the proxy key counts on the connection`() = usersTest(proxyKey = PROXY_KEY) {
        val statuses = (1..SIGN_IN_ATTEMPTS).map { visitor ->
            client.post("${ApiVersion.BASE}/auth/sign-in") {
                header("X-Visitor-IP", "203.0.113.$visitor")
                header("X-Proxy-Key", "a-guess")
                contentType(ContentType.Application.Json)
                setBody("""{"email":"dana@example.com","password":"wrong"}""")
            }.status
        }
        assertTrue(
            statuses.contains(HttpStatusCode.TooManyRequests),
            "a caller who cannot present the proxy key must not choose its own bucket",
        )
    }
}

private const val SIGN_IN_ATTEMPTS = 15

private const val PROXY_KEY = "test-proxy-key"

private const val PASSWORD = "password123"

/**
 * What an account's language column may hold, beside the key the account reads as:
 * - the key
 * - the language's code
 * - a tag with its region
 * - a tag as a platform writes it, with an underscore
 * - a tag as a caller sent it, its case and spaces kept
 * - a language the apps do not speak
 * - a code that begins as Slovak's but names another language
 * - a blank
 * - a word that names no language, as the free field let through
 */
private val STORED_LANGUAGES = listOf(
    "SK" to "SK",
    "EN" to "EN",
    "sk" to "SK",
    "en" to "EN",
    "sk-SK" to "SK",
    "en-GB" to "EN",
    "sk_SK" to "SK",
    "en_US" to "EN",
    " Sk-sk " to "SK",
    "fr-FR" to "EN",
    "hu" to "EN",
    "skx" to "EN",
    "" to "EN",
    "constructor" to "EN",
)

/**
 * A language pick's body the server refuses:
 * - a tag in place of the key
 * - the code in place of the key
 * - a language the apps do not speak
 * - a word that names no language
 * - the free tag under the name it was sent by before keys were
 * - no language at all
 */
private val OUTSIDE_THE_SET = listOf(
    """{"language":"sk-SK"}""",
    """{"language":"sk"}""",
    """{"language":"hu"}""",
    """{"language":"constructor"}""",
    """{"locale":"sk-SK"}""",
    "{}",
)

private suspend fun bodyOf(response: HttpResponse): JsonObject =
    Json.parseToJsonElement(response.bodyAsText()).jsonObject

/** The account an auth answer carries. */
private suspend fun accountOf(response: HttpResponse): JsonObject = bodyOf(response).getValue("user").jsonObject

/** The account answers [key] as its language, with no free tag beside it. */
private fun assertLanguageKey(key: String, account: JsonObject, message: String? = null) {
    assertEquals(JsonPrimitive(key), account["language"], message)
    assertFalse("locale" in account, listOfNotNull("a free tag beside the key", message).joinToString(", "))
}
