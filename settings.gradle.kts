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
include(":StrappPlugin")

//include(":paparazzi")
//project(":paparazzi").projectDir = file("third-party/paparazzi")
//include(":paparazzi:paparazzi")
//include(":paparazzi:paparazzi-agent")
//include(":paparazzi:paparazzi-gradle-plugin")
//include ':libs:layoutlib'
//include ':libs:native-macarm'
//include ':libs:native-macosx'
//include ':libs:native-win'
//include ':libs:native-linux'
//includeBuild("../paparazzi/paparazzi")
include(":core")
