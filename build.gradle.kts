plugins {
    java
    id("com.github.johnrengelman.shadow") version "7.0.0"
    id("io.papermc.paperweight.userdev") version "1.3.3"
}

repositories {
    mavenLocal()
    maven {
        url = uri("https://papermc.io/repo/repository/maven-public/")
    }

    maven {
        url = uri("https://oss.sonatype.org/content/groups/public/")
    }

    maven {
        url = uri("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    }

    maven {
        url = uri("https://nexus.neetgames.com/repository/maven-releases/")
    }

    maven {
        url = uri("https://repo.maven.apache.org/maven2/")
    }
}

dependencies {
    paperDevBundle("1.18.1-R0.1-SNAPSHOT")
    implementation("cloud.commandframework:cloud-annotations:1.6.1")
    implementation("cloud.commandframework:cloud-paper:1.6.1")
    implementation("org.spongepowered:configurate-hocon:4.2.0-20210903.044723-5")
    implementation("net.kyori:adventure-text-minimessage:4.2.0-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.10.10")
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.1.206")
}

group = "games.cultivate"
version = "0.0.1"
description = "MCMMOCredits"
java.sourceCompatibility = JavaVersion.VERSION_17

tasks{
    shadowJar {
        manifest {
            attributes(Pair("Main-Class", "games.cultivate.mcmmocredits.MCMMOCredits"))
        }
    }
}
