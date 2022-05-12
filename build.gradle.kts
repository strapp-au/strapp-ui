buildscript {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        jcenter()
        mavenLocal()
        maven {
            url = uri("https://plugins.gradle.org/m2/")
        }
        maven(url = "https://maven.pkg.jetbrains.space/public/p/kotlinx-coroutines/maven")
        maven(url = "https://www.jetbrains.com/intellij-repository/releases")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.10")
        classpath("com.android.tools.build:gradle:7.0.4")
    }
}

allprojects {

    ext {
        set("kotlin_version", "1.6.10")
    }

    repositories {
        google()
        mavenCentral()
    }
}

