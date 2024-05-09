plugins {
    id("com.android.library") version "8.4.0" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    kotlin("plugin.serialization") version "1.9.23" apply false
    kotlin("multiplatform") version "1.9.23" apply false
    id("org.jetbrains.compose") version "1.6.2" apply false
    id("com.vanniktech.maven.publish.base") version "0.28.0" apply false
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.9.23"))
        classpath("com.android.tools.build:gradle:8.4.0")
    }
}

allprojects {
    tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile>().configureEach {
        kotlinOptions {
            jvmTarget = JavaVersion.VERSION_17.toString()
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}