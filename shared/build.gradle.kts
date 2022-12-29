import org.jetbrains.kotlin.gradle.plugin.mpp.apple.XCFramework

plugins {
    kotlin("multiplatform")
    id("com.android.library")
    kotlin("plugin.serialization") version "1.6.10"
    id("com.chromaticnoise.multiplatform-swiftpackage") version "2.0.3"
    id("maven-publish")
}

kotlin {
    android()

    val xcFramework = XCFramework("Shared")

    multiplatformSwiftPackage {
        swiftToolsVersion("5.7.1")
        targetPlatforms {
            iOS { v("14") }
            iosSimulatorArm64()
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            xcFramework.add(this)
        }
    }

    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.2")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting
        val androidTest by getting
        val iosX64Main by getting
        val iosArm64Main by getting
        val iosSimulatorArm64Main by getting
        val iosMain by creating {
            dependsOn(commonMain)
            iosX64Main.dependsOn(this)
            iosArm64Main.dependsOn(this)
            iosSimulatorArm64Main.dependsOn(this)
        }
        val iosX64Test by getting
        val iosArm64Test by getting
        val iosSimulatorArm64Test by getting
        val iosTest by creating {
            dependsOn(commonTest)
            iosX64Test.dependsOn(this)
            iosArm64Test.dependsOn(this)
            iosSimulatorArm64Test.dependsOn(this)
        }
    }
}

val libVersion = ext.get("strapp_version") as String

group = "com.github.strapp-au"
version = libVersion

android {
    compileSdk = 32
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdk = 21
        targetSdk = 32
    }
}

afterEvaluate {
    publishing {
        publications {
            getByName<MavenPublication>("kotlinMultiplatform") {
                groupId = "com.github.strapp-au.strapp-ui"
                artifactId = "shared"

                version = libVersion
                description = libVersion
            }

            create<MavenPublication>("release") {
                groupId = "com.github.strapp-au.strapp-ui"
                artifactId = "shared"

                version = libVersion
                description = libVersion

                from(components["release"])
            }
        }
    }
}