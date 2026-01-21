package mk.digital.kmpshowcase.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.darwin.Darwin
import io.ktor.client.plugins.defaultRequest
import io.ktor.client.request.header
import io.ktor.http.HttpHeaders

actual class HttpClientProvider {
    actual fun create(): HttpClient {
        return HttpClient(Darwin) {
            applyCommonConfig()
            defaultRequest { header(HttpHeaders.AcceptEncoding, "identity") }
            engine {
                configureRequest {
                    setAllowsCellularAccess(true)
                }
            }
        }
    }
}
