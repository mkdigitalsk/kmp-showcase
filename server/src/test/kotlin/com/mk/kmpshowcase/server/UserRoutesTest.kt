package com.mk.kmpshowcase.server

import com.mk.kmpshowcase.contracts.ApiVersion
import com.mk.kmpshowcase.contracts.user.UserResponseDTO
import com.mk.kmpshowcase.server.config.DatabaseConfig
import com.mk.kmpshowcase.server.core.mail.MailConfig
import com.mk.kmpshowcase.server.core.security.JwtConfig
import com.mk.kmpshowcase.server.di.AppDependencies
import com.mk.kmpshowcase.server.feature.user.persistence.UserRepositoryImpl
import com.mk.kmpshowcase.server.feature.user.service.Role
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

class UserRoutesTest {

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

    private fun createUser(role: Role): Pair<String, String> = runBlocking {
        val email = "test-${UUID.randomUUID()}@test.com"
        val user = UserRepositoryImpl().create(email, "password123", "Test User", role)
        val token = jwtConfig.generateToken(user.id, user.email, user.role.name)
        email to token
    }

    private fun usersTest(block: suspend ApplicationTestBuilder.() -> Unit) = testApplication {
        environment { config = MapApplicationConfig() }
        application {
            configureSerialization()
            configureStatusPages()
            configureAuth(jwtConfig)
            configureRouting(AppDependencies(jwtConfig, MailConfig("", 0, "", "", "", "", "")))
        }
        block()
    }

    @Test
    fun `client cannot see admins in the users list`() = usersTest {
        val jsonClient = createClient { install(ContentNegotiation) { json() } }

        val (adminEmail, _) = createUser(Role.ADMIN)
        val (clientEmail, clientToken) = createUser(Role.CLIENT)

        val response = jsonClient.get("${ApiVersion.BASE}/users") {
            header(HttpHeaders.Authorization, "Bearer $clientToken")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val emails = response.body<List<UserResponseDTO>>().map { it.email }
        assertFalse(emails.contains(adminEmail), "CLIENT must not see ADMIN accounts")
        assertTrue(emails.contains(clientEmail), "CLIENT still sees peer clients")
    }

    @Test
    fun `admin sees both clients and admins`() = usersTest {
        val jsonClient = createClient { install(ContentNegotiation) { json() } }

        val (adminEmail, adminToken) = createUser(Role.ADMIN)
        val (clientEmail, _) = createUser(Role.CLIENT)

        val response = jsonClient.get("${ApiVersion.BASE}/users") {
            header(HttpHeaders.Authorization, "Bearer $adminToken")
        }

        assertEquals(HttpStatusCode.OK, response.status)
        val emails = response.body<List<UserResponseDTO>>().map { it.email }
        assertTrue(emails.contains(adminEmail), "ADMIN sees admins")
        assertTrue(emails.contains(clientEmail), "ADMIN sees clients")
    }

    @Test
    fun `get users without auth returns unauthorized`() = usersTest {
        val response = client.get("${ApiVersion.BASE}/users")
        assertEquals(HttpStatusCode.Unauthorized, response.status)
    }

    @Test
    fun `login with missing fields returns 400 not 500`() = usersTest {
        val response = client.post("${ApiVersion.BASE}/auth/login") {
            contentType(ContentType.Application.Json)
            setBody("{}")
        }
        assertEquals(HttpStatusCode.BadRequest, response.status)
    }
}
