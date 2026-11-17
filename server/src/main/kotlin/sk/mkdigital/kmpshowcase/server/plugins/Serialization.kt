package sk.mkdigital.kmpshowcase.server.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

internal fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(wireJson)
    }
}

/**
 * `encodeDefaults` sends every property, its default included — off, a contract field written as `= false` reaches a
 * client that does not share the contract as absent.
 */
private val wireJson = Json {
    prettyPrint = true
    isLenient = true
    ignoreUnknownKeys = true
    encodeDefaults = true
}
