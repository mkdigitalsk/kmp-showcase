plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.ktor)
    application
}

ktor {
    openApi {
        // The compiler reads each route's description off the routing tree.
        // A .describe {} on a route adds what it cannot infer.
        enabled = true
        codeInferenceEnabled = true
    }
}

application {
    mainClass = "sk.mkdigital.kmpshowcase.server.ApplicationKt"
}

tasks.named<JavaExec>("run") {
    // .env fills gaps only — an explicitly exported shell var ALWAYS wins. Without this guard,
    // `USE_H2=true ./gradlew :server:run` would still be silently pointed at the staging DB.
    val envFile = rootProject.file(".env")
    if (envFile.exists()) {
        envFile.readLines()
            .filter { it.isNotBlank() && !it.startsWith("#") }
            .forEach { line ->
                val idx = line.indexOf('=')
                if (idx > 0) {
                    val key = line.substring(0, idx)
                    if (System.getenv(key) == null) environment(key, line.substring(idx + 1))
                }
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
    implementation(libs.ktor.server.routing.openapi)
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
    testImplementation(libs.coroutines.test)
    testImplementation(libs.testcontainers.postgresql)
}

// The committed specification is an output of the test run that writes it — a build-cache hit restores it instead of
// leaving a stale one on disk, and an edited one forces the run.
tasks.test {
    outputs.file(rootProject.file("contracts/openapi.json")).withPropertyName("spec")
}
