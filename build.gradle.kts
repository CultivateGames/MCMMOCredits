group = "games.cultivate"
version = "0.0.2-SNAPSHOT"
description = "MCMMOCredits"

/**
 * Includes necessary plugins for Gradle build script to execute.
 */
plugins {
    id ("java-library")
    id("com.github.johnrengelman.shadow") version "7.1.0"
    id("io.papermc.paperweight.userdev") version "1.3.3"
}

/**
 * Sets release version to Java 17
 */
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.neetgames.com/repository/maven-releases/")
    //Required for MCMMO to be included, for some reason. TODO: This needs to be fixed.
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")
    implementation("cloud.commandframework:cloud-annotations:1.6.1")
    implementation("cloud.commandframework:cloud-paper:1.6.1")
    implementation("org.spongepowered:configurate-hocon:4.2.0-SNAPSHOT")
    implementation("net.kyori:adventure-text-minimessage:4.10.0-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.10.10")
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.1.207") {
        //Required for MCMMO to be included, for some reason. TODO: This needs to be fixed.
        exclude(module = "worldguard-legacy")
    }
}
/**
 * Adds UTF-8 everywhere that we can, sets release version to Java 17.
 *
 * We are including only the classes we need to run, and relocating to
 * eliminate conflicts with other plugins who may use these libraries.
 */
tasks{
    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    shadowJar {
        minimize()
        //Cloud
        relocate("cloud.commandframework", "games.cultivate.mcmmocredits.relocate.cloud.commandframework")
        relocate("io.leangen", "games.cultivate.mcmmocredits.relocate.io.leangen")

        //Configurate
        relocate("org.spongepowered", "games.cultivate.mcmmocredits.relocate.org.spongepowered")
        relocate("com.typesafe", "games.cultivate.mcmmocredits.relocate.com.typesafe")

        //MiniMessage
        relocate("net.kyori.adventure.text", "games.cultivate.mcmmocredits.relocate.net.kyori.adventure.text")

        manifest {
            attributes(Pair("Main-Class", "games.cultivate.mcmmocredits.MCMMOCredits"))
        }
    }
}
