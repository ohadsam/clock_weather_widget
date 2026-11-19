// :core is pure Kotlin/JVM and builds anywhere (also in the cloud sandbox, which has no Android SDK).
// :app needs the Android SDK, so it is only included where one exists (CI, a developer machine).
pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "ClockWise"
include(":core")
val hasAndroidSdk = System.getenv("ANDROID_HOME") != null ||
    System.getenv("ANDROID_SDK_ROOT") != null ||
    providers.gradleProperty("withApp").isPresent
if (hasAndroidSdk) include(":app")
