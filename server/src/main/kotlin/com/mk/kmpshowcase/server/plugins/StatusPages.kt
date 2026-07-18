package com.mk.kmpshowcase.server.plugins

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import com.mk.kmpshowcase.server.core.PayloadTooLargeException
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.response.respondText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("StatusPages")

// RFC 9457 Problem Details. Ktor has no first-party support — hand-rolled and served as
// `application/problem+json` so clients can branch on a typed error body. See backend-conventions §4.
@Serializable
internal data class ProblemDetail(
    val type: String = "about:blank",
    val title: String,
    val status: Int,
    val detail: String? = null,
    val instance: String? = null,
)

private val problemJson = Json { explicitNulls = false }
private val problemContentType = ContentType("application", "problem+json")

private suspend fun ApplicationCall.respondProblem(
    status: HttpStatusCode,
    detail: String,
    title: String = status.description,
) {
    val problem = ProblemDetail(title = title, status = status.value, detail = detail, instance = request.uri)
    respondText(
        text = problemJson.encodeToString(ProblemDetail.serializer(), problem),
        contentType = problemContentType,
        status = status,
    )
}

internal fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            logger.debug("Malformed request body: ${cause.message}")
            call.respondProblem(HttpStatusCode.BadRequest, "Invalid or malformed request body")
        }
        exception<ContentConvertException> { call, cause ->
            logger.debug("Body deserialization failed: ${cause.message}")
            call.respondProblem(HttpStatusCode.BadRequest, "Invalid or malformed request body")
        }
        exception<IllegalArgumentException> { call, cause ->
            logger.debug("Bad request: ${cause.message}")
            call.respondProblem(HttpStatusCode.BadRequest, cause.message ?: "Bad request")
        }
        exception<PayloadTooLargeException> { call, cause ->
            logger.debug("Payload too large: ${cause.message}")
            call.respondProblem(HttpStatusCode.PayloadTooLarge, cause.message ?: "Payload too large")
        }
        exception<IllegalStateException> { call, cause ->
            logger.warn("Conflict: ${cause.message}")
            call.respondProblem(HttpStatusCode.Conflict, cause.message ?: "Conflict")
        }
        exception<NoSuchElementException> { call, cause ->
            logger.debug("Not found: ${cause.message}")
            call.respondProblem(HttpStatusCode.NotFound, cause.message ?: "Not found")
        }
        // The RateLimit plugin emits a bodiless 429 + Retry-After; give it a problem+json body like the rest.
        status(HttpStatusCode.TooManyRequests) { call, status ->
            val retryAfter = call.response.headers[HttpHeaders.RetryAfter]
            val suffix = retryAfter?.let { " Retry after $it seconds." } ?: ""
            call.respondProblem(status, "Too many requests.$suffix")
        }
        // Never leak internals (stack trace, cause.message, SQL) — log the cause, return a safe summary.
        exception<Throwable> { call, cause ->
            logger.error("Internal server error", cause)
            call.respondProblem(HttpStatusCode.InternalServerError, "Internal server error")
        }
    }
}
