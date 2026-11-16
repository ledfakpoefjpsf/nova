
// @name: Nova Schematic Printer
// @type: Gradle Kotlin DSL
// @location: build.gradle.kts

import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    id("fabric-loom") version "1.13.6"
    `maven-publish`
    id("org.jetbrains.kotlin.jvm") version "2.4.21"
}

group = project.property("maven_group") as String
version = project.property("mod_version") as String

base {
    archivesName.set(
        project.property("archives_base_name") as String
    )
}

repositories {
    mavenCentral()

    maven {
        name = "Fabric"
        url = uri("https://maven.fabricmc.net/")
    }
}

loom {
    splitEnvironmentSourceSets()

    mods {
        register("nova_schematic_printer") {
            sourceSet(sourceSets.main.get())
            sourceSet(sourceSets.getByName("client"))
        }
    }
}

fabricApi {
    configureDataGeneration {
        client = true
        modId = "nova_schematic_printer"
    }
}

dependencies {
    minecraft(
        "com.mojang:minecraft:${providers.gradleProperty("minecraft_version").get()}"
    )

    mappings(
        "net.fabricmc:yarn:${providers.gradleProperty("yarn_mappings").get()}:v2"
    )

    modImplementation(
        "net.fabricmc:fabric-loader:${providers.gradleProperty("loader_version").get()}"
    )

    modImplementation(
        "net.fabricmc.fabric-api:fabric-api:${providers.gradleProperty("fabric_api_version").get()}"
    )

    modImplementation(
        "net.fabricmc:fabric-language-kotlin:${providers.gradleProperty("fabric_kotlin_version").get()}"
    )
}

tasks.processResources {
    val projectVersion = project.version

    inputs.property("version", projectVersion)

    filesMatching("fabric.mod.json") {
        expand("version" to projectVersion)
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.release.set(21)
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_21)
    }
}

java {
    withSourcesJar()

    sourceCompatibility = JavaVersion.VERSION_21
    targetCompatibility = JavaVersion.VERSION_21
}

tasks.jar {
    val projectName = project.name

    inputs.property("projectName", projectName)

    from("LICENSE") {
        rename { "${it}_$projectName" }
    }
}

publishing {
    publications {
        register<MavenPublication>("mavenJava") {
            from(components["java"])
        }
    }

    repositories {
    }
}
