plugins {
    id("com.android.library") version "7.4.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    kotlin("plugin.serialization") version "1.8.10" apply false
    kotlin("multiplatform") version "1.8.10" apply false
    id("org.jetbrains.compose") version "1.3.1" apply false
    id("com.vanniktech.maven.publish.base") version "0.25.1" apply false
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = "1.8.10"))
        classpath("com.android.tools.build:gradle:7.4.2")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}