plugins {
    id("org.jetbrains.kotlin.jvm")
    id("java-gradle-plugin")
    id("maven-publish")
    id("com.gradle.plugin-publish") version "0.18.0"
}

repositories {
    mavenLocal()
//    maven {
//        name = "Strapp Github Packages"
//        url = uri("https://maven.pkg.github.com/strapp-au/paparazzi")
//        credentials {
//            username = "brenpearson"//findProperty("gpr.user") as String? ?: System.getenv("USERNAME")
//            password = "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"//findProperty("gpr.key") as String? ?: System.getenv("TOKEN")
//        }
//    }
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
    implementation("app.cash.paparazzi:paparazzi:1.0.0")
    implementation("app.cash.paparazzi:paparazzi-gradle-plugin:1.0.0")
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}