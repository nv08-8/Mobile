pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
        // Allow local AARs placed in app/libs via flatDir
        flatDir {
            dirs("${rootDir}/app/libs")
        }
    }
}

rootProject.name = "ViewFlipper_CricleIndicator"
include(":app")
