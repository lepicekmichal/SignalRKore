# Installation

SignalRKore is available on Maven Central. You can add it to your project using Gradle or Maven.

## Gradle

### Kotlin DSL

Add the following to your `build.gradle.kts` file:

```kotlin
dependencies {
    implementation("eu.lepicekmichal.signalrkore:signalrkore:${signalrkoreVersion}")
}
```

### Groovy DSL

Add the following to your `build.gradle` file:

```groovy
dependencies {
    implementation "eu.lepicekmichal.signalrkore:signalrkore:${signalrkoreVersion}"
}
```

## Maven

Add the following to your `pom.xml` file:

```xml
<dependency>
    <groupId>eu.lepicekmichal.signalrkore</groupId>
    <artifactId>signalrkore</artifactId>
    <version>${signalrkoreVersion}</version>
</dependency>
```

## Multiplatform Setup

SignalRKore is a Kotlin Multiplatform library that supports Android, JVM, and iOS platforms.

### Android

No additional setup is required for Android projects. Just add the dependency as shown above.

### JVM

No additional setup is required for JVM projects. Just add the dependency as shown above.

### iOS

For iOS projects, you need to include the SignalRKore framework in your Xcode project.

#### Swift Package Manager

Currently, SignalRKore is not available via Swift Package Manager. You need to use Kotlin Multiplatform Mobile (KMM) to include it in your iOS project.

#### Kotlin Multiplatform Mobile (KMM)

If you're using KMM, you can add SignalRKore as a dependency to your shared module:

```kotlin
kotlin {
    sourceSets {
        val commonMain by getting {
            dependencies {
                implementation("eu.lepicekmichal.signalrkore:signalrkore:${signalrkoreVersion}")
            }
        }
    }
}
```

Then, you can access SignalRKore from your iOS app through the shared module.

## Requirements

- Kotlin 2.0.0 or higher
- Ktor 3.0.0 or higher
- Kotlinx Serialization 1.7.0 or higher
- Kotlinx Coroutines 1.9.0 or higher

## Next Steps

Once you have added SignalRKore to your project, you can proceed to the [Quick Start](quick-start.md) guide to learn how to use it.