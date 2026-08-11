package sk.mkdigital.kmpshowcase.data.network

import sk.mkdigital.kmpshowcase.BuildType
import sk.mkdigital.kmpshowcase.util.Logger as AppLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

actual class HttpClientProvider actual constructor(
    private val baseUrl: String,
    private val buildType: BuildType,
    private val logger: AppLogger,
) {
    actual fun create(): HttpClient {
        return HttpClient(Darwin) {
            applyCommonConfig(baseUrl, buildType, logger)
            defaultRequest { header(HttpHeaders.AcceptEncoding, "identity") }
            engine {
                configureRequest {
                    setAllowsCellularAccess(true)
                }
            }
        }
    }
}
