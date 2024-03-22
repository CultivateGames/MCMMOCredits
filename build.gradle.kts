group = "games.cultivate"
version = "1.0.0-SNAPSHOT"

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    alias(libs.plugins.runPaper)
    alias(libs.plugins.shadow)
    alias(libs.plugins.licenser)
    alias(libs.plugins.versions)
    alias(libs.plugins.indra.core)
    alias(libs.plugins.indra.publishing.sonatype)
    alias(libs.plugins.gremlin.gradle)
}

repositories {
    mavenCentral()
    maven("https://papermc.io/repo/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.neetgames.com/repository/maven-releases/")
}

dependencies {
    api(libs.cloud.annotations)
    api(libs.cloud.paper)
    api(libs.configurate)
    api(libs.h2)
    api(libs.jdbi.core)
    api(libs.guice)
    api(libs.hikari)
    runtimeDownload(libs.cloud.annotations)
    runtimeDownload(libs.cloud.paper)
    runtimeDownload(libs.configurate)
    runtimeDownload(libs.h2)
    runtimeDownload(libs.jdbi.core)
    runtimeDownload(libs.guice)
    runtimeDownload(libs.hikari)
    implementation(libs.bstats)
    testImplementation(platform(libs.junit.bom))
    testImplementation(libs.junit.jupiter)
    testImplementation(libs.mockito.core)
    testImplementation(libs.mockito.jupiter)
    testImplementation(libs.paper)
    testImplementation(libs.placeholderApi)
    testImplementation(libs.mcmmo) {
        isTransitive = false
    }
    compileOnly(libs.paper)
    compileOnly(libs.placeholderApi)
    compileOnly(libs.mcmmo) {
        isTransitive = false
    }
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

license {
    style.put("java", "DOUBLE_SLASH")
    newLine(false)
    exclude("**/*.yml")
}

configurations {
    runtimeDownload {
        exclude("io.papermc.paper")
        exclude("net.kyori")
        exclude("org.slf4j")
        exclude("org.ow2.asm")
    }
}

tasks {
    build {
        dependsOn(shadowJar)
    }
    compileJava {
        options.encoding = Charsets.UTF_8.name()
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
    writeDependencies {
        repos.set(repositories.filterIsInstance<MavenArtifactRepository>().map { it.url.toString() })
    }
    shadowJar {
        archiveClassifier.set("")
        dependencies {
            include(dependency(libs.gremlin.runtime.get()))
            include(dependency(libs.bstats.get()))
        }
        listOf("xyz.jpenilla.gremlin", "org.bstats").forEach { x ->
            relocate(x, "games.cultivate.mcmmocredits.deps.$x")
        }
        manifest {
            attributes(Pair("Main-Class", "games.cultivate.mcmmocredits.MCMMOCredits"))
        }
    }
}
