package sk.mkdigital.kmpshowcase.data.network

import sk.mkdigital.kmpshowcase.BuildType
import sk.mkdigital.kmpshowcase.util.Logger
import io.ktor.client.HttpClient
import io.ktor.client.engine.mock.MockEngine
import io.ktor.client.engine.mock.respond
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.headersOf
import io.ktor.http.ContentType
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertContains
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

private const val PASSWORD = "correct-horse-battery-staple"
private const val BEARER = "eyJhbGciOiJIUzI1NiJ9.the-bearer.signature"

private class RecordingLogger : Logger {
    val lines = mutableListOf<String>()
    override fun e(log: String) { lines += log }
    override fun e(e: Throwable) { lines += e.toString() }
    override fun e(log: String, e: Throwable) { lines += log }
    override fun d(log: String) { lines += log }
}

private fun client(
    buildType: BuildType,
    logger: Logger,
    responseBody: String = """{"token":"$BEARER"}""",
) = HttpClient(MockEngine) {
    applyCommonConfig("example.invalid", buildType, logger)
    engine {
        addHandler {
            respond(
                content = responseBody,
                status = HttpStatusCode.OK,
                headers = headersOf(HttpHeaders.ContentType, ContentType.Application.Json.toString()),
            )
        }
    }
}

class HttpClientLoggingTest {

    @Test
    fun `sign-in never reaches the log, in either build`() = runTest {
        for (buildType in BuildType.entries) {
            val logger = RecordingLogger()
            client(buildType, logger).post("auth/sign-in") { setBody("""{"password":"$PASSWORD"}""") }

            val logged = logger.lines.joinToString("\n")
            assertFalse(logged.contains(PASSWORD), "$buildType logged the password: $logged")
            assertFalse(logged.contains(BEARER), "$buildType logged the bearer: $logged")
        }
    }

    @Test
    fun `a release build logs nothing at all`() = runTest {
        val logger = RecordingLogger()
        client(BuildType.RELEASE, logger).post("notes") { setBody("""{"title":"a note"}""") }

        assertEquals(emptyList(), logger.lines)
    }

    @Test
    fun `a debug build logs an ordinary call, and never the bearer carrying it`() = runTest {
        val logger = RecordingLogger()
        client(BuildType.DEBUG, logger, responseBody = """{"id":1}""").post("notes") {
            header(HttpHeaders.Authorization, "Bearer $BEARER")
            setBody("""{"title":"a note"}""")
        }

        val logged = logger.lines.joinToString("\n")
        assertContains(logged, "a note")
        assertFalse(logged.contains(BEARER), "the bearer reached the log: $logged")
    }

    @Test
    fun `the response body still arrives when logging is off`() = runTest {
        val body = client(BuildType.RELEASE, RecordingLogger()).post("notes").bodyAsText()

        assertTrue(body.contains(BEARER))
    }
}
