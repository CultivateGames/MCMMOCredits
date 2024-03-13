group = "games.cultivate"
version = "0.4.6"
description = "MCMMOCredits"

plugins {
    `java-library`
    `maven-publish`
    signing
    alias(libs.plugins.runPaper)
    alias(libs.plugins.shadow)
    alias(libs.plugins.pluginYml)
    alias(libs.plugins.licenser)
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.neetgames.com/repository/maven-releases/")
}

dependencies {
    implementation(libs.bstats)
    implementation(libs.cloud.annotations)
    implementation(libs.cloud.paper)
    implementation(libs.configurate)
    implementation(libs.h2)
    implementation(libs.jdbi.core)
    implementation(libs.jdbi.sqlite)
    implementation(libs.guice)
    implementation(libs.hikari)
    implementation(libs.caffeine)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.jupiter)
    testImplementation(libs.jdbi.testing)
    compileOnly(libs.paper)
    compileOnly(libs.placeholderApi)
    compileOnly(libs.mcmmo) {
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
    foliaSupported = true
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
        //MCMMO spams legacy text and run-task adds this flag by default.
        systemProperty("net.kyori.adventure.text.warnWhenLegacyFormattingDetected", false)
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
