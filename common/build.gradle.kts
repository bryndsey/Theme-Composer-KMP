import org.jetbrains.compose.compose

plugins {
    kotlin("multiplatform")
    kotlin("kapt")
    id("com.android.library")
    id("org.jetbrains.compose")// version Versions.composeVersion
}

group = "dev.bryanlindsey"
version = "1.0"

repositories {
    google()
}

kotlin {
    android()
    jvm("desktop") {
        compilations.all {
            kotlinOptions.jvmTarget = "11"
        }
    }
    sourceSets {
        val commonMain by getting {
            dependencies {
                api(compose.runtime)
                api(compose.foundation)
                api(compose.material)
                api(compose.materialIconsExtended)

                api("org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}")

                api(project(":staccato"))
                api(project(":themecomposer"))

                api("com.github.JensPiegsa:jfugue:5.0.9")

                api("com.google.dagger:dagger:${Versions.daggerVersion}")

                api("com.squareup.sqldelight:coroutines-extensions:${Versions.sqlDelightVersion}")
            }
        }
        val commonTest by getting
        val androidMain by getting
        val androidTest by getting {
            dependencies {
                implementation("junit:junit:4.13")
            }
        }
        val desktopMain by getting
        val desktopTest by getting
    }
}

dependencies {
    "kapt"("com.google.dagger:dagger-compiler:${Versions.daggerVersion}")
}

android {
    compileSdkVersion(29)
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    defaultConfig {
        minSdkVersion(24)
        targetSdkVersion(29)
    }
}