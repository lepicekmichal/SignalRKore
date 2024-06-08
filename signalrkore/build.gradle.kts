import eu.lepicekmichal.signalrkore.HubCommunicationTask
import org.gradle.internal.os.OperatingSystem

plugins {
    kotlin("multiplatform")
    kotlin("plugin.serialization")
    id("com.android.library")
    id("com.vanniktech.maven.publish.base")
}

group = requireNotNull(project.findProperty("GROUP"))
version = requireNotNull(project.findProperty("VERSION_NAME"))

kotlin {
    androidTarget {
        publishLibraryVariants("release")

        compilations.all {
            kotlinOptions {
                jvmTarget = JavaVersion.VERSION_17.toString()
            }
        }
    }

    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }

    if (OperatingSystem.current().isMacOsX) {
        listOf(
            iosArm64(),
            iosSimulatorArm64()
        ).forEach { iosTarget ->
            iosTarget.binaries.framework {
                baseName = "SignalRKore"
                isStatic = true
            }
        }
    }

    jvmToolchain(17)

    sourceSets {
        applyDefaultHierarchyTemplate()

        all {
            languageSettings {
                optIn("kotlin.RequiresOptIn")
            }
        }

        val commonMain by getting {
            kotlin.srcDir(project.layout.buildDirectory.dir("generated/kotlin").get().asFile)

            dependencies {
                implementation("org.jetbrains.kotlin:kotlin-stdlib-common:2.0.0")
                implementation("io.ktor:ktor-client-core:2.3.11")
                implementation("io.ktor:ktor-client-websockets:2.3.11")
                implementation("io.ktor:ktor-client-content-negotiation:2.3.11")
                implementation("io.ktor:ktor-serialization-kotlinx-json:2.3.11")
                implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.3")
                implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.0")
                implementation("com.squareup.okio:okio:3.9.0")
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
                implementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation("com.squareup.okhttp3:okhttp:4.12.0")
                implementation("io.ktor:ktor-client-okhttp:2.3.11")
            }
        }
        val androidMain by getting {
            dependsOn(jvmMain)
        }

        if (OperatingSystem.current().isMacOsX) {
            iosMain.dependencies {

            }
        }
    }
}

android {
    compileSdk = 34
    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")

    defaultConfig {
        minSdk = 21
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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

tasks.register<HubCommunicationTask>("HubCommunicationGeneration") {
    this.group = "build"
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
    dependsOn += tasks["HubCommunicationGeneration"]
}