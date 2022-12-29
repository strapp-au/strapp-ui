plugins {
    id("com.android.library")
    kotlin("android")
    id("maven-publish")
    kotlin("kapt")
}

val libVersion = ext.get("strapp_version") as String

group = "com.github.strapp-au"
version = libVersion

repositories {
    mavenLocal()
}

android {
    compileSdk = 33
    defaultConfig {
        minSdk = 22
        targetSdk = 33

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        testInstrumentationRunnerArguments["useTestStorageService"] = "true"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

    buildFeatures {
        compose = true
        dataBinding = true
    }

    dataBinding {
        isEnabled = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.3.0"
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("release") {
                groupId = "com.github.strapp-au"
                artifactId = "strapp-ui"

                version = libVersion
                description = libVersion

                from(components["release"])
            }
            create<MavenPublication>("debug") {
                from(components["debug"])
            }
        }
    }
}

dependencies {
    implementation(project(":shared"))

    implementation("com.google.code.gson:gson:2.9.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.7.10")
    implementation("androidx.savedstate:savedstate-ktx:1.1.0")
    implementation("androidx.test:rules:1.4.0")

    implementation("androidx.core:core-ktx:1.9.0")
    implementation(kotlin("test-junit"))
    implementation("junit:junit:4.13.2")
    kaptTest("androidx.databinding:databinding-compiler:7.2.0")

    implementation("androidx.test.ext:junit:1.1.3")
    implementation("androidx.test.espresso:espresso-core:3.4.0")

    val compose_version = "1.3.0"
    implementation("androidx.test:core:1.5.0")
    implementation("androidx.compose.ui:ui:$compose_version")
    // Tooling  support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:$compose_version")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:$compose_version")
    // Material Design
    implementation("androidx.compose.material:material:$compose_version")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:$compose_version")
    implementation("androidx.compose.material:material-icons-extended:$compose_version")
    // UI Tests
    implementation("androidx.compose.ui:ui-test-junit4:$compose_version")
    implementation("androidx.compose.ui:ui-test-manifest:$compose_version")

}