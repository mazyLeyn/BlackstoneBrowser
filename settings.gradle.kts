pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        // Mozilla deposunu en temiz ve doğrudan URL yapısıyla ekliyoruz:
        maven {
            url = java.net.URI("https://maven.mozilla.org/maven2/")
        }
    }
}

rootProject.name = "BlackstoneBrowser"
include(":app")