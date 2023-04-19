pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}
plugins {
    id("com.gradle.enterprise") version "3.13"
}
gradleEnterprise {
    buildScan {
        termsOfServiceUrl = "https://gradle.com/terms-of-service"
        termsOfServiceAgree = "yes"
        obfuscation {
            username { name -> "" }
            hostname { hostname -> "" }
            ipAddresses { addresses -> addresses.map { address -> "" } }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "PhotoApp"
include(":app")
include(":common")
include(":repository")
include(":data:mediastore")
include(":test")
