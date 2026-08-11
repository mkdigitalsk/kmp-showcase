package sk.mkdigital.kmpshowcase.server

import sk.mkdigital.kmpshowcase.server.config.DatabaseConfig
import sk.mkdigital.kmpshowcase.server.config.useH2
import sk.mkdigital.kmpshowcase.server.core.security.JwtConfig
import sk.mkdigital.kmpshowcase.server.di.AppDependencies
import sk.mkdigital.kmpshowcase.server.plugins.configureAuth
import sk.mkdigital.kmpshowcase.server.plugins.configureCORS
import sk.mkdigital.kmpshowcase.server.plugins.configureCallLogging
import sk.mkdigital.kmpshowcase.server.plugins.configureRateLimit
import sk.mkdigital.kmpshowcase.server.plugins.configureRouting
import sk.mkdigital.kmpshowcase.server.plugins.configureSecurityHeaders
import sk.mkdigital.kmpshowcase.server.plugins.configureSerialization
import sk.mkdigital.kmpshowcase.server.plugins.configureStatusPages
import io.ktor.server.application.Application
import io.ktor.server.netty.EngineMain
import org.slf4j.LoggerFactory

private val logger = LoggerFactory.getLogger("Application")

fun main(args: Array<String>) = EngineMain.main(args)

internal fun Application.module() {
    logger.info("Server starting...")
    val config = environment.config

    val isH2 = config.useH2()
    check(isH2 || System.getenv("JWT_SECRET") != null) {
        "JWT_SECRET must be set when running against a production database"
    }

    DatabaseConfig.init(config)
    val jwtConfig = JwtConfig(
        secret = config.property("jwt.secret").getString(),
        issuer = config.property("jwt.issuer").getString(),
        audience = config.property("jwt.audience").getString(),
    )
    val dependencies = AppDependencies(jwtConfig)
    configureSecurityHeaders()
    configureCallLogging()
    configureSerialization()
    configureStatusPages()
    configureCORS(config)
    configureAuth(jwtConfig)
    configureRateLimit()
    configureRouting(dependencies)
}
