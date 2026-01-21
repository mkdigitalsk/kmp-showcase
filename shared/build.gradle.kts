import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.compose.multiplatform)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.mokkery)
}

kotlin {
    androidLibrary {
        compileSdk = libs.versions.androidCompileSdk.get().toInt()
        namespace = "mk.digital.kmpshowcase.shared"
        minSdk = libs.versions.androidMinSdk.get().toInt()

        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }

        androidResources {
            enable = true
        }

        // for Unit tests to run on JVM
        withHostTest {}
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            freeCompilerArgs += listOf("-Xbinary=bundleId=mk.digital.kmpshowcase.shared")
            isStatic = true
        }
    }

    @OptIn(ExperimentalKotlinGradlePluginApi::class)
    applyDefaultHierarchyTemplate {
        common {
            withIos()
        }
    }

    sourceSets {
        all {
            languageSettings {
                optIn("androidx.compose.material3.ExperimentalMaterial3Api")
                optIn("kotlin.uuid.ExperimentalUuidApi")
                optIn("kotlinx.cinterop.ExperimentalForeignApi")
                optIn("kotlinx.cinterop.BetaInteropApi")
                enableLanguageFeature("ExpectActualClasses")
            }
        }

        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            api(libs.kotlinx.datetime)

            api(libs.koin.core)
            api(libs.koin.compose)
            api(libs.koin.compose.vm)

            // Navigation & Lifecycle
            api(libs.navigation3.compose)
            api(libs.lifecycle.viewmodel.navigation3)


            implementation(libs.compose.ui)
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
            implementation(libs.compose.material.icons.extended)

            api(libs.compose.components.resources)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.logging)

            implementation(libs.coil3.compose)
            implementation(libs.coil3.svg)
            implementation(libs.coil3.network.ktor)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }

        androidMain.dependencies {
            api(libs.koin.android)

            implementation(libs.ktor.client.okhttp)

            implementation(libs.android.material)
            implementation(libs.activity.ktx)
            implementation(libs.activity.compose)
            implementation(libs.compose.ui.tooling)
        }

        commonTest.dependencies {
            implementation(kotlin("test"))
            implementation(libs.coroutines.test)
        }

        // androidHostTest inherits from commonTest, runs on JVM without device
        getByName("androidHostTest").dependencies {
            implementation(kotlin("test-junit"))
        }
    }

    jvmToolchain(17)
}

compose.resources {
    publicResClass = true
}

//todo - delete?
if (project.hasProperty("configuration")) {
    tasks.register("assembleXCFramework") {
        group = "build"
        description =
            "📦 Assembles an XCFramework for iOS. Use -Pconfiguration=Debug|Release|Internal (default: Debug)"

        val config = (project.findProperty("configuration") as String?)
            ?.replaceFirstChar(Char::uppercaseChar)
            ?: throw GradleException("❌ Missing -Pconfiguration=BuildType (e.g., Debug, Release, Internal)")

        val effectiveBuildType = if (config == "Internal") "Debug" else config

        dependsOn(
            "link${effectiveBuildType}FrameworkIosArm64",
            "link${effectiveBuildType}FrameworkIosX64"
        )

        doLast {
            val outputDir = layout.buildDirectory.dir("xcframework/$config").get().asFile
            val outputFramework = outputDir.resolve("shared-$config.xcframework")


            if (outputFramework.exists()) {
                println("🗑 Removing existing XCFramework: $outputFramework")
                outputFramework.deleteRecursively()
            }

            outputDir.mkdirs()

            val frameworks = listOf(
                layout.buildDirectory.dir("bin/iosArm64/${effectiveBuildType.lowercase()}Framework/shared.framework")
                    .get().asFile,
                layout.buildDirectory.dir("bin/iosX64/${effectiveBuildType.lowercase()}Framework/shared.framework")
                    .get().asFile
            )

            println("✅ Assembling shared-$config.xcframework (effective build: $effectiveBuildType)")

            val process = ProcessBuilder(
                "xcodebuild",
                "-create-xcframework",
                "-framework", frameworks[0].absolutePath,
                "-framework", frameworks[1].absolutePath,
                "-output", outputFramework.absolutePath
            ).inheritIO().start()
            val exitCode = process.waitFor()
            if (exitCode != 0) {
                throw GradleException("xcodebuild failed with exit code $exitCode")
            }
        }
    }

    tasks.register<Copy>("linkXCFrameworkToIosApp") {
        dependsOn("assembleXCFramework")
        outputs.upToDateWhen { false }

        val config = (project.findProperty("configuration") as String?)?.lowercase()
            ?: throw GradleException("❌ Missing -Pconfiguration=Debug|Internal|Release")

        val configCapitalized = config.replaceFirstChar(Char::uppercaseChar)
        val frameworkName = "shared-$configCapitalized.xcframework"

        val sourceDir = layout.buildDirectory.dir("xcframework/$configCapitalized/$frameworkName")
        val targetDir = rootProject.layout.projectDirectory.dir("iosApp/Frameworks/$frameworkName")

        println("📦 Source dir: ${sourceDir.get().asFile.absolutePath}")
        println("📂 Target dir: ${targetDir.asFile.absolutePath}")

        targetDir.asFile.parentFile.mkdirs()
        targetDir.asFile.mkdirs()

        println("🔗 Copying $frameworkName to iosApp/Frameworks/$frameworkName")

        from(sourceDir)
        into(targetDir)
    }

    tasks.register<Copy>("linkXCFrameworkToIosAppForArchive") {
        dependsOn("assembleXCFramework")
        val config = (project.findProperty("configuration") as String?)?.lowercase()
            ?: throw GradleException("❌ Missing -Pconfiguration=Debug|Internal|Release")

        val configCapitalized = config.replaceFirstChar(Char::uppercaseChar)
        val frameworkName = "shared-$configCapitalized.xcframework"

        val sourceDir = layout.buildDirectory.dir("xcframework/$configCapitalized/$frameworkName")
        val targetDir =
            rootProject.layout.projectDirectory.dir("iosApp/$frameworkName") // do root iosApp

        println("🔗 Copying $frameworkName to iosApp/$frameworkName (for Archive)")

        from(sourceDir)
        into(targetDir)
    }
}
