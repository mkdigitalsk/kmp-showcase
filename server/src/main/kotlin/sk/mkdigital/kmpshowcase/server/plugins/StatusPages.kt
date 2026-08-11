package sk.mkdigital.kmpshowcase.server.plugins

import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.server.application.Application
import io.ktor.server.application.ApplicationCall
import io.ktor.server.application.install
import sk.mkdigital.kmpshowcase.server.core.PayloadTooLargeException
import sk.mkdigital.kmpshowcase.server.core.maskEmails
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.CannotTransformContentToTypeException
import io.ktor.server.plugins.UnsupportedMediaTypeException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.request.uri
import io.ktor.server.response.respondText
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("StatusPages")

/**
 * Problem Details. Ktor has no first-party support — hand-rolled and served as `application/problem+json`
 * so clients can branch on a typed error body. See backend conventions → Errors.
 *
 * [RFC 9457 — Problem Details for HTTP APIs](https://www.rfc-editor.org/rfc/rfc9457)
 */
@Serializable
internal data class ProblemDetail(
    val type: String = "about:blank",
    val title: String,
    val status: Int,
    val detail: String? = null,
    val instance: String? = null,
)

internal class ForbiddenException(message: String) : Exception(message)

private val problemJson = Json { explicitNulls = false }
private val problemContentType = ContentType("application", "problem+json")

// Ktor's ApplicationCall happens to implement CoroutineScope; the receiver is the call, not a scope to launch in.
@Suppress("SuspendFunWithCoroutineScopeReceiver")
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
            logger.debug("Malformed request body: ${cause.message?.maskEmails()}")
            call.respondProblem(HttpStatusCode.BadRequest, "Invalid or malformed request body")
        }
        // No converter accepts the Content-Type; one that reads the body and fails throws ContentConvertException.
        exception<UnsupportedMediaTypeException> { call, cause ->
            logger.debug("Unsupported media type: ${cause.message?.maskEmails()}")
            call.respondProblem(HttpStatusCode.UnsupportedMediaType, "Content-Type must be application/json")
        }
        exception<CannotTransformContentToTypeException> { call, cause ->
            logger.debug("No converter for the request content type: ${cause.message?.maskEmails()}")
            call.respondProblem(HttpStatusCode.UnsupportedMediaType, "Content-Type must be application/json")
        }
        exception<ContentConvertException> { call, cause ->
            logger.debug("Body deserialization failed: ${cause.message?.maskEmails()}")
            call.respondProblem(HttpStatusCode.BadRequest, "Invalid or malformed request body")
        }
        exception<IllegalArgumentException> { call, cause ->
            logger.debug("Bad request: ${cause.message?.maskEmails()}")
            call.respondProblem(HttpStatusCode.BadRequest, cause.message ?: "Bad request")
        }
        exception<PayloadTooLargeException> { call, cause ->
            logger.debug("Payload too large: ${cause.message?.maskEmails()}")
            call.respondProblem(HttpStatusCode.PayloadTooLarge, cause.message ?: "Payload too large")
        }
        exception<IllegalStateException> { call, cause ->
            logger.warn("Conflict: ${cause.message?.maskEmails()}")
            call.respondProblem(HttpStatusCode.Conflict, cause.message ?: "Conflict")
        }
        exception<ForbiddenException> { call, cause ->
            logger.debug("Forbidden: ${cause.message?.maskEmails()}")
            call.respondProblem(HttpStatusCode.Forbidden, cause.message ?: "Forbidden")
        }
        exception<NoSuchElementException> { call, cause ->
            logger.debug("Not found: ${cause.message?.maskEmails()}")
            call.respondProblem(HttpStatusCode.NotFound, cause.message ?: "Not found")
        }
        // The RateLimit plugin emits a bodiless 429 + Retry-After; give it a problem+json body like the rest.
        status(HttpStatusCode.TooManyRequests) { call, status ->
            val retryAfter = call.response.headers[HttpHeaders.RetryAfter]
            val suffix = retryAfter?.let { " Retry after $it seconds." }.orEmpty()
            call.respondProblem(status, "Too many requests.$suffix")
        }
        // Never leak internals (stack trace, cause.message, SQL) — log the cause, return a safe summary.
        exception<Throwable> { call, cause ->
            logger.error("Internal server error", cause)
            call.respondProblem(HttpStatusCode.InternalServerError, "Internal server error")
        }
    }
}
