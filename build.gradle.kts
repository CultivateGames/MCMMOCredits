group = "games.cultivate"
version = "0.3.8-SNAPSHOT"
description = "MCMMOCredits"

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    id("xyz.jpenilla.run-paper") version "2.0.1"
    id("com.github.johnrengelman.shadow") version "8.1.1"
    id("io.papermc.paperweight.userdev") version "1.5.5"
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.neetgames.com/repository/maven-releases/")
    maven("https://maven.enginehub.org/repo/")
}

dependencies {
    paperweight.paperDevBundle("1.19.4-R0.1-SNAPSHOT")
    implementation ("org.bstats:bstats-bukkit:3.0.2")

    implementation("cloud.commandframework:cloud-annotations:1.8.3")
    implementation("cloud.commandframework:cloud-paper:1.8.3")

    implementation("org.spongepowered:configurate-yaml:4.2.0-SNAPSHOT")

    implementation("com.h2database:h2:2.1.214")
    implementation("com.google.inject:guice:5.1.0")
    implementation("com.zaxxer:HikariCP:5.0.1")
    implementation("org.jdbi:jdbi3-core:3.38.0")
    implementation("org.jdbi:jdbi3-sqlite:3.38.0")
    implementation("org.jdbi:jdbi3-sqlobject:3.38.0")

    implementation("org.incendo.interfaces:interfaces-paper:1.0.0-SNAPSHOT") {
        exclude(module = "paper-api")
    }

    compileOnly("me.clip:placeholderapi:2.11.3") {
        exclude(group = "net.kyori")
    }
    compileOnly("com.gmail.nossr50.mcMMO:mcMMO:2.1.218") {
        exclude("com.sk89q.worldguard")
        exclude("com.sk89q.worldedit")
    }

    testImplementation("org.junit.jupiter:junit-jupiter:5.9.2")
    testImplementation("org.mockito:mockito-inline:5.2.0")
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
            afterEvaluate {
                artifactId = tasks.jar.get().archiveBaseName.get()
            }
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

tasks {
    assemble {
        dependsOn(reobfJar)
    }

    test {
        useJUnitPlatform()
        testLogging {
            events("passed", "skipped", "failed")
        }
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
        minecraftVersion("1.19.4")
    }

    shadowJar {
        archiveClassifier.set("")
        minimize {
            exclude(dependency("com.h2database:h2:2.1.214"))
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
        reloc("org.bstats")
        reloc("org.checkerframework")
        reloc("org.enginehub")
        reloc("org.jdbi")
        reloc("org.incendo")
        reloc("org.spongepowered")
        manifest {
            attributes(Pair("Main-Class", "games.cultivate.mcmmocredits.MCMMOCredits"))
        }
    }
}
