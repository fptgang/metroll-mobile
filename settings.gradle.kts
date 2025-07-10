pluginManagement {
    includeBuild("build-logic")
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        google() // Essential for AndroidX

        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode = RepositoriesMode.FAIL_ON_PROJECT_REPOS
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
                includeGroupByRegex("org.chromium.*")
            }
        }
        maven {
            url = uri("https://api.mapbox.com/downloads/v2/releases/maven")
        }
        mavenCentral()
    }
}
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
rootProject.name = "metroll_mobile"
include(":app")
include(":common:base")
include(":common:theme")
include(":core:data")
include(":core:domain")
include(":core:datastore")
include(":feature:test")
include(":feature:auth")
include(":feature:home")
include(":feature:ticket")
include(":feature:route-management")
include(":feature:account")
include(":feature:membership")
include(":feature:staff")
include(":feature:qr-scanner")
