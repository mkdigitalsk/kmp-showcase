package com.mk.kmpshowcase.server.plugins

import io.ktor.http.HttpStatusCode
import io.ktor.serialization.ContentConvertException
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.BadRequestException
import io.ktor.server.plugins.statuspages.StatusPages
import io.ktor.server.response.respond
import kotlinx.serialization.Serializable
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("StatusPages")

internal fun Application.configureStatusPages() {
    install(StatusPages) {
        exception<BadRequestException> { call, cause ->
            logger.debug("Malformed request body: ${cause.message}")
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid or malformed request body"))
        }
        exception<ContentConvertException> { call, cause ->
            logger.debug("Body deserialization failed: ${cause.message}")
            call.respond(HttpStatusCode.BadRequest, ErrorResponse("Invalid or malformed request body"))
        }
        exception<IllegalArgumentException> { call, cause ->
            logger.debug("Bad request: ${cause.message}")
            call.respond(HttpStatusCode.BadRequest, ErrorResponse(cause.message ?: "Bad request"))
        }
        exception<IllegalStateException> { call, cause ->
            logger.warn("Conflict: ${cause.message}")
            call.respond(HttpStatusCode.Conflict, ErrorResponse(cause.message ?: "Conflict"))
        }
        exception<NoSuchElementException> { call, cause ->
            logger.debug("Not found: ${cause.message}")
            call.respond(HttpStatusCode.NotFound, ErrorResponse(cause.message ?: "Not found"))
        }
        exception<Throwable> { call, cause ->
            logger.error("Internal server error", cause)
            call.respond(
                HttpStatusCode.InternalServerError,
                ErrorResponse(cause.message ?: "Internal server error")
            )
        }
    }
}

@Serializable
internal data class ErrorResponse(val message: String)
