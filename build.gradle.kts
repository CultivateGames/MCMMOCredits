group = "games.cultivate"
version = "0.1.0"
description = "MCMMOCredits"

plugins {
    id ("java-library")
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.3.6"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

repositories {
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
    //SkullCreator
    maven("https://github.com/deanveloper/SkullCreator/raw/mvn-repo/")
}

dependencies {
    paperDevBundle("1.19-R0.1-SNAPSHOT")
    //annotationProcessor("cloud.commandframework:cloud-annotations:1.7.0")
    implementation("cloud.commandframework:cloud-annotations:1.7.0")
    implementation("cloud.commandframework:cloud-paper:1.7.0")
    implementation("cloud.commandframework:cloud-minecraft-extras:1.7.0")
    implementation("com.google.inject:guice:5.1.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.spongepowered:configurate-hocon:4.2.0-SNAPSHOT")
    implementation("dev.dbassett:skullcreator:3.0.1")
    implementation("org.incendo.interfaces:interfaces-paper:1.0.0-SNAPSHOT") {
        exclude(module = "paper-api")
    }
    implementation("org.jdbi:jdbi3-core:3.31.0")
    implementation("org.jdbi:jdbi3-sqlobject:3.31.0")
    implementation("org.jdbi:jdbi3-sqlite:3.31.0")

    compileOnly("me.clip:placeholderapi:2.11.1")
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.1.214") {
        exclude("com.sk89q.worldguard")
        exclude("com.sk89q.worldedit")
    }
}

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.release.set(17)
        options.compilerArgs.add("-parameters")
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    shadowJar {
        relocate("cloud.commandframework", "games.cultivate.mcmmocredits.relocate.cloud.commandframework")
        relocate("org.incendo.interfaces", "games.cultivate.mcmmocredits.relocate.org.incendo.interfaces")
        relocate("io.leangen", "games.cultivate.mcmmocredits.relocate.io.leangen")
        relocate("com.google.inject", "games.cultivate.mcmmocredits.relocate.com.google.inject")
        relocate("org.spongepowered", "games.cultivate.mcmmocredits.relocate.org.spongepowered")
        relocate("com.typesafe", "games.cultivate.mcmmocredits.relocate.com.typesafe")

        manifest {
            attributes(Pair("Main-Class", "games.cultivate.mcmmocredits.MCMMOCredits"))
        }
    }
}
