package com.mk.kmpshowcase.server

import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.server.config.DatabaseConfig
import com.mk.kmpshowcase.server.core.mail.MailConfig
import com.mk.kmpshowcase.server.core.security.JwtConfig
import com.mk.kmpshowcase.server.di.AppDependencies
import com.mk.kmpshowcase.server.feature.lead.persistence.LeadRepositoryImpl
import com.mk.kmpshowcase.server.feature.lead.service.LeadArtifactStage
import com.mk.kmpshowcase.server.feature.lead.service.LeadDraft
import com.mk.kmpshowcase.server.feature.lead.service.LeadStatus
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

class EngagementRoutesTest {

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

    private fun seedLead(email: String, status: LeadStatus) = runBlocking {
        val repo = LeadRepositoryImpl()
        repo.create(LeadDraft(email, "Fintech", listOf("iOS"), listOf("Auth"), "Jana", null, null, true, null))
        repo.updateStatus(email, status)
        repo
    }

    private fun engagementTest(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
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
    fun `client with no lead gets 404`() = engagementTest {
        val clientToken = token("noeng-${UUID.randomUUID()}@test.com", Role.CLIENT)
        val res = client.get("${ApiVersion.BASE}/me/engagement") {
            header(HttpHeaders.Authorization, "Bearer $clientToken")
        }
        assertEquals(HttpStatusCode.NotFound, res.status)
    }

    @Test
    fun `engagement returns 401 without a token`() = engagementTest {
        assertEquals(HttpStatusCode.Unauthorized, client.get("${ApiVersion.BASE}/me/engagement").status)
    }

    @Test
    fun `client sees released demo + proposal but not unreleased demo`() = engagementTest {
        val jsonClient = createClient { install(ContentNegotiation) { json() } }
        val email = "eng-${UUID.randomUUID()}@test.com"
        val repo = seedLead(email, LeadStatus.PROPOSAL_SENT)
        runBlocking { repo.upsertArtifact(email, LeadArtifactStage.PROPOSAL, "Your proposal body") }
        val adminToken = token("admin-${UUID.randomUUID()}@test.com", Role.ADMIN)

        fun admin(path: String, body: String) = runBlocking {
            jsonClient.post("${ApiVersion.BASE}/admin/leads/$email/$path") {
                header(HttpHeaders.Authorization, "Bearer $adminToken")
                contentType(ContentType.Application.Json); setBody(body)
            }.status
        }
        assertEquals(HttpStatusCode.Created, admin("milestones", """{"title":"Design","status":"DONE","position":0}"""))
        assertEquals(HttpStatusCode.Created, admin("demos", """{"title":"Beta","url":"https://x","released":true}"""))
        assertEquals(HttpStatusCode.Created, admin("demos", """{"title":"WIP","url":"https://y","released":false}"""))

        val clientToken = token(email, Role.CLIENT)
        val res = jsonClient.get("${ApiVersion.BASE}/me/engagement") {
            header(HttpHeaders.Authorization, "Bearer $clientToken")
        }
        assertEquals(HttpStatusCode.OK, res.status)
        val body = res.bodyAsText()
        assertTrue(body.contains("PROPOSAL_READY"), "stage mapped to client-safe value")
        assertTrue(body.contains("Your proposal body"), "proposal visible once sent")
        assertTrue(body.contains("Design"), "milestone present")
        assertTrue(body.contains("Beta"), "released demo present")
        assertFalse(body.contains("WIP"), "unreleased demo must NOT reach the client")
    }

    @Test
    fun `admin client-preview returns the client projection and non-admin is forbidden`() = engagementTest {
        val jsonClient = createClient { install(ContentNegotiation) { json() } }
        val email = "prev-${UUID.randomUUID()}@test.com"
        val repo = seedLead(email, LeadStatus.PROPOSAL_SENT)
        runBlocking { repo.upsertArtifact(email, LeadArtifactStage.PROPOSAL, "Preview proposal") }

        val adminToken = token("padmin-${UUID.randomUUID()}@test.com", Role.ADMIN)
        val ok = jsonClient.get("${ApiVersion.BASE}/admin/leads/$email/client-preview") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
        }
        assertEquals(HttpStatusCode.OK, ok.status)
        assertTrue(ok.bodyAsText().contains("Preview proposal"), "admin sees the client projection")

        val clientToken = token("pcli-${UUID.randomUUID()}@test.com", Role.CLIENT)
        val forbidden = jsonClient.get("${ApiVersion.BASE}/admin/leads/$email/client-preview") {
            header(HttpHeaders.Authorization, "Bearer $clientToken")
        }
        assertEquals(HttpStatusCode.Forbidden, forbidden.status)
    }
}
