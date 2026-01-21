package mk.digital.kmpshowcase.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual class HttpClientProvider {
    actual fun create(): HttpClient = HttpClient(OkHttp) {
        applyCommonConfig()
    }
}
