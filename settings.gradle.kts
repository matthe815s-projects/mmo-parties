pluginManagement {
    repositories {
        fun exclusiveMaven(url: String, filter: Action<InclusiveRepositoryContentDescriptor>) =
                exclusiveContent {
                    forRepository { maven(url) }
                    filter(filter)
                }
        exclusiveMaven("https://maven.fabricmc.net/") {
            includeGroup("net.fabricmc")
            includeGroup("fabric-loom")
        }
        exclusiveMaven("https://maven.parchmentmc.org") {
            includeGroupByRegex("org\\.parchmentmc.*")
        }
        maven("https://repo.spongepowered.org/repository/maven-public/") {
            content {
                includeGroupByRegex("org\\.spongepowered.*")
                includeGroupByRegex("net\\.minecraftforge.*")
            }
        }
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "org.spongepowered.mixin") {
                useModule("org.spongepowered:mixingradle:${requested.version}")
            }
        }
    }
}