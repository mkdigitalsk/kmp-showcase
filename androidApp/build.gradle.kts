import com.google.firebase.appdistribution.gradle.firebaseAppDistribution
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.maven.publish)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.fb.crashlytics)
    alias(libs.plugins.firebase.distribution)
    alias(libs.plugins.roborazzi)
}

val keystorePropertiesFile = project.file("keystore.properties")
val keystoreProperties = Properties()

if (keystorePropertiesFile.exists()) {
    keystoreProperties.load(keystorePropertiesFile.inputStream())
}

val localPropertiesFile = rootProject.file("local.properties")
val localProperties = Properties()

if (localPropertiesFile.exists()) {
    localProperties.load(localPropertiesFile.inputStream())
}

val hasSigningProperties = keystoreProperties.isNotEmpty()


android {
    compileSdk = libs.versions.androidCompileSdk.get().toInt()
    namespace = "com.mk.kmpshowcase"

    defaultConfig {
        applicationId = "com.mk.kmpshowcase"
        minSdk = libs.versions.androidMinSdk.get().toInt()
        targetSdk = libs.versions.androidTargetSdk.get().toInt()
        versionCode = (project.findProperty("versionCode") as String?)?.toIntOrNull() ?: 1
        versionName = (project.findProperty("versionName") as String?) ?: "1.0.0"
    }

    testOptions {
        unitTests {
            isIncludeAndroidResources = true
            all { test ->
                test.useJUnitPlatform()
                test.systemProperties["robolectric.pixelCopyRenderMode"] = "hardware"
            }
        }
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    signingConfigs {
        if (hasSigningProperties) {
            create("release") {
                storeFile = file(keystoreProperties["storeFile"] as String)
                storePassword = keystoreProperties["storePassword"] as String
                keyAlias = keystoreProperties["keyAlias"] as String
                keyPassword = keystoreProperties["keyPassword"] as String
            }
        }
    }

    buildTypes {
        debug {
            isDefault = true
            isDebuggable = true
            buildConfigField("String", "BASE_URL", "\"kmp-showcase-staging.up.railway.app\"")
        }

        release {
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            buildConfigField("String", "BASE_URL", "\"kmp-showcase-production.up.railway.app\"")

            val extraProguardFiles = fileTree("$projectDir/proguard") {
                include("*.pro")
            }.files.toTypedArray()

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                *extraProguardFiles
            )

            if (hasSigningProperties) {
                signingConfig = signingConfigs.getByName("release")
            }

            firebaseAppDistribution {
                appId = "1:463409941213:android:77c77a251382f0350a2934"
                artifactType = "APK"
                groups = localProperties["fb.test.group"]?.toString() ?: "testers"
            }
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(project(":shared"))
    implementation(libs.activity.compose)
    implementation(libs.compose.ui.tooling.preview)

    implementation(libs.android.material)
    implementation(libs.material3.android)

    // location
    implementation(libs.google.services.location)

    // Firebase (BOM manages versions)
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.app.check)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.messaging)

    // Testing - JUnit 5 + Vintage for JUnit4 + Roborazzi
    testImplementation(libs.junit4)
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.compose.ui.test.junit4)
    testImplementation(libs.roborazzi)
    testImplementation(libs.roborazzi.compose)
    testImplementation(libs.roborazzi.junit.rule)
    testImplementation(libs.robolectric)
    testRuntimeOnly(libs.junit.platform.launcher)
    testRuntimeOnly(libs.junit.vintage.engine)
    debugImplementation(libs.compose.ui.test.manifest)
}