plugins {
    java
    alias(libs.plugins.shadow)
    alias(libs.plugins.run.paper)
}

group = "com.ytdd9527.networksexpansion"
version = "2.1.131"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
    maven("https://nexus.neetgames.com/repository/maven-public")
    maven("https://repo.bg-software.com/repository/api/")
    maven("https://repo.rosewooddev.io/repository/public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://s01.oss.sonatype.org/content/repositories/snapshots/")
    maven("https://repo.codemc.org/repository/maven-public/")
    maven("https://oss.sonatype.org/content/groups/public/")
    maven("https://repo.alessiodp.com/releases/")
    maven("https://maven.enginehub.org/repo/")
    maven("https://repo.alessiodp.com/releases")
    maven("https://repo.jeff-media.com/public")
}

dependencies {
    // Core
    compileOnly(libs.paper.api)
    compileOnly(libs.slimefun4)

    // Tools etc.
    implementation(libs.bstats)
    implementation(libs.more.persistent.data.types)
    implementation(libs.sefilib)
    implementation(libs.libby.bukkit)
    implementation(libs.big.interaction.menu)

    compileOnly(libs.findbugs.annotations) {
        exclude("net.jcip", "jcip-annotations")
        exclude("com.google.code.findbugs", "jsr305")
    }
    compileOnly(libs.lombok)
    annotationProcessor(libs.lombok)
    compileOnly(libs.pinyin)

    // Supported Plugins
    compileOnly(libs.infinity.expansion)
    compileOnly(libs.netheopoiesis)
    compileOnly(libs.slimehud)
    compileOnly(libs.wild.chests.api)
    compileOnly(libs.wild.stacker.api)
    compileOnly(libs.rosestacker)
    compileOnly(libs.mcmmo) {
        exclude("com.sk89q.worldedit", "worldedit-bukkit")
        exclude("com.sk89q.worldedit", "worldedit-core")
        exclude("com.sk89q.worldguard", "worldguard-legacy")
        exclude("com.comphenix.protocol", "ProtocolLib")
    }
    compileOnly(libs.guizhan.lib.plugin)
    compileOnly(libs.fluffy.machines)
    compileOnly(libs.gugu.slimefun.lib)
    compileOnly(libs.just.enough.guide)
    compileOnly(libs.tsingshan.technology)
    // System-scoped local JARs
    compileOnly(fileTree(mapOf("dir" to "lib", "include" to listOf("*.jar"))))

    testImplementation(platform("org.junit:junit-bom:5.10.2"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.withType<JavaExec>().configureEach {
    systemProperty("file.encoding", "UTF-8")
    systemProperty("sun.stdout.encoding", "UTF-8")
    systemProperty("sun.stderr.encoding", "UTF-8")
}

tasks {
    compileJava {
        options.compilerArgs.add("-Xlint:-removal")
        options.release = 21
    }

    processResources {
        filesMatching("plugin.yml") {
            expand(project.properties)
        }
    }

    shadowJar {
        archiveBaseName.set("NetworksExpansion")
        archiveVersion.set(project.version.toString())
        archiveClassifier.set("")

        minimize()

        // Relocations
        relocate("org.bstats", "io.github.sefiraat.networks.bstats")
        relocate("io.papermc.lib", "dev.sefiraat.cultivation.paperlib")
        relocate("net.byteflux.libby", "com.balugaq.netex.libraries.libby")

        // Exclude META-INF
        exclude("META-INF/*")

        duplicatesStrategy = DuplicatesStrategy.EXCLUDE
        mergeServiceFiles()
    }

    build {
        dependsOn(shadowJar)
    }

    runServer {
        dependsOn(shadowJar)
        val run = file(providers.gradleProperty("server.run.dir").orElse("run"))
        runDirectory.set(run)

        doFirst {
            run.resolve("eula.txt").writeText("eula=true")

            val pl = run.resolve("plugins")
            pl.mkdirs()
            copy {
                from(projectDir.resolve("build/libs")) {
                    include("${name}-${version}.jar")
                }
                into(pl)
            }
        }

        jvmArgs(
            "-Dfile.encoding=UTF-8",
            "-Dsun.jnu.encoding=UTF-8",
            "-agentlib:jdwp=transport=dt_socket,server=y,suspend=n,address=*:5001",
            "-Dnet.kyori.adventure.text.warn_when_legacy_formatting_detected=false"
        )
        maxHeapSize = "4G"
        minecraftVersion("26.2")
    }
}

// Set default tasks
defaultTasks("clean", "build")
