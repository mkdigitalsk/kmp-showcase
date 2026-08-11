package sk.mkdigital.kmpshowcase.data.network

import sk.mkdigital.kmpshowcase.BuildType
import sk.mkdigital.kmpshowcase.contracts.ApiVersion
import sk.mkdigital.kmpshowcase.util.Logger as AppLogger
import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.URLProtocol
import io.ktor.http.contentType
import io.ktor.http.path
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect class HttpClientProvider(baseUrl: String, buildType: BuildType, logger: AppLogger) {
    fun create(): HttpClient
}

fun HttpClientConfig<*>.applyCommonConfig(baseUrl: String, buildType: BuildType, logger: AppLogger) {
    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = baseUrl
            path("${ApiVersion.CURRENT}/")
        }
        contentType(ContentType.Application.Json)
    }
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
                explicitNulls = false
            }
        )
    }

    install(Logging) {
        this.logger = KtorLogger(logger)
        level = if (buildType.isDebug) LogLevel.BODY else LogLevel.NONE
        filter { request -> !request.url.pathSegments.contains(AUTH_PATH_SEGMENT) }
        sanitizeHeader { header -> header == HttpHeaders.Authorization }
    }

    install(HttpTimeout) {
        requestTimeoutMillis = REQUEST_TIME_OUT_MILLIS
        connectTimeoutMillis = CONNECT_TIME_OUT_MILLIS
    }
}

private const val REQUEST_TIME_OUT_MILLIS: Long = 30_000
private const val CONNECT_TIME_OUT_MILLIS: Long = 30_000
private const val AUTH_PATH_SEGMENT = "auth"

private class KtorLogger(private val logger: AppLogger) : Logger {
    override fun log(message: String) {
        message.chunked(LOG_CHUNK_SIZE).forEach { logger.d("HTTP: $it") }
    }

    private companion object {
        private const val LOG_CHUNK_SIZE = 4000
    }
}

