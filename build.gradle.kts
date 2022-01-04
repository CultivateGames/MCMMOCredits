group = "games.cultivate"
version = "0.0.1"
description = "MCMMOCredits"
java.sourceCompatibility = JavaVersion.VERSION_17

plugins {
    id ("java")
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("io.papermc.paperweight.userdev") version "1.3.3"
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.neetgames.com/repository/maven-releases/")
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
        exclude(module = "worldguard-legacy")
    }
}

tasks{
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "games.cultivate.mcmmocredits.MCMMOCredits"))
        }
    }
}
