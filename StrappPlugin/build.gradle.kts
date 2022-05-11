plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.18.0"
}

val pluginVersion = "0.1.0"

group = "au.strapp"
version = pluginVersion

pluginBundle {
    website = "https://strapp.au"
    vcsUrl = "https://github.com/strapp-au/strapp-ui"
    tags = listOf("developer", "tool", "android", "ios", "snapshot", "test")
}

gradlePlugin {
    plugins {
        create("strapp") {
            id = "au.strapp.strapp-ui"
            displayName = "StrappUI"
            description = "Build native mobile apps with clarity"
            implementationClass = "au.strapp.strappplugin.StrappPlugin"
            version = pluginVersion
        }
    }
}

publishing {
    publications {
        create<MavenPublication>("pluginPublication") {
            groupId = "au.strapp"
            artifactId = "strapp-ui"
            version = pluginVersion

            from(components["java"])
        }
    }
}

dependencies {
    implementation("app.cash.paparazzi:paparazzi:0.9.3")
    implementation(project(":paparazzi:paparazzi-gradle-plugin"))

}