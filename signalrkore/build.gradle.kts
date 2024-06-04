import eu.lepicekmichal.signalrkore.HubCommunicationTask
import org.gradle.internal.os.OperatingSystem

plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.kotlin.multiplatform)
    alias(libs.plugins.vanniktech.publish)
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
                implementation(libs.kotlin.stdlib.common)
                implementation(libs.ktor.core)
                implementation(libs.ktor.websockets)
                implementation(libs.ktor.content.negotiation)
                implementation(libs.ktor.serialization.kotlinx.json)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.datetime)
                implementation(libs.okio)
            }
        }
        val commonTest by getting {
            dependencies {
                implementation(kotlin("test"))
            }
        }
        val jvmMain by getting {
            dependencies {
                implementation(libs.okhttp)
                implementation(libs.ktor.okhttp)
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