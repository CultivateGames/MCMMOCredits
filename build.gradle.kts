group = "games.cultivate"
version = "1.0.0-SNAPSHOT"
description = "MCMMOCredits"

plugins {
    `java-library`
    `maven-publish`
    signing
    alias(libs.plugins.runPaper)
    alias(libs.plugins.shadow)
    alias(libs.plugins.pluginYml)
    alias(libs.plugins.licenser)
    alias(libs.plugins.versions)
    alias(libs.plugins.indra.core)
    alias(libs.plugins.indra.publishing.sonatype)
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
}

indraSonatype {
    useAlternateSonatypeOSSHost("s01")
}

indra {
    javaVersions {
        minimumToolchain(21)
        target(21)
    }
    mitLicense()
    github("CultivateGames", "MCMMOCredits") {
        ci(true)
        scm(true)
        issues(true)
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
        minecraftVersion("1.20.4")
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
