plugins {
    id("com.android.application") version "8.9.2"
    id("org.jetbrains.kotlin.android") version "2.1.20"
}

// Wear OS date tile (Gregorian + Hebrew date, holiday / weekly portion). Standalone: computed on the watch by :core, no phone link, no permissions.
android {
    namespace = "com.clockweather.wear"
    compileSdk = 35

    defaultConfig {
        // the SAME applicationId as the phone app (and the same signing key): the Data Layer only links an app to its twin on the other device that way
        applicationId = "com.clockweather.app"
        minSdk = 26
        targetSdk = 35
        versionCode = (project.findProperty("versionCodeOverride") as String?)?.toIntOrNull() ?: 1
        versionName = "0.4.0"
    }

    // The same committed sideload key as the phone app (not secret; every build is an in-place update).
    signingConfigs {
        create("sideload") {
            storeFile = file("../app/sideload.jks")
            storePassword = "clockweather123"
            keyAlias = "clockweather"
            keyPassword = "clockweather123"
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("sideload")
        }
        debug { signingConfig = signingConfigs.getByName("sideload") }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlin { compilerOptions { jvmTarget.set(org.jetbrains.kotlin.gradle.dsl.JvmTarget.JVM_17) } }
    lint { abortOnError = true; warningsAsErrors = false; checkReleaseBuilds = false }
}

dependencies {
    implementation(project(":core"))
    implementation("androidx.wear.tiles:tiles:1.4.1")
    implementation("androidx.wear.protolayout:protolayout:1.2.1")
    implementation("com.google.guava:guava:33.4.0-android")
    implementation("com.google.android.gms:play-services-wearable:19.0.0")
}
