package com.mk.kmpshowcase.server

import com.mk.kmpshowcase.server.config.DatabaseConfig
import com.mk.kmpshowcase.server.core.mail.MailConfig
import com.mk.kmpshowcase.server.core.security.JwtConfig
import com.mk.kmpshowcase.server.di.AppDependencies
import com.mk.kmpshowcase.server.plugins.configureAuth
import com.mk.kmpshowcase.server.plugins.configureCORS
import com.mk.kmpshowcase.server.plugins.configureCallLogging
import com.mk.kmpshowcase.server.plugins.configureRouting
import com.mk.kmpshowcase.server.plugins.configureSerialization
import com.mk.kmpshowcase.server.plugins.configureStatusPages
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Application")

fun main(args: Array<String>) = EngineMain.main(args)

internal fun Application.module() {
    logger.info("Server starting...")
    val config = environment.config

    val useH2 = config.property("database.useH2").getString().toBoolean()
    check(useH2 || System.getenv("JWT_SECRET") != null) {
        "JWT_SECRET must be set when running against a production database"
    }

    DatabaseConfig.init(config)
    val jwtConfig = JwtConfig(
        secret = config.property("jwt.secret").getString(),
        issuer = config.property("jwt.issuer").getString(),
        audience = config.property("jwt.audience").getString(),
    )
    val mailConfig = MailConfig(
        host = config.property("mail.host").getString(),
        port = config.property("mail.port").getString().toInt(),
        user = config.property("mail.user").getString(),
        password = config.property("mail.password").getString(),
        from = config.property("mail.from").getString(),
        recipient = config.property("mail.recipient").getString(),
        resendApiKey = config.property("mail.resendApiKey").getString(),
    )
    val dependencies = AppDependencies(jwtConfig, mailConfig)
    configureCallLogging()
    configureSerialization()
    configureStatusPages()
    configureCORS(config)
    configureAuth(jwtConfig)
    configureRouting(dependencies)
}
