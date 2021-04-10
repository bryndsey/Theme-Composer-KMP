plugins {
    kotlin("multiplatform")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

kotlin {
    jvm()
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib:${Versions.kotlinVersion}")

                implementation("org.jetbrains.kotlin:kotlin-script-runtime:${Versions.kotlinVersion}")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation("junit:junit:4.12")
            }
        }
    }
}
