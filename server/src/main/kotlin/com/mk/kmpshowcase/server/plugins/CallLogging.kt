package com.mk.kmpshowcase.server.plugins

import com.mk.kmpshowcase.contracts.ApiVersion
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import org.slf4j.event.Level

internal fun Application.configureCallLogging() {
    install(CallLogging) {
        level = Level.INFO
        format { call ->
            val status = call.response.status()
            val method = call.request.httpMethod.value
            val path = call.request.path()
            "$method $path -> $status"
        }
        filter { call ->
            // Log only API calls, skip health checks etc.
            val path = call.request.path()
            path.startsWith(ApiVersion.BASE) || path.startsWith("/api")
        }
    }
}
