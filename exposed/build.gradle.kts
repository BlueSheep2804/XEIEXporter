import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.jetbrains.kotlin.gradle.utils.extendsFrom

plugins {
    id("idea")
    id("java-library")
    id("net.neoforged.moddev.legacyforge") version "2.0.91"
    id("com.gradleup.shadow") version "9.3.1"
}

@Suppress("ConstPropertyName")
object ModInfo {
    const val minecraft_version = "1.20.1"
    const val minecraft_version_range = "[1.20.1,1.21)"
    const val forge_version = "47.4.0"
    const val forge_version_range = "[47.4,)"
    const val loader_version_range = "[47,)"

    const val mod_id = "exposed_provider"
    const val mod_name = "Exposed Provider"
    const val mod_license = "Apache License 2.0"
    const val mod_version = "1.0.0-beta-4"
    const val mod_group_id = "dev.bluesheep"
    const val mod_authors = "BlueSheep"
    val mod_description = """
        A mod that enables the use of the Exposed library in other mods.
    """.trim()
}

version = ModInfo.mod_version
group = ModInfo.mod_group_id

base {
    archivesName = ModInfo.mod_id
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

legacyForge {
    version = ModInfo.minecraft_version + "-" + ModInfo.forge_version

    runs {
        register("client") {
            client()

            systemProperty ("forge.enabledGameTestNamespaces", ModInfo.mod_id)
        }

        register("server") {
            server()
            programArgument ("--nogui")
        }

        configureEach {
            systemProperty ("forge.logging.markers", "REGISTRIES")
            systemProperty ("forge.enabledGameTestNamespaces", ModInfo.mod_id)

            jvmArgument("-XX:+AllowEnhancedClassRedefinition")

            logLevel = org.slf4j.event.Level.DEBUG
        }
    }

    mods {
        register(ModInfo.mod_id) {
            sourceSet(sourceSets["main"])
        }
    }
}

sourceSets["main"].resources { srcDir("src/generated/resources") }

configurations {
    runtimeClasspath.extendsFrom(configurations.register("localRuntime"))
}
obfuscation {
    createRemappingConfiguration(configurations.getAt("localRuntime"))
}

repositories {
    mavenCentral()
}

dependencies {
    "shadow"(implementation("org.jetbrains.exposed:exposed-core:${ModInfo.mod_version}") {
        isTransitive = false
    })
    "shadow"(implementation("org.jetbrains.exposed:exposed-dao:${ModInfo.mod_version}") {
        isTransitive = false
    })
    "shadow"(implementation("org.jetbrains.exposed:exposed-jdbc:${ModInfo.mod_version}") {
        isTransitive = false
    })
    "shadow"(implementation("org.jetbrains.exposed:exposed-json:${ModInfo.mod_version}") {
        isTransitive = false
    })
}

var generateModMetadata = tasks.register<ProcessResources>("generateModMetadata") {
    var replaceProperties = mapOf(
        "minecraft_version" to ModInfo.minecraft_version,
        "minecraft_version_range" to ModInfo.minecraft_version_range,
        "forge_version" to ModInfo.forge_version,
        "forge_version_range" to ModInfo.forge_version_range,
        "loader_version_range" to ModInfo.loader_version_range,
        "mod_id" to ModInfo.mod_id,
        "mod_name" to ModInfo.mod_name,
        "mod_license" to ModInfo.mod_license,
        "mod_version" to ModInfo.mod_version,
        "mod_authors" to ModInfo.mod_authors,
        "mod_description" to ModInfo.mod_description
    )
    inputs.properties(replaceProperties)
    expand(replaceProperties)
    from(project.file("src/main/templates"))
    into(project.file("build/generated/sources/modMetadata"))
}

sourceSets["main"].resources.srcDir(generateModMetadata)

legacyForge.ideSyncTask(generateModMetadata)

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
}

tasks.named<ShadowJar>("shadowJar") {
    configurations.set(listOf(project.configurations.getAt("shadow")))
    mergeServiceFiles()
    append("META-INF/LICENSE")
    append("META-INF/NOTICE")

    from("LICENSE_exposed")
    from("LICENSE") {
        rename { "${it}_${ModInfo.mod_id}" }
    }
}

artifacts {
    add("shadow", tasks.shadowJar)
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
