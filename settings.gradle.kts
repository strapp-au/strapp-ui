pluginManagement {
    repositories {
        mavenLocal()
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}

rootProject.name = "StrappUI"
include(":android")
project(":android").name = "strapp-ui"
include(":shared")
project(":shared").projectDir = file("shared")
include(":android-plugin")
include(":androidsample")
