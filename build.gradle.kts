import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.dsl.KotlinCommonCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinJvmCompile
import org.jetbrains.kotlin.gradle.tasks.KotlinNativeCompile

plugins {
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.kotlin.multiplatform) apply false
    alias(libs.plugins.vanniktech.publish) apply false
    alias(libs.plugins.dokka)
    id("org.jetbrains.compose") version "1.8.2" apply false
    id("org.jetbrains.kotlin.plugin.compose") version "2.2.0" apply false
}

buildscript {
    dependencies {
        classpath(kotlin("gradle-plugin", version = libs.versions.kotlin.get()))
        classpath(libs.gradle)
    }
}

allprojects {
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_1_8)
        }
    }
    tasks.withType<KotlinCommonCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
            freeCompilerArgs.add("-Xcontext-parameters")
            freeCompilerArgs.add("-Xwhen-guards")

            optIn.add("kotlin.time.ExperimentalTime")
        }
    }
    tasks.withType<KotlinJvmCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.add("-Xexpect-actual-classes")
            freeCompilerArgs.add("-Xcontext-parameters")
            freeCompilerArgs.add("-Xwhen-guards")

            optIn.add("kotlin.time.ExperimentalTime")
        }
    }
    tasks.withType<KotlinNativeCompile>().configureEach {
        compilerOptions {
            freeCompilerArgs.add("-Xcontext-parameters")

            optIn.add("kotlin.time.ExperimentalTime")
        }
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}
