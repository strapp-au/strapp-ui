plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.18.0"
}

val pluginVersion = ext.get("strapp_version") as String
val pluginDescription = "Take snapshots of your native mobile UI and see them in one place."

group = "au.strapp"
version = pluginVersion

pluginBundle {
    website = "https://strapp.au"
    description = pluginDescription
    vcsUrl = "https://github.com/strapp-au/strapp-ui"
    tags = listOf("developer", "tool", "android", "ios", "snapshot", "test")
}

gradlePlugin {
    plugins {
        create("strapp") {
            id = "au.strapp.strapp-ui"
            displayName = "StrappUI"
            description = pluginDescription
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
            description = pluginDescription

            from(components["java"])
        }
    }
}

dependencies {
//    implementation("app.cash.paparazzi:paparazzi:0.9.3")
    implementation(project(":paparazzi:paparazzi"))
    implementation(project(":paparazzi:paparazzi-gradle-plugin"))

}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}