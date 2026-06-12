package com.mk.kmpshowcase.server

import com.mk.kmpshowcase.server.config.DatabaseConfig
import com.mk.kmpshowcase.server.di.AppDependencies
import com.mk.kmpshowcase.server.plugins.configureAuth
import com.mk.kmpshowcase.server.plugins.configureCORS
import com.mk.kmpshowcase.server.plugins.configureCallLogging
import com.mk.kmpshowcase.server.plugins.configureRouting
import com.mk.kmpshowcase.server.plugins.configureSerialization
import com.mk.kmpshowcase.server.plugins.configureStatusPages
import io.ktor.server.application.Application
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Application")

fun main() {
    embeddedServer(
        factory = Netty,
        port = System.getenv("PORT")?.toIntOrNull() ?: DEFAULT_PORT,
        host = "0.0.0.0",
        module = Application::module,
    ).start(wait = true)
}

private const val DEFAULT_PORT = 8080

fun Application.module() {
    logger.info("Server starting...")
    DatabaseConfig.init()
    val dependencies = AppDependencies()
    configureCallLogging()
    configureSerialization()
    configureStatusPages()
    configureCORS()
    configureAuth()
    configureRouting(dependencies)
}
