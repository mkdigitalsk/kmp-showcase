plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.kotlin.serialization)
}

// Pure @Serializable DTOs shared by mobile (iOS native) and the JVM server. No Android target:
// the Android consumer (:shared) resolves the jvm() variant via Kotlin's androidJvm→jvm
// compatibility (verified: :androidApp:assembleDebug builds without it). Dropping it keeps the
// Android SDK out of the server-only build. iOS targets stay — Kotlin/Native can't use a jvm artifact.
kotlin {
    jvm()
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.json)
        }
    }
}
