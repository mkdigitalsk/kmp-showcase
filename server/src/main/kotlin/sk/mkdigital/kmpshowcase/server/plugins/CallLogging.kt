package sk.mkdigital.kmpshowcase.server.plugins

import sk.mkdigital.kmpshowcase.contracts.ApiVersion
import sk.mkdigital.kmpshowcase.server.core.maskEmails
import io.ktor.http.HttpHeaders
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.callid.CallId
import io.ktor.server.plugins.callid.callIdMdc
import io.ktor.server.plugins.calllogging.CallLogging
import io.ktor.server.request.httpMethod
import io.ktor.server.request.path
import java.util.UUID
import org.slf4j.event.Level

internal fun Application.configureCallLogging() {
    // Correlation id per request (backend conventions → Config & observability). CallId must install
// BEFORE CallLogging so the MDC
    // value exists for every log line the request produces; the id is echoed as X-Request-Id so a
    // client-visible error can be matched to its full server trace.
    install(CallId) {
        retrieveFromHeader(HttpHeaders.XRequestId)
        generate { UUID.randomUUID().toString().take(CALL_ID_LENGTH) }
        verify { it.isNotBlank() }
        replyToHeader(HttpHeaders.XRequestId)
    }
    install(CallLogging) {
        level = Level.INFO
        callIdMdc("call-id")
        format { call ->
            val status = call.response.status()
            val method = call.request.httpMethod.value
            // Paths carry user emails (/v1/users/{email}) — mask them (PII discipline).
            val path = call.request.path().maskEmails()
            "$method $path -> $status"
        }
        filter { call ->
            // Log only API calls, skip health checks etc.
            val path = call.request.path()
            path.startsWith(ApiVersion.BASE)
        }
    }
}

private const val CALL_ID_LENGTH = 8
