import dev.detekt.gradle.extensions.DetektExtension

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.kotlin.multiplatform.library) apply false
    alias(libs.plugins.compose.multiplatform) apply false
    alias(libs.plugins.compose.compiler) apply false
    alias(libs.plugins.kotlin.jvm) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.google.services) apply false
    alias(libs.plugins.fb.crashlytics) apply false
    alias(libs.plugins.firebase.distribution) apply false
}

subprojects {
    plugins.withId("org.jetbrains.kotlin.jvm") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension> {
            jvmToolchain(21)
        }
    }
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        extensions.configure<org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension> {
            jvmToolchain(21)
        }
    }

    // Per subproject: detekt derives type-resolution tasks from Kotlin compilations, and the root has none.
    apply(plugin = "dev.detekt")

    // detekt skips type-resolution rules silently, so the per-compilation tasks matter; the source-set
    // ones are what reaches Kotlin/Native.
    tasks.named("detekt") {
        dependsOn(
            tasks.matching { task ->
                task.name.startsWith("detekt") &&
                    task.name != "detekt" &&
                    !task.name.startsWith("detektBaseline") &&
                    !task.name.startsWith("detektGenerateConfig")
            }
        )
    }

    extensions.configure<DetektExtension> {
        buildUponDefaultConfig = true
        allRules = false
        config.setFrom(rootProject.files("config/detekt/detekt.yml"))
        baseline = rootProject.file("config/detekt/baseline.xml")
        parallel = true
        autoCorrect = true
    }

    // A KMP source set carries its generated directories, so SQLDelight's tables and Compose's resource
    // accessors arrive as source detekt is asked to judge.
    tasks.withType<dev.detekt.gradle.Detekt>().configureEach {
        exclude { it.file.absolutePath.contains("/build/") }
    }

    dependencies {
        add("detektPlugins", rootProject.libs.detekt.compose)
    }
}
