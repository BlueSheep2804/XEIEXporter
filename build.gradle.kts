import org.jetbrains.kotlin.gradle.utils.extendsFrom

plugins {
    id ("idea")
    id ("java-library")
    id ("maven-publish")
    id ("net.neoforged.moddev.legacyforge") version "2.0.91"
    id ("org.jetbrains.kotlin.jvm") version "2.2.21"
}

tasks.named<Wrapper>("wrapper").configure {
    distributionType = Wrapper.DistributionType.BIN
}

@Suppress("ConstPropertyName")
object ModInfo {
    const val minecraft_version = "1.20.1"
    const val minecraft_version_range = "[1.20.1,1.21)"
    const val parchment_mappings_version = "2023.09.03"
    const val forge_version = "47.4.0"
    const val forge_version_range = "[47.4,)"
    const val loader_version_range = "[4.12,)"

    const val mod_id = "xeiexporter"
    const val mod_name = "XEIEXporter"
    const val mod_license = "MIT"
    const val mod_version = "0.1.0"
    const val mod_group_id = "dev.bluesheep"
    const val mod_authors = "BlueSheep"
    const val mod_description = ""

    const val jei_version = "15.20.0.112"
    const val exposed_version = "1.0.0-beta-4"
}

version = ModInfo.mod_version
group = ModInfo.mod_group_id

base {
    archivesName = ModInfo.mod_id
}

java.toolchain.languageVersion = JavaLanguageVersion.of(17)

kotlin {
    jvmToolchain(17)
}

legacyForge {
    version = ModInfo.minecraft_version + "-" + ModInfo.forge_version

    parchment {
        mappingsVersion = ModInfo.parchment_mappings_version
        minecraftVersion = ModInfo.minecraft_version
    }

    // accessTransformers = project.files("src/main/resources/META-INF/accesstransformer.cfg")

    runs {
        register("client") {
            client()

            systemProperty ("forge.enabledGameTestNamespaces", ModInfo.mod_id)
        }

        register("server") {
            server()
            programArgument ("--nogui")
            systemProperty ("forge.enabledGameTestNamespaces", ModInfo.mod_id)
        }

        register("gameTestServer") {
            type = "gameTestServer"
            systemProperty ("forge.enabledGameTestNamespaces", ModInfo.mod_id)
        }

        register("data") {
            data()

            // gameDirectory = project.file("run-data")

            programArguments.addAll("--mod", ModInfo.mod_id, "--all", "--output", file("src/generated/resources/").getAbsolutePath(), "--existing", file("src/main/resources/").getAbsolutePath())
        }

        configureEach {
            // Recommended logging data for a userdev environment
            // The markers can be added/remove as needed separated by commas.
            // "SCAN": For mods scan.
            // "REGISTRIES": For firing of registry events.
            // "REGISTRYDUMP": For getting the contents of all registries.
            systemProperty ("forge.logging.markers", "REGISTRIES")

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
    mavenLocal()

    flatDir {
        dir("libs")
    }

    maven {
        name = "ModMaven"
        url = uri("https://modmaven.dev")
        content {
            includeGroup("mezz.jei")
        }
    }
    maven {
        name = "Kotlin for Forge"
        url = uri("https://thedarkcolour.github.io/KotlinForForge/")
        content {
            includeGroup("thedarkcolour")
        }
    }
}

dependencies {
    annotationProcessor("org.spongepowered:mixin:0.8.5:processor")

    implementation("thedarkcolour:kotlinforforge:4.12.0")

    jarJar(modRuntimeOnly("dev.bluesheep:exposed_provider:${ModInfo.exposed_version}")!!)
    compileOnly("org.jetbrains.exposed:exposed-core:${ModInfo.exposed_version}")
    compileOnly("org.jetbrains.exposed:exposed-dao:${ModInfo.exposed_version}")
    compileOnly("org.jetbrains.exposed:exposed-jdbc:${ModInfo.exposed_version}")

    implementation("org.postgresql:postgresql:42.7.7")
    "additionalRuntimeClasspath"("org.postgresql:postgresql:42.7.7")

    modCompileOnly("mezz.jei:jei-${ModInfo.minecraft_version}-common-api:${ModInfo.jei_version}")
    modCompileOnly("mezz.jei:jei-${ModInfo.minecraft_version}-forge-api:${ModInfo.jei_version}")
    "modLocalRuntime"("mezz.jei:jei-${ModInfo.minecraft_version}-forge:${ModInfo.jei_version}")
}

mixin {
    add(sourceSets["main"], "${ModInfo.mod_id}.refmap.json")
    config("${ModInfo.mod_id}.mixins.json")
}

tasks.named<Jar>("jar").configure {
    manifest.attributes(mapOf(
            "MixinConfigs" to "${ModInfo.mod_id}.mixins.json"
    ))
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
    from("src/main/templates")
    into("build/generated/sources/modMetadata")
}

sourceSets["main"].resources.srcDir(generateModMetadata)

legacyForge.ideSyncTask(generateModMetadata)

tasks.withType<JavaCompile>().configureEach {
    options.encoding = "UTF-8" // Use the UTF-8 charset for Java compilation
}

idea {
    module {
        isDownloadSources = true
        isDownloadJavadoc = true
    }
}
