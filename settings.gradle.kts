pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "StrappUI"
include(":android")
include(":shared")

include(":android:paparazzi")
includeBuild("android/paparazzi")