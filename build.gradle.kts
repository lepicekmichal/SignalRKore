plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.vanniktech.publish) apply false
    alias(libs.plugins.dokka)
    id("org.jetbrains.compose") version "1.6.11" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.0.0" apply false
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = libs.versions.kotlin.get()))
        classpath(libs.gradle)
    }
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_1_8.toString()
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
