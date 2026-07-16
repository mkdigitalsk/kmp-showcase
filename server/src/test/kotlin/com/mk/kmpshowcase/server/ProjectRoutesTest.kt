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
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.get
import io.ktor.client.request.header
import io.ktor.client.request.patch
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
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
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class ProjectRoutesTest {

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

    private fun projectTest(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
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
    fun `client with no project gets 404`() = projectTest {
        val clientToken = token("noproj-${UUID.randomUUID()}@test.com", Role.CLIENT)
        val res = client.get("${ApiVersion.BASE}/me/project") {
            header(HttpHeaders.Authorization, "Bearer $clientToken")
        }
        assertEquals(HttpStatusCode.NotFound, res.status)
    }

    @Test
    fun `project returns 401 without a token`() = projectTest {
        assertEquals(HttpStatusCode.Unauthorized, client.get("${ApiVersion.BASE}/me/project").status)
    }

    @Test
    fun `admin builds a project and client sees it with released demos only`() = projectTest {
        val jsonClient = createClient { install(ContentNegotiation) { json() } }
        val email = "proj-${UUID.randomUUID()}@test.com"
        val adminToken = token("padmin-${UUID.randomUUID()}@test.com", Role.ADMIN)

        fun admin(method: String, path: String, body: String) = runBlocking {
            jsonClient.post("${ApiVersion.BASE}/admin/projects/$email$path") {
                header(HttpHeaders.Authorization, "Bearer $adminToken")
                contentType(ContentType.Application.Json); setBody(body)
            }.status
        }
        val start = """{"startDate":1000,"targetEndDate":9000,"health":"AMBER","scope":[{"title":"Native KMP app","detail":"iOS + Android"}],"outOfScope":[{"title":"Web admin"}]}"""
        assertEquals(HttpStatusCode.Created, admin("POST", "", start))
        assertEquals(HttpStatusCode.Created, admin("POST", "/milestones", """{"title":"Design","status":"DONE","plannedDate":2000,"position":0,"acceptanceCriteria":["Figma approved"]}"""))
        assertEquals(HttpStatusCode.Created, admin("POST", "/documents", """{"type":"CONTRACT","title":"Signed contract","url":"https://x/c.pdf"}"""))
        assertEquals(HttpStatusCode.Created, admin("POST", "/demos", """{"title":"Beta","url":"https://x/beta","released":true}"""))
        assertEquals(HttpStatusCode.Created, admin("POST", "/demos", """{"title":"WIP","url":"https://x/wip","released":false}"""))
        assertEquals(HttpStatusCode.Created, admin("POST", "/payments", """{"label":"Milestone 1","amountCents":500000,"currency":"EUR","status":"PAID","position":0}"""))

        val clientToken = token(email, Role.CLIENT)
        val res = jsonClient.get("${ApiVersion.BASE}/me/project") {
            header(HttpHeaders.Authorization, "Bearer $clientToken")
        }
        assertEquals(HttpStatusCode.OK, res.status)
        val body = res.bodyAsText()
        assertTrue(body.contains("AMBER"), "health present")
        assertTrue(body.contains("Design"), "milestone present")
        assertTrue(body.contains("Native KMP app"), "scope present")
        assertTrue(body.contains("Figma approved"), "acceptance criteria present")
        assertTrue(body.contains("Signed contract"), "document present")
        assertTrue(body.contains("Beta"), "released demo present")
        assertTrue(body.contains("Milestone 1") && body.contains("500000"), "payment present and client-visible")
        assertFalse(body.contains("WIP"), "unreleased demo (or its history event) must NOT reach the client")
        assertTrue(body.contains("STARTED"), "client history records the project start")
        assertFalse(body.contains("DEMO_ADDED"), "granular admin events must NOT reach the client history")
        assertFalse(body.contains("PAYMENT_ADDED"), "granular admin events must NOT reach the client history")
    }

    @Test
    fun `admin updates tooling links, blank clears, client never sees them`() = projectTest {
        val jsonClient = createClient { install(ContentNegotiation) { json() } }
        val email = "links-${UUID.randomUUID()}@test.com"
        val adminToken = token("padmin-${UUID.randomUUID()}@test.com", Role.ADMIN)
        jsonClient.post("${ApiVersion.BASE}/admin/projects/$email") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
            contentType(ContentType.Application.Json); setBody("""{"startDate":1000}""")
        }

        val patched = jsonClient.patch("${ApiVersion.BASE}/admin/projects/$email/links") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
            contentType(ContentType.Application.Json)
            setBody("""{"jiraBoardUrl":"https://x.atlassian.net/board/1","specUrl":"https://x.atlassian.net/wiki/spec","designUrl":"  "}""")
        }
        assertEquals(HttpStatusCode.OK, patched.status)
        val adminBody = patched.bodyAsText()
        assertTrue(adminBody.contains("board/1"), "links visible to admin")
        assertTrue(adminBody.contains(""""designUrl":null"""), "blank link cleared to null")

        val clientToken = token(email, Role.CLIENT)
        val clientBody = jsonClient.get("${ApiVersion.BASE}/me/project") {
            header(HttpHeaders.Authorization, "Bearer $clientToken")
        }.bodyAsText()
        assertFalse(clientBody.contains("board/1"), "internal tooling links must NOT reach the client")

        val forbidden = jsonClient.patch("${ApiVersion.BASE}/admin/projects/$email/links") {
            header(HttpHeaders.Authorization, "Bearer $clientToken")
            contentType(ContentType.Application.Json); setBody("""{}""")
        }
        assertEquals(HttpStatusCode.Forbidden, forbidden.status)
    }

    @Test
    fun `admin client-preview returns projection and non-admin is forbidden`() = projectTest {
        val jsonClient = createClient { install(ContentNegotiation) { json() } }
        val email = "prev-${UUID.randomUUID()}@test.com"
        val adminToken = token("padmin-${UUID.randomUUID()}@test.com", Role.ADMIN)
        jsonClient.post("${ApiVersion.BASE}/admin/projects/$email") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
            contentType(ContentType.Application.Json); setBody("""{"startDate":1000}""")
        }

        val ok = jsonClient.get("${ApiVersion.BASE}/admin/projects/$email/client-preview") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
        }
        assertEquals(HttpStatusCode.OK, ok.status)

        val clientToken = token("pcli-${UUID.randomUUID()}@test.com", Role.CLIENT)
        val forbidden = jsonClient.get("${ApiVersion.BASE}/admin/projects/$email/client-preview") {
            header(HttpHeaders.Authorization, "Bearer $clientToken")
        }
        assertEquals(HttpStatusCode.Forbidden, forbidden.status)
    }
}
