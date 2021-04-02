buildscript {
    repositories {
        gradlePluginPortal()
        jcenter()
        google()
        mavenCentral()
        maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
    }
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}")
        classpath("org.jetbrains.compose:compose-gradle-plugin:${Versions.composeVersion}")
        classpath("com.android.tools.build:gradle:4.0.2")

        classpath("com.google.dagger:hilt-android-gradle-plugin:${Versions.hiltVersion}")
        classpath("com.squareup.sqldelight:gradle-plugin:${Versions.sqlDelightVersion}")
    }
}

group = "com.bryanlindsey"
version = "1.0"

allprojects {
    repositories {
        jcenter()
        mavenCentral()
        maven { url = uri("https://maven.pkg.jetbrains.space/public/p/compose/dev") }
        maven("https://jitpack.io")
    }
}