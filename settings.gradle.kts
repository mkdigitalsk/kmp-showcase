pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

rootProject.name = "KMP Showcase"

// Server-only builds (Docker: -PserverOnly=true) exclude the mobile app modules so the build
// needs no Android SDK. :server depends only on :contracts (jvm), which is Android-free.
val serverOnly = providers.gradleProperty("serverOnly").orNull == "true"
include(":contracts", ":server")
if (!serverOnly) {
    include(":shared", ":androidApp", ":iosApp")
}
