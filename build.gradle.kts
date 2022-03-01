group = "games.cultivate"
version = "0.0.4"
description = "MCMMOCredits"

/**
 * Includes necessary plugins for Gradle build script to execute.
 */
plugins {
    id ("java-library")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.3.5-SNAPSHOT"
}

/**
 * Sets release version to Java 17
 */
java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
    //MiniMessage snapshots
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    //Maven Central
    mavenCentral()
    //Paper
    maven("https://papermc.io/repo/repository/maven-public/")
    //Configurate
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    //Placeholder API
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    //mcMMO
    maven("https://nexus.neetgames.com/repository/maven-releases/")
    //Interfaces
    maven("https://repo.incendo.org/content/repositories/snapshots/")
    //SkullCreator
    maven("https://github.com/deanveloper/SkullCreator/raw/mvn-repo/")
}

dependencies {
    //Paper NMS
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")
    //paperDevBundle("1.18.2-RO.1-SNAPSHOT")
    //Cloud Command Framework
    implementation("cloud.commandframework:cloud-annotations:1.6.2")
    implementation("cloud.commandframework:cloud-paper:1.6.2")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.6.2") {
        exclude(module = "adventure-text-minimessage")
    }
    //Incendo Interfaces
    implementation("org.incendo.interfaces:interfaces-paper:1.0.0-SNAPSHOT") {
        exclude(module = "paper-api")
    }
    //Configurate
    implementation("org.spongepowered:configurate-hocon:4.2.0-SNAPSHOT")
    //Adventure (MiniMessage and Serializers)
    implementation("net.kyori:adventure-text-minimessage:4.10.0-SNAPSHOT")
    //SkullCreator
    implementation("dev.dbassett:skullcreator:3.0.1")
    //PlaceholderAPI
    compileOnly("me.clip:placeholderapi:2.10.10")
    //mcMMO
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.1.209") {
        exclude(group = "com.sk89q.worldguard")
        exclude(group = "com.sk89q.worldedit")
    }
    //HikariCP
    implementation("com.zaxxer:HikariCP:5.0.1")
}

/**
 * Adds UTF-8 everywhere that we can, sets release version to Java 17.
 *
 * We are relocating to eliminate conflicts with other plugins who may use these libraries.
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
        //Incendo (Cloud / Interfaces)
        relocate("cloud.commandframework", "games.cultivate.mcmmocredits.relocate.cloud.commandframework")
        relocate("org.incendo.interfaces", "games.cultivate.mcmmocredits.relocate.org.incendo.interfaces")
        relocate("io.leangen", "games.cultivate.mcmmocredits.relocate.io.leangen")

        //Configurate
        relocate("org.spongepowered", "games.cultivate.mcmmocredits.relocate.org.spongepowered")
        relocate("com.typesafe", "games.cultivate.mcmmocredits.relocate.com.typesafe")

        //MiniMessage
        relocate("net.kyori.adventure.text.minimessage", "games.cultivate.mcmmocredits.relocate.net.kyori.adventure.text.minimessage")

        manifest {
            attributes(Pair("Main-Class", "games.cultivate.mcmmocredits.MCMMOCredits"))
        }
    }
}
