plugins {
    id("org.jetbrains.compose") version "0.2.0-build132"
    id("com.android.application")
    kotlin("android")
}

group = "com.bryanlindsey"
version = "1.0"

repositories {
    google()
}

dependencies {
    implementation(project(":common"))
}

android {
    compileSdkVersion(29)
    defaultConfig {
        applicationId = "com.bryanlindsey.android"
        minSdkVersion(24)
        targetSdkVersion(29)
        versionCode = 1
        versionName = "1.0"
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
        }
    }
}