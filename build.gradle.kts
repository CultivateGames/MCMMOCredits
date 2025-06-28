group = "games.cultivate"
version = "1.0.0-SNAPSHOT"

plugins {
    id("java-library")
    id("maven-publish")
    id("signing")
    id("idea")
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
    maven("https://repo.opencollab.dev/maven-snapshots")
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://repo.extendedclip.com/content/repositories/placeholderapi/")
    maven("https://nexus.neetgames.com/repository/maven-releases/")
}

dependencies {
    api(libs.bundles.cloud)
    api(libs.h2)
    api(libs.jdbi.core)
    api(libs.guice)
    api(libs.guice.assisted)
    api(libs.hikari)
    api(libs.configurate.yaml)
    runtimeDownload(libs.configurate.yaml)
    runtimeDownload(libs.bundles.cloud)
    runtimeDownload(libs.h2)
    runtimeDownload(libs.jdbi.core)
    runtimeDownload(libs.guice)
    runtimeDownload(libs.guice.assisted)
    runtimeDownload(libs.hikari)
    implementation(libs.bstats)
    testImplementation(platform("org.junit:junit-bom:5.12.1"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testImplementation("org.mockbukkit.mockbukkit:mockbukkit-v1.21:4.41.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    testImplementation(libs.bundles.mockito)
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
        minimumToolchain(22)
        target(22)
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
    test {
        maxParallelForks = (Runtime.getRuntime().availableProcessors() / 2).coerceAtLeast(1)
        useJUnitPlatform()
    }
    assemble {
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
        minecraftVersion("1.21.4")
        systemProperty("net.kyori.adventure.text.warnWhenLegacyFormattingDetected", false)
        downloadPlugins {
            url("https://popicraft.net/jenkins/job/mcMMO/lastSuccessfulBuild/artifact/target/mcMMO.jar")
        }
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
