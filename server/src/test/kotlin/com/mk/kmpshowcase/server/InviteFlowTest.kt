package com.mk.kmpshowcase.server

import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.server.config.DatabaseConfig
import com.mk.kmpshowcase.server.core.mail.MailConfig
import com.mk.kmpshowcase.server.core.mail.Mailer
import com.mk.kmpshowcase.server.core.security.JwtConfig
import com.mk.kmpshowcase.server.di.AppDependencies
import com.mk.kmpshowcase.server.feature.user.persistence.InviteRepositoryImpl
import com.mk.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import com.mk.kmpshowcase.server.feature.user.service.InviteService
import com.mk.kmpshowcase.server.feature.user.service.Role
import com.mk.kmpshowcase.server.feature.user.service.UserService
import com.mk.kmpshowcase.server.plugins.configureAuth
import com.mk.kmpshowcase.server.plugins.configureRateLimit
import com.mk.kmpshowcase.server.plugins.configureRouting
import com.mk.kmpshowcase.server.plugins.configureSerialization
import com.mk.kmpshowcase.server.plugins.configureStatusPages
import io.ktor.client.request.header
import io.ktor.client.request.post
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.runBlocking
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class InviteFlowTest {

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

    private val noopMailer = object : Mailer {
        override suspend fun send(to: String, subject: String, text: String, html: String?, replyTo: String?) = Unit
    }

    // Same H2 as the test app — lets a test mint an invite token without exposing it over HTTP.
    private fun inviteService(): InviteService {
        val users = UserRepositoryImpl()
        return InviteService(
            InviteRepositoryImpl(), users, UserService(users), noopMailer,
            "https://test.local", CoroutineScope(SupervisorJob()),
        )
    }

    private fun token(email: String, role: Role): String = runBlocking {
        val user = UserRepositoryImpl().create(email, "password123", "Test User", role)
        jwtConfig.generateToken(user.id, user.email, user.role.name)
    }

    private fun inviteTest(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
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
    fun `invite requires admin`() = inviteTest {
        val clientToken = token("inv-cli-${UUID.randomUUID()}@test.com", Role.CLIENT)
        val res = client.post("${ApiVersion.BASE}/admin/clients/newclient%40test.com/invite") {
            header(HttpHeaders.Authorization, "Bearer $clientToken")
        }
        assertEquals(HttpStatusCode.Forbidden, res.status)
    }

    @Test
    fun `admin invites and re-invites without leaking the token`() = inviteTest {
        val adminToken = token("inv-adm-${UUID.randomUUID()}@test.com", Role.ADMIN)
        val email = "invitee-${UUID.randomUUID()}@test.com".replace("@", "%40")
        val res = client.post("${ApiVersion.BASE}/admin/clients/$email/invite") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
        }
        assertEquals(HttpStatusCode.NoContent, res.status)
        assertTrue(res.bodyAsText().isEmpty(), "token must never leave via the API")
        // re-invite replaces the pending token
        assertEquals(
            HttpStatusCode.NoContent,
            client.post("${ApiVersion.BASE}/admin/clients/$email/invite") {
                header(HttpHeaders.Authorization, "Bearer $adminToken")
            }.status,
        )
    }

    @Test
    fun `accept with garbage token returns 400`() = inviteTest {
        val res = client.post("${ApiVersion.BASE}/auth/accept-invite") {
            contentType(ContentType.Application.Json)
            setBody("""{"token":"not-a-real-token","password":"Str0ng!Pass"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, res.status)
    }

    @Test
    fun `accepted invite creates the client who can then log in`() = inviteTest {
        val email = "accept-${UUID.randomUUID()}@test.com"
        val raw = runBlocking { inviteService().invite(email, "Jane Client", "sk-SK") }

        val accepted = client.post("${ApiVersion.BASE}/auth/accept-invite") {
            contentType(ContentType.Application.Json)
            setBody("""{"token":"$raw","password":"Str0ng!Pass"}""")
        }
        assertEquals(HttpStatusCode.Created, accepted.status)
        assertTrue(accepted.bodyAsText().contains(email), "auto-login response carries the user")

        val login = client.post("${ApiVersion.BASE}/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("""{"email":"$email","password":"Str0ng!Pass"}""")
        }
        assertEquals(HttpStatusCode.OK, login.status)

        // token is single-use
        val reuse = client.post("${ApiVersion.BASE}/auth/accept-invite") {
            contentType(ContentType.Application.Json)
            setBody("""{"token":"$raw","password":"Str0ng!Pass"}""")
        }
        assertEquals(HttpStatusCode.BadRequest, reuse.status)
    }
}
