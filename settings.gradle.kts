pluginManagement {
    repositories {
        google()
        gradlePluginPortal()
        mavenCentral()
    }
}
dependencyResolutionManagement {
//    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "SignalRKore"

include(
    ":androidApp",
    ":signalrkore",
)

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")