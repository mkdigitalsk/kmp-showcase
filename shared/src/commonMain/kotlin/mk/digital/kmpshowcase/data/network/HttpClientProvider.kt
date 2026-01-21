package mk.digital.kmpshowcase.data.network

import io.ktor.client.HttpClient
import io.ktor.client.HttpClientConfig
import io.ktor.client.plugins.HttpTimeout
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.LogLevel
import io.ktor.client.plugins.logging.Logger
import io.ktor.client.plugins.logging.Logging
import io.ktor.http.URLProtocol
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

expect class HttpClientProvider() {
    fun create(): HttpClient
}

fun HttpClientConfig<*>.applyCommonConfig() {
    defaultRequest {
        url {
            protocol = URLProtocol.HTTPS
            host = "jsonplaceholder.typicode.com"
        }
    }
    install(ContentNegotiation) {
        json(
            Json {
                prettyPrint = true
                isLenient = true
                ignoreUnknownKeys = true
            }
        )
    }

    install(Logging) {
        logger = Logger.DEFAULT
        level = LogLevel.ALL
    }

    install(HttpTimeout) {
        requestTimeoutMillis = REQUEST_TIME_OUT_MILLIS
        connectTimeoutMillis = CONNECT_TIME_OUT_MILLIS
    }
}

private const val REQUEST_TIME_OUT_MILLIS: Long = 30_000
private const val CONNECT_TIME_OUT_MILLIS: Long = 30_000
