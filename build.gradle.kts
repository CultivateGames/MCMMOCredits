group = "games.cultivate"
version = "0.3.2"
description = "MCMMOCredits"

plugins {
    id("java-library")
    id("maven-publish")
    id("xyz.jpenilla.run-paper") version "2.0.0"
    id("com.github.johnrengelman.shadow") version "7.1.2"
    id("io.papermc.paperweight.userdev") version "1.3.11"
    id("net.kyori.indra") version "3.0.1"
    id("net.kyori.indra.publishing") version "3.0.1"
    id("net.kyori.indra.publishing.sonatype") version "3.0.1"
}

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    withJavadocJar()
    withSourcesJar()
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.neetgames.com/repository/maven-releases/")
}

dependencies {
    paperDevBundle("1.19.3-R0.1-SNAPSHOT")
    implementation("cloud.commandframework:cloud-annotations:1.8.0")
    implementation("cloud.commandframework:cloud-paper:1.8.0")

    implementation("org.spongepowered:configurate-yaml:4.2.0-SNAPSHOT")

    implementation("org.incendo.interfaces:interfaces-paper:1.0.0-SNAPSHOT") {
        exclude(module = "paper-api")
    }

    implementation("com.google.inject:guice:5.1.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.jdbi:jdbi3-core:3.36.0")
    implementation("org.jdbi:jdbi3-sqlite:3.36.0")
    implementation("org.jdbi:jdbi3-sqlobject:3.36.0")

    compileOnly("me.clip:placeholderapi:2.11.2") {
        exclude(group = "net.kyori")
    }

    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.1.218") {
        exclude("com.sk89q.worldguard")
        exclude("com.sk89q.worldedit")
    }
}

indra {
    mitLicense()

    javaVersions {
        minimumToolchain(17)
        target(17)
    }

    github("CultivateGames", "MCMMOCredits") {
        ci(true)
    }

    configurePublications {
        pom {
            developers {
                developer {
                    id.set("CultivateGames")
                    email.set("admin@cultivate.games")
                }
            }
        }
    }
}

indraSonatype {
    useAlternateSonatypeOSSHost("s01")
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
    runServer {
        minecraftVersion("1.19.3")
    }
    //TODO: investigate transitive deps
    shadowJar {
        minimize {
            exclude(dependency("com.github.ben-manes.caffeine:caffeine:3.0.3"))
        }
        // https://github.com/PaperMC/paperweight-test-plugin/blob/shadow/build.gradle.kts
        fun reloc(pkg: String) = relocate(pkg, "games.cultivate.mcmmocredits.relocate.$pkg")
        reloc("cloud.commandframework")
        reloc("com.github")
        reloc("com.typesafe")
        reloc("com.zaxxer")
        reloc("io.leangen")
        reloc("javax.annotation")
        reloc("javax.inject")
        reloc("org.aopalliance")
        reloc("org.checkerframework")
        reloc("org.jdbi")
        reloc("org.incendo")
        reloc("org.spongepowered")

        manifest {
            attributes(Pair("Main-Class", "games.cultivate.mcmmocredits.MCMMOCredits"))
        }
    }
}