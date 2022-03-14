plugins {
    id("com.android.library")
    kotlin("android")
    id("app.cash.paparazzi")
}

android {
    compileSdk = 31
    defaultConfig {
        minSdk = 24
        targetSdk = 31

        buildConfigField("String", "PROJECT_DIR", "\"${project.rootDir.absolutePath}\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
    }

    composeOptions {
        kotlinCompilerVersion = "1.6.10"
        kotlinCompilerExtensionVersion = "1.1.0-rc03"
    }
}

dependencies {
    implementation(project(":shared"))
//    implementation(project(":android:paparazzi:paparazzi"))

    implementation("com.google.code.gson:gson:2.9.0")

    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.10")
    implementation("androidx.savedstate:savedstate-ktx:1.1.0")
//    implementation(project(":core")) {
//        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-core")
//    }
//    implementation("androidx.appcompat:appcompat:1.4.0")

    val kotlin_version = "1.6.10"

    implementation("androidx.core:core-ktx:1.3.2")
    implementation(kotlin("test-junit"))
    implementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

    val compose_version = "1.1.0-beta03"
    testImplementation("androidx.test:core:1.4.0")
    implementation("androidx.compose.ui:ui:$compose_version")
    // Tooling support (Previews, etc.)
    implementation("androidx.compose.ui:ui-tooling:$compose_version")
    // Foundation (Border, Background, Box, Image, Scroll, shapes, animations, etc.)
    implementation("androidx.compose.foundation:foundation:$compose_version")
    // Material Design
    implementation("androidx.compose.material:material:$compose_version")
    // Material design icons
    implementation("androidx.compose.material:material-icons-core:$compose_version")
    implementation("androidx.compose.material:material-icons-extended:$compose_version")
}