plugins {
    id("com.android.library") version "7.3.1" apply false
    id("org.jetbrains.kotlin.android") version "1.7.20" apply false
    kotlin("plugin.serialization") version "1.7.20" apply false
    kotlin("multiplatform") version "1.7.20" apply false
    id("org.jetbrains.compose") version "1.2.0" apply false
    id("com.vanniktech.maven.publish") version "0.22.0" apply false
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.7.20"))
        classpath("com.android.tools.build:gradle:7.3.1")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}