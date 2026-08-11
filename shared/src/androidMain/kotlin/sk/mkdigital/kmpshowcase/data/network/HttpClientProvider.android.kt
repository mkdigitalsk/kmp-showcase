package sk.mkdigital.kmpshowcase.data.network

import sk.mkdigital.kmpshowcase.BuildType
import sk.mkdigital.kmpshowcase.util.Logger as AppLogger
import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual class HttpClientProvider actual constructor(
    private val baseUrl: String,
    private val buildType: BuildType,
    private val logger: AppLogger,
) {
    actual fun create(): HttpClient = HttpClient(OkHttp) {
        applyCommonConfig(baseUrl, buildType, logger)
    }
}
