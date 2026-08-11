package sk.mkdigital.kmpshowcase.data.client

import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.HttpRequestData
import io.ktor.http.HttpMethod
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.test.runTest
import sk.mkdigital.kmpshowcase.BuildType
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
