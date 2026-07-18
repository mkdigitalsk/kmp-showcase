plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    application
}

application {
    mainClass = "com.mk.kmpshowcase.server.ApplicationKt"
}

tasks.named<JavaExec>("run") {
    val envFile = rootProject.file(".env")
    if (envFile.exists()) {
        envFile.readLines()
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .forEach { line ->
                val idx = line.indexOf('=')
                if (idx > 0) environment(line.substring(0, idx), line.substring(idx + 1))
            }
    }
}

dependencies {
    implementation(project(":contracts"))

    // Ktor Server
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.auth.jwt)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.call.logging)
    implementation(libs.ktor.server.call.id)
    implementation(libs.ktor.server.status.pages)
    implementation(libs.ktor.server.rate.limit)
    implementation(libs.ktor.server.forwarded.header)
    implementation(libs.ktor.server.default.headers)
    implementation(libs.ktor.server.hsts)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.serialization.kotlinx.json)

    // Database
    implementation(libs.exposed.core)
    implementation(libs.exposed.dao)
    implementation(libs.exposed.jdbc)
    implementation(libs.postgresql)
    implementation(libs.h2)
    implementation(libs.hikari)

    // Email (lead notifications)
    implementation(libs.angus.mail)
    implementation(libs.kotlinx.html.jvm)

    // Security
    implementation(libs.bcrypt)

    // Logging
    implementation(libs.logback)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)

    // Testing
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.ktor.client.content.negotiation)
    testImplementation(libs.kotlin.test)
}
