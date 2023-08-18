group = "games.cultivate"
version = "0.4.4-SNAPSHOT"
description = "MCMMOCredits"

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    id("xyz.jpenilla.run-paper") version "2.1.0"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("net.minecrell.plugin-yml.bukkit") version "0.6.0"
    id("org.cadixdev.licenser") version "0.6.1"
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.neetgames.com/repository/maven-releases/")
}

dependencies {
    implementation("org.bstats:bstats-bukkit:3.0.2")
    implementation("cloud.commandframework:cloud-annotations:1.8.3")
    implementation("cloud.commandframework:cloud-paper:1.8.3")
    implementation("org.spongepowered:configurate-yaml:4.2.0-SNAPSHOT")
    implementation("com.h2database:h2:2.2.220")
    implementation("com.google.inject:guice:7.0.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("com.github.ben-manes.caffeine:caffeine:3.1.7")
    implementation("org.jdbi:jdbi3-core:3.41.0")
    implementation("org.jdbi:jdbi3-sqlite:3.41.0")
    testImplementation("org.jdbi:jdbi3-testing:3.41.0")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockito:mockito-core:5.4.0")
    testImplementation("org.mockito:mockito-junit-jupiter:5.4.0")
    compileOnly("io.papermc.paper:paper-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("me.clip:placeholderapi:2.11.3") {
        exclude(group = "net.kyori")
    }
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.1.222") {
        exclude("com.sk89q.worldguard")
        exclude("com.sk89q.worldedit")
    }
}

java {
    withSourcesJar()
    withJavadocJar()
    toolchain.languageVersion.set(JavaLanguageVersion.of(17))
}

publishing {
    publications {
        create<MavenPublication>("MCMMOCredits") {
            from(components["java"])
            repositories {
                maven {
                    name = "OSSRH"
                    url = uri("https://s01.oss.sonatype.org/service/local/staging/deploy/maven2/")
                    credentials {
                        val sonatypeUsername: String? by project
                        val sonatypePassword: String? by project
                        username = sonatypeUsername
                        password = sonatypePassword
                    }
                }
            }
            pom {
                name.set("MCMMOCredits")
                description.set("MCMMOCredits")
                url.set("https://github.com/CultivateGames/MCMMOCredits")
                licenses {
                    license {
                        name.set("The MIT License")
                        url.set("https://opensource.org/licenses/MIT")
                    }
                }
                developers {
                    developer {
                        id.set("CultivateGames")
                        email.set("admin@cultivate.games")
                    }
                }
                scm {
                    connection.set("scm:git:https://github.com/CultivateGames/MCMMOCredits.git")
                    developerConnection.set("scm:git:ssh://git@github.com/CultivateGames/MCMMOCredits.git")
                    url.set("https://github.com/CultivateGames/MCMMOCredits")
                    ciManagement {
                        system.set("Github Actions")
                        url.set("https://github.com/CultivateGames/MCMMOCredits/actions")
                    }
                }
            }
        }
    }
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
    sign(publishing.publications)
}

bukkit {
    name = project.name
    version = project.version.toString()
    main = "games.cultivate.mcmmocredits.MCMMOCredits"
    apiVersion = "1.19"
    description = "A modern MCMMO Credits plugin."
    authors = listOf("CultivateGames")
    website = "https://cultivate.games/"
    softDepend = listOf("mcMMO", "PlaceholderAPI")
}

license {
    style.put("java", "DOUBLE_SLASH")
    newLine(false)
    exclude("**/*.yml")
}

configurations {
    testImplementation {
        extendsFrom(compileOnly.get())
    }
}

tasks {
    assemble {
        dependsOn(shadowJar)
    }

    test {
        useJUnitPlatform()
    }

    compileJava {
        options.encoding = Charsets.UTF_8.name()
        options.compilerArgs.add("-parameters")
    }

    javadoc {
        options.encoding = Charsets.UTF_8.name()
    }

    processResources {
        filteringCharset = Charsets.UTF_8.name()
    }

    runServer {
        minecraftVersion("1.20.1")
    }

    shadowJar {
        archiveClassifier.set("")
        minimize {
            exclude(dependency("com.h2database:h2"))
        }
        fun reloc(pkg: String) = relocate(pkg, "games.cultivate.mcmmocredits.relocate.$pkg")
        reloc("cloud.commandframework")
        reloc("com.github")
        reloc("com.google.common")
        reloc("com.google.inject")
        reloc("com.google.errorprone")
        reloc("com.google.j2objc")
        reloc("com.zaxxer")
        reloc("io.leangen")
        reloc("javax.annotation")
        reloc("javax.inject")
        reloc("org.aopalliance")
        reloc("org.bstats")
        reloc("org.checkerframework")
        reloc("org.jdbi")
        reloc("org.incendo")
        reloc("org.spongepowered")
        manifest {
            attributes(Pair("Main-Class", "games.cultivate.mcmmocredits.MCMMOCredits"))
        }
    }
}
