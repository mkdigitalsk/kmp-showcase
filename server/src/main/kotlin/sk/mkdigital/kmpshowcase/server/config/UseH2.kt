package sk.mkdigital.kmpshowcase.server.config

import io.ktor.server.config.ApplicationConfig

/** `toBoolean()` answers false for an empty or misspelled value, picking a database by accident. */
internal fun ApplicationConfig.useH2(): Boolean =
    when (val raw = property("database.useH2").getString()) {
        "true" -> true
        "false" -> false
        else -> error("USE_H2 must be exactly true or false, was \"$raw\" — set it explicitly per environment")
    }
