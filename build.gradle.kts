buildscript {
    val compose_version by extra("1.3.0")
    repositories {
        mavenLocal()
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
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.7.10")
        classpath("com.android.tools.build:gradle:7.3.1")
    }
}

allprojects {

    ext {
        set("kotlin_version", "1.7.10")
        set("strapp_version", "22.12.1")
    }

    repositories {
        mavenLocal()
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.dsl.KotlinJvmCompile> {
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xopt-in=kotlin.RequiresOptIn"
    }
}