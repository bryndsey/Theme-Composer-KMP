plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("org.jetbrains.compose")// version Versions.composeVersion
    id("dagger.hilt.android.plugin")
    id("com.squareup.sqldelight")
}

group = "com.bryanlindsey"
version = "1.0"

repositories {
    google()
    maven("https://jitpack.io")
    maven("https://github.com/trinoid/JFugue-for-Android/raw/master/jfugue-android/snapshot")
}

configurations {
    implementation {
        exclude(group = "com.github.JensPiegsa", module = "jfugue")
    }
}

dependencies {
//    implementation(fileTree(dir: "libs", include: ["*.jar"]))
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:${Versions.kotlinVersion}")
    implementation("androidx.activity:activity-compose:1.3.0-alpha04")
    implementation("androidx.activity:activity-ktx:1.3.0-alpha04")
//    implementation("androidx.appcompat:appcompat:1.2.0")
//    implementation("androidx.core:core-ktx:1.3.2")
    implementation("com.google.android.material:material:1.3.0")
//    implementation("androidx.compose.ui:ui:$compose_version")
//    implementation("androidx.compose.material:material:$compose_version")
//    implementation("androidx.compose.ui:ui-tooling:$compose_version")
    implementation("androidx.compose.material:material-icons-extended:1.0.0-beta03")
    testImplementation("junit:junit:4.12")
    androidTestImplementation("androidx.test.ext:junit:1.1.0")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.1")

    implementation("androidx.datastore:datastore-preferences:1.0.0-alpha08")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVersion}")

    implementation("com.google.dagger:hilt-android:${Versions.hiltVersion}")
    kapt("com.google.dagger:hilt-android-compiler:${Versions.hiltVersion}")

    implementation("com.squareup.sqldelight:android-driver:${Versions.sqlDelightVersion}")

    implementation("jp.kshoji:jfugue-android:5.0.9.201706:@aar")
    implementation("com.github.appleeducate:MidiDriver-Android-SF2:1.1")
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
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }
    }
}