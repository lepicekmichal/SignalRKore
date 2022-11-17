plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    `maven-publish`
}

kotlin {
    android()

    sourceSets {
        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
        }

        val commonMain by getting {
            dependencies {
                implementation("io.ktor:ktor-client-core:2.1.3")
                implementation("io.ktor:ktor-client-cio:2.1.3")
                implementation("io.ktor:ktor-client-websockets:2.1.3")
                implementation("io.ktor:ktor-client-content-negotiation:2.1.3")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.1.3")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.4.1")
                implementation("co.touchlab:stately-concurrency:1.2.2")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val androidMain by getting {
            kotlin.srcDir("src/commonMain/kotlin")

            dependencies {
                implementation("com.squareup.okhttp3:okhttp:4.10.0")
            }
        }
        val androidTest by getting
    }
}

android {
    compileSdk = 33
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        targetSdk = 33
    }

    namespace = "eu.lepicekmichal.signalrkore"

    publishing {
        singleVariant("release")

        publications {
            create<MavenPublication>("release") {
                groupId = "eu.lepicekmichal.signalrkore"
                artifactId = "core"
                version = "0.0.1"

                from(components["release"])

                pom {
                    name.set("SignalR Kore")
                    description.set("Connect to SignalR Core server with library written in Kotlin and coroutines.")
//                url.set("http://www.example.com/library")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("lepicekmichal")
                            name.set("Michal Lepíček")
                        }
                    }
                    /*scm {
                        connection.set("scm:git:git://example.com/my-library.git")
                        developerConnection.set("scm:git:ssh://example.com/my-library.git")
                        url.set("http://example.com/my-library/")
                    }*/
                }
            }
        }
    }
}