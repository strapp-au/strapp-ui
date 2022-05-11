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
include(":StrappPlugin")

include(":paparazzi")
project(":paparazzi").projectDir = file("../paparazzi")
//include(":paparazzi:paparazzi")
//include(":paparazzi:paparazzi-agent")
include(":paparazzi:paparazzi-gradle-plugin")
//include ':libs:layoutlib'
//include ':libs:native-macarm'
//include ':libs:native-macosx'
//include ':libs:native-win'
//include ':libs:native-linux'
//includeBuild("../paparazzi/paparazzi")