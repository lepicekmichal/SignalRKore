plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.vanniktech.maven.publish.base")
}

group = requireNotNull(project.findProperty("GROUP"))
version = requireNotNull(project.findProperty("VERSION_NAME"))

kotlin {
    android {
        publishLibraryVariants("release")
    }

    targets {
        android {
            compilations.all {
                kotlinOptions {
                    jvmTarget = JavaVersion.VERSION_1_8.toString()
                }
            }
        }
        jvm {
            compilations.all {
                kotlinOptions.jvmTarget = JavaVersion.VERSION_1_8.toString()
            }
        }
    }

    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:1.7.20")
                implementation("io.ktor:ktor-client-core:2.2.1")
                implementation("io.ktor:ktor-client-websockets:2.2.1")
                implementation("io.ktor:ktor-client-content-negotiation:2.2.1")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.2.1")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
                implementation("com.squareup.okio:okio:3.2.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.squareup.okhttp3:okhttp:4.10.0")
                implementation("io.ktor:ktor-client-okhttp:2.2.1")
            }
        }
        val androidMain by getting {
            dependsOn(jvmMain)
        }
        val androidTest by getting
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 21
        targetSdk = 33
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    namespace = "eu.lepicekmichal.signalrkore"
}

mavenPublishing {
    pomFromGradleProperties()
    publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.S01)
    signAllPublications()
    configure(
        com.vanniktech.maven.publish.KotlinMultiplatform(
            javadocJar = com.vanniktech.maven.publish.JavadocJar.Empty(),
        )
    )
}