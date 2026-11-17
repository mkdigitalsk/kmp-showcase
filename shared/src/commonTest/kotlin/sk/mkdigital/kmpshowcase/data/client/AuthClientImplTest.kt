package sk.mkdigital.kmpshowcase.data.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import kotlinx.coroutines.test.runTest
import sk.mkdigital.kmpshowcase.BuildType
import sk.mkdigital.kmpshowcase.contracts.LanguageDTO
import sk.mkdigital.kmpshowcase.data.network.applyCommonConfig
import sk.mkdigital.kmpshowcase.domain.exceptions.base.ApiException
import sk.mkdigital.kmpshowcase.fake.NoOpLogger
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFailsWith
import kotlin.test.assertNotNull

class AuthClientImplTest {

    private val requests = mutableListOf<HttpRequestData>()

    private fun clientResponding(status: HttpStatusCode) = AuthClientImpl(
        HttpClient(MockEngine) {
            applyCommonConfig("example.invalid", BuildType.DEBUG, NoOpLogger)
            engine {
                addHandler { request ->
                    requests += request
                    respond(content = "", status = status)
                }
            }
        }
    )

    private fun clientSigningIn(language: String) = AuthClientImpl(
        HttpClient(MockEngine) {
            applyCommonConfig("example.invalid", BuildType.DEBUG, NoOpLogger)
            engine {
                addHandler {
                    respond(
                        content = """{"token":"t0k3n","user":{"id":1,"email":"dana@example.com",""" +
                            """"themeMode":"system","language":"$language"}}""",
                        status = HttpStatusCode.OK,
                        headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
                    )
                }
            }
        }
    )

    @Test
    fun `sign in reads the account language by its key`() = runTest {
        val response = clientSigningIn(language = "SK").signIn("dana@example.com", "Password1@")

        assertEquals(LanguageDTO.SK, response.user.language)
    }

    @Test
    fun `sign in answering a language this build does not know opens in English`() = runTest {
        val response = clientSigningIn(language = "FR").signIn("dana@example.com", "Password1@")

        assertEquals(LanguageDTO.EN, response.user.language)
    }

    @Test
    fun `delete account sends DELETE to the versioned me route`() = runTest {
        clientResponding(HttpStatusCode.NoContent).deleteAccount()

        val request = assertNotNull(requests.singleOrNull())
        assertEquals(HttpMethod.Delete, request.method)
        assertEquals("/v1/users/me", request.url.encodedPath)
    }

    @Test
    fun `delete account fails when the server rejects it`() = runTest {
        val client = clientResponding(HttpStatusCode.InternalServerError)

        val exception = assertFailsWith<ApiException> { client.deleteAccount() }

        assertEquals(500, exception.httpCode)
    }

    @Test
    fun `delete account fails when the caller is unauthorized`() = runTest {
        val client = clientResponding(HttpStatusCode.Unauthorized)

        val exception = assertFailsWith<ApiException> { client.deleteAccount() }

        assertEquals(401, exception.httpCode)
    }
}
