import dev.detekt.gradle.extensions.DetektExtension

buildscript {
    dependencies {
        classpath(platform(libs.build.netty.bom)) {
            because("Firebase App Distribution brings an unpatched Netty through grpc-netty: GHSA-c4c3-7fpv-j4q5")
        }
        constraints {
            classpath(libs.build.bouncycastle.bcprov) {
                because("AGP brings an unpatched bcprov: GHSA-9pwp-9qqc-pr26, GHSA-qp49-qgx5-5m26, GHSA-c3fc-8qff-9hwx")
            }
            classpath(libs.build.bouncycastle.bcpkix) {
                because("AGP brings an unpatched bcpkix: GHSA-wg6q-6289-32hp")
            }
            classpath(libs.build.bouncycastle.bcutil) {
                because("Bouncy Castle releases its jars together; bcutil follows bcprov and bcpkix")
            }
            classpath(libs.build.jose4j) {
                because("AGP brings an unpatched jose4j: GHSA-3677-xxcr-wjqv")
            }
            classpath(libs.build.jdom2) {
                because("AGP brings an unpatched jdom2: GHSA-2363-cqg2-863c")
            }
            classpath(libs.build.commons.lang3) {
                because("AGP brings an unpatched commons-lang3 through commons-compress: GHSA-j288-q9x7-2f5v")
            }
        }
    }
}

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

    configurations.matching { it.name == "androidLintTool" }.configureEach {
        val build = rootProject.libs.build
        mapOf(
            build.bouncycastle.bcprov to
                "lint brings an unpatched bcprov: GHSA-9pwp-9qqc-pr26, GHSA-qp49-qgx5-5m26, GHSA-c3fc-8qff-9hwx",
            build.bouncycastle.bcpkix to "lint brings an unpatched bcpkix: GHSA-wg6q-6289-32hp",
            build.bouncycastle.bcutil to "Bouncy Castle releases its jars together; bcutil follows bcprov and bcpkix",
            build.commons.lang3 to "lint brings an unpatched commons-lang3: GHSA-j288-q9x7-2f5v",
            build.httpclient to "lint brings an unpatched HttpClient through httpmime: GHSA-7r82-7xv7-xcpj",
        ).forEach { (library, reason) -> project.dependencies.constraints.add(name, library) { because(reason) } }
    }
    // A constraint, never a dependency — a dependency here stops Kotlin adding the Swift export worker itself.
    configurations.matching { it.name == "swiftExportClasspath" }.configureEach {
        val reason = "Kotlin's Swift export worker brings an unpatched OpenTelemetry: GHSA-rcgg-9c38-7xpx"
        listOf(
            rootProject.libs.build.opentelemetry.api,
            rootProject.libs.build.opentelemetry.context,
        ).forEach { library -> project.dependencies.constraints.add(name, library) { because(reason) } }
    }
}
