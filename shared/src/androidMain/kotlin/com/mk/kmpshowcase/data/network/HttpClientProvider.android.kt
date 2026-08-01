package com.mk.kmpshowcase.data.network

import io.ktor.client.HttpClient
import io.ktor.client.engine.okhttp.OkHttp

actual class HttpClientProvider actual constructor(private val baseUrl: String) {
    actual fun create(): HttpClient = HttpClient(OkHttp) {
        applyCommonConfig(baseUrl)
    }
}
