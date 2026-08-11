package sk.mkdigital.kmpshowcase.server.plugins

import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import kotlinx.serialization.json.Json

internal fun Application.configureSerialization() {
    install(ContentNegotiation) {
        json(Json {
            prettyPrint = true
            isLenient = true
            ignoreUnknownKeys = true
            // Off by default, which drops every property equal to its declared default — so a contract
            // field written as `= false` reaches a client that does not share the contract as absent.
            encodeDefaults = true
        })
    }
}
