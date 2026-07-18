package com.mk.kmpshowcase.server

import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.server.config.DatabaseConfig
import com.mk.kmpshowcase.server.core.mail.MailConfig
import com.mk.kmpshowcase.server.core.security.JwtConfig
import com.mk.kmpshowcase.server.di.AppDependencies
import com.mk.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import com.mk.kmpshowcase.server.feature.user.service.Role
import com.mk.kmpshowcase.server.plugins.configureAuth
import com.mk.kmpshowcase.server.plugins.configureRateLimit
import com.mk.kmpshowcase.server.plugins.configureRouting
import com.mk.kmpshowcase.server.plugins.configureSerialization
import com.mk.kmpshowcase.server.plugins.configureStatusPages
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.server.config.MapApplicationConfig
import io.ktor.server.testing.ApplicationTestBuilder
import io.ktor.server.testing.testApplication
import java.util.UUID
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class LeadRoutesTest {

    companion object {
        private var initialized = false
        private val jwtConfig = JwtConfig("test-secret", "kmp-showcase", "kmp-showcase-users")
    }

    @BeforeTest
    fun setup() {
        if (!initialized) {
            DatabaseConfig.init(MapApplicationConfig("database.useH2" to "true"))
            initialized = true
        }
    }

    private fun token(email: String, role: Role): String = runBlocking {
        val user = UserRepositoryImpl().create(email, "password123", "Test User", role)
        jwtConfig.generateToken(user.id, user.email, user.role.name)
    }

    // The test MailConfig points nowhere — every submit also proves mail failures never fail the lead.
    private fun leadTest(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
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

    private suspend fun HttpClient.submit(body: String) =
        post("${ApiVersion.BASE}/leads") { contentType(ContentType.Application.Json); setBody(body) }

    private fun leadBody(email: String, note: String? = null) = buildString {
        append("""{"email":"$email","appType":"Fintech app","platforms":["Web"],"hasDoc":true,"hasDesign":true""")
        if (note != null) append(""","note":"$note"""")
        append("}")
    }

    @Test
    fun `valid submit returns 201 even though the mailer is dead`() = leadTest {
        val res = client.submit(leadBody("lead-${UUID.randomUUID()}@test.com"))
        assertEquals(HttpStatusCode.Created, res.status)
        assertTrue(res.bodyAsText().contains("true"))
    }

    @Test
    fun `invalid email returns 400`() = leadTest {
        assertEquals(HttpStatusCode.BadRequest, client.submit(leadBody("not-an-email")).status)
    }

    @Test
    fun `blank appType returns 400`() = leadTest {
        val body = """{"email":"a@b.com","appType":"  "}"""
        assertEquals(HttpStatusCode.BadRequest, client.submit(body).status)
    }

    @Test
    fun `malformed body returns 400 not 500`() = leadTest {
        assertEquals(HttpStatusCode.BadRequest, client.submit("{").status)
    }

    @Test
    fun `sixth submit within the window is rate limited with 429`() = leadTest {
        repeat(5) {
            assertEquals(HttpStatusCode.Created, client.submit(leadBody("rl-$it-${UUID.randomUUID()}@test.com")).status)
        }
        assertEquals(HttpStatusCode.TooManyRequests, client.submit(leadBody("rl-last@test.com")).status)
    }

    @Test
    fun `resubmitting the same email keeps both rows and the latest wins in detail`() = leadTest {
        val email = "dup-${UUID.randomUUID()}@test.com"
        assertEquals(HttpStatusCode.Created, client.submit(leadBody(email, note = "first")).status)
        assertEquals(HttpStatusCode.Created, client.submit(leadBody(email, note = "second")).status)

        val adminToken = token("ladmin-${UUID.randomUUID()}@test.com", Role.ADMIN)
        val detail = client.get("${ApiVersion.BASE}/admin/leads/$email") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
        }
        assertEquals(HttpStatusCode.OK, detail.status)
        val body = detail.bodyAsText()
        assertTrue(body.contains("second"), "latest submission is the lead detail")
        assertFalse(body.contains("first"), "older submission does not leak into the detail")
    }

    @Test
    fun `admin lead list and detail are admin-only`() = leadTest {
        val clientToken = token("lcli-${UUID.randomUUID()}@test.com", Role.CLIENT)
        assertEquals(HttpStatusCode.Unauthorized, client.get("${ApiVersion.BASE}/admin/leads").status)
        assertEquals(
            HttpStatusCode.Forbidden,
            client.get("${ApiVersion.BASE}/admin/leads") {
                header(HttpHeaders.Authorization, "Bearer $clientToken")
            }.status,
        )
        assertEquals(
            HttpStatusCode.Forbidden,
            client.get("${ApiVersion.BASE}/admin/leads/x@y.com") {
                header(HttpHeaders.Authorization, "Bearer $clientToken")
            }.status,
        )
    }

    @Test
    fun `status update validates the enum and 404s unknown emails`() = leadTest {
        val adminToken = token("ladmin-${UUID.randomUUID()}@test.com", Role.ADMIN)
        val email = "st-${UUID.randomUUID()}@test.com"
        client.submit(leadBody(email))

        val bad = client.patch("${ApiVersion.BASE}/admin/leads/$email/status") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
            contentType(ContentType.Application.Json); setBody("""{"status":"NOT_A_STATUS"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, bad.status)

        val unknown = client.patch("${ApiVersion.BASE}/admin/leads/ghost-${UUID.randomUUID()}@test.com/status") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
            contentType(ContentType.Application.Json); setBody("""{"status":"WON"}""")
        }
        assertEquals(HttpStatusCode.NotFound, unknown.status)

        val ok = client.patch("${ApiVersion.BASE}/admin/leads/$email/status") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
            contentType(ContentType.Application.Json); setBody("""{"status":"REVIEWING"}""")
        }
        assertEquals(HttpStatusCode.OK, ok.status)
        assertTrue(ok.bodyAsText().contains("REVIEWING"))
    }

    @Test
    fun `artifact PUT upserts — two writes leave one artifact with the latest content`() = leadTest {
        val adminToken = token("ladmin-${UUID.randomUUID()}@test.com", Role.ADMIN)
        val email = "art-${UUID.randomUUID()}@test.com"
        client.submit(leadBody(email))

        suspend fun putArtifact(content: String) = client.put("${ApiVersion.BASE}/admin/leads/$email/artifacts/ANALYSIS") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
            contentType(ContentType.Application.Json); setBody("""{"content":"$content"}""")
        }
        putArtifact("v1")
        putArtifact("v2")

        val detail = client.get("${ApiVersion.BASE}/admin/leads/$email") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
        }.bodyAsText()
        assertTrue(detail.contains("v2"), "latest artifact content present")
        assertFalse(detail.contains("v1"), "upsert replaced, not appended")
        assertEquals(1, Regex("ANALYSIS").findAll(detail).count(), "single artifact row per stage")
    }

    @Test
    fun `clients list contains only WON leads`() = leadTest {
        val adminToken = token("ladmin-${UUID.randomUUID()}@test.com", Role.ADMIN)
        val newLead = "new-${UUID.randomUUID()}@test.com"
        val wonLead = "won-${UUID.randomUUID()}@test.com"
        client.submit(leadBody(newLead))
        client.submit(leadBody(wonLead))
        client.patch("${ApiVersion.BASE}/admin/leads/$wonLead/status") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
            contentType(ContentType.Application.Json); setBody("""{"status":"WON"}""")
        }

        val clients = client.get("${ApiVersion.BASE}/admin/clients") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
        }.bodyAsText()
        assertTrue(clients.contains(wonLead), "WON lead is a client")
        assertFalse(clients.contains(newLead), "NEW lead is not a client")
    }
}
