pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.10.0"
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "ArchitectureTemplate"

// App
include(":app")

// Core modules
include(":core:common")
include(":core:domain")
include(":core:data")
include(":core:network")
include(":core:database")
include(":core:ui")

// Feature modules — each split into domain / data / ui
include(":feature:auth:domain")
include(":feature:auth:data")
include(":feature:auth:ui")

include(":feature:home:domain")
include(":feature:home:data")
include(":feature:home:ui")

include(":feature:profile:domain")
include(":feature:profile:data")
include(":feature:profile:ui")

include(":feature:settings:domain")
include(":feature:settings:data")
include(":feature:settings:ui")

// Services
include(":services:sync")
