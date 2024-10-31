import me.modmuss50.mpp.PublishModTask
import net.minecraftforge.gradle.common.tasks.DownloadMavenArtifact
import net.minecraftforge.gradle.common.tasks.JarExec
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent

plugins {
    id("java")
    id("eclipse")
    id("idea")
    id("maven-publish")
    id("net.minecraftforge.gradle") version ("[6.0,6.2)")
    id("me.modmuss50.mod-publish-plugin") version ("0.7.4")
}

val version: String by extra
val group: String by extra
val modId: String by extra
val minecraftVersion: String by extra
val minecraftVersionRangeStart: String by extra
val modGroup: String by extra
val curseHomepageUrl: String by extra
val curseProjectId: String by extra
val modJavaVersion: String by extra
val forgeVersion: String by extra
val curseforgeApikey: String? by project

//adds the build number to the end of the version string if on a build server
var buildNumber = project.findProperty("BUILD_NUMBER")
if (buildNumber == null) {
    buildNumber = "9999"
}
var baseArchivesName = "${modId}-${buildNumber}-${minecraftVersion}"

base {
    archivesName.set(baseArchivesName)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(modJavaVersion))
    }
    withSourcesJar()
}

minecraft {
    // The mappings can be changed at any time and must be in the following format.
    // Channel:   Version:
    // official   MCVersion             Official field/method names from Mojang mapping files
    // parchment  YYYY.MM.DD-MCVersion  Open community-sourced parameter names and javadocs layered on top of official
    //
    // You must be aware of the Mojang license when using the 'official' or 'parchment' mappings.
    // See more information here: https://github.com/MinecraftForge/MCPConfig/blob/master/Mojang.md
    //
    // Parchment is an unofficial project maintained by ParchmentMC, separate from MinecraftForge
    // Additional setup is needed to use their mappings: https://parchmentmc.org/docs/getting-started
    //
    // Use non-default mappings at your own risk. They may not always work.
    // Simply re-run your setup task after changing the mappings to update your workspace.
    mappings("official", minecraftVersion)

    // When true, this property will have all Eclipse/IntelliJ IDEA run configurations run the "prepareX" task for the given run configuration before launching the game.
    // In most cases, it is not necessary to enable.
    // enableEclipsePrepareRuns = true
    // enableIdeaPrepareRuns = true

    // This property allows configuring Gradle's ProcessResources task(s) to run on IDE output locations before launching the game.
    // It is REQUIRED to be set to true for this template to function.
    // See https://docs.gradle.org/current/dsl/org.gradle.language.jvm.tasks.ProcessResources.html
    copyIdeResources.set(true)

    // When true, this property will add the folder name of all declared run configurations to generated IDE run configurations.
    // The folder name can be set on a run configuration using the "folderName" property.
    // By default, the folder name of a run configuration is the name of the Gradle project containing it.
    // generateRunFolders = true

    // This property enables access transformers for use in development.
    // They will be applied to the Minecraft artifact.
    // The access transformer file can be anywhere in the project.
    // However, it must be at "META-INF/accesstransformer.cfg" in the final mod jar to be loaded by Forge.
    // This default location is a best practice to automatically put the file in the right place in the final jar.
    // See https://docs.minecraftforge.net/en/latest/advanced/accesstransformers/ for more information.
    // accessTransformer = file('src/main/resources/META-INF/accesstransformer.cfg')

    // Default run configurations.
    // These can be tweaked, removed, or duplicated as needed.
    runs {
        val client = create("client") {
            taskName("runClientDev")
            property("forge.logging.console.level", "debug")
            workingDirectory(file("run/client/Dev"))
            mods {
                create(modId) {
                    source(sourceSets.main.get())
                }
            }
        }
        create("client_01") {
            taskName("runClientPlayer01")
            parent(client)
            workingDirectory(file("run/client/Player01"))
            args("--username", "Player01")
        }
        create("client_02") {
            taskName("runClientPlayer02")
            parent(client)
            workingDirectory(file("run/client/Player02"))
            args("--username", "Player02")
        }
        create("server") {
            taskName("Server")
            property("forge.logging.console.level", "debug")
            workingDirectory(file("run/server"))
            mods {
                create(modId) {
                    source(sourceSets.main.get())
                }
            }
        }
    }
}

var list = mutableListOf("common","forge","sources")

// Include resources generated by data generators.
sourceSets {
    main {
        java {
            setSrcDirs(list)
        }
    }
}

publishMods {
    file.set(tasks.jar.get().archiveFile)
    changelog = "Nightly build"
    type = BETA
    modLoaders.add("forge")
    displayName.set("${project.version}-$minecraftVersion")
    version.set(project.version.toString())

    curseforge {
        projectId = curseProjectId
        accessToken.set(curseforgeApikey ?: "0")
        changelog = "Nightly build"
        minecraftVersionRange {
            start = minecraftVersionRangeStart
            end = minecraftVersion
        }
        javaVersions.add(JavaVersion.toVersion(modJavaVersion))
    }
}

repositories {
    // Put repositories for dependencies here
    // ForgeGradle automatically adds the Forge maven and Maven Central for you

    // If you have mod jar dependencies in ./libs, you can declare them as a repository like so.
    // See https://docs.gradle.org/current/userguide/declaring_repositories.html#sub:flat_dir_resolver
    // flatDir {
    //     dir 'libs'
    // }
}

dependencies {
    // Specify the version of Minecraft to use.
    // Any artifact can be supplied so long as it has a "userdev" classifier artifact and is a compatible patcher artifact.
    // The "userdev" classifier will be requested and setup by ForgeGradle.
    // If the group id is "net.minecraft" and the artifact id is one of ["client", "server", "joined"],
    // then special handling is done to allow a setup of a vanilla dependency without the use of an external repository.
    "minecraft"(
            group = "net.minecraftforge",
            name = "forge",
            version = "${minecraftVersion}-${forgeVersion}"
    )

    // Example mod dependency with JEI - using fg.deobf() ensures the dependency is remapped to your development mappings
    // The JEI API is declared for compile time use, while the full JEI artifact is used at runtime
    // compileOnly fg.deobf("mezz.jei:jei-${mc_version}-common-api:${jei_version}")
    // compileOnly fg.deobf("mezz.jei:jei-${mc_version}-forge-api:${jei_version}")
    // runtimeOnly fg.deobf("mezz.jei:jei-${mc_version}-forge:${jei_version}")

    // Example mod dependency using a mod jar from ./libs with a flat dir repository
    // This maps to ./libs/coolmod-${mc_version}-${coolmod_version}.jar
    // The group id is ignored when searching -- in this case, it is "blank"
    // implementation fg.deobf("blank:coolmod-${mc_version}:${coolmod_version}")

    // For more info:
    // http://www.gradle.org/docs/current/userguide/artifact_dependencies_tutorial.html
    // http://www.gradle.org/docs/current/userguide/dependency_management.html
}

tasks.jar {
    from(sourceSets.main.get().output)

    duplicatesStrategy = DuplicatesStrategy.EXCLUDE
}

// Required because FG, copied from the MDK
sourceSets.forEach {
    val outputDir = layout.buildDirectory.file("sourcesSets/${it.name}").get().asFile
    it.output.setResourcesDir(outputDir)
    it.java.destinationDirectory.set(outputDir)
}
