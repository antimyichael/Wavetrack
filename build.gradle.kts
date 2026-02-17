plugins {
    id("java")
    id("idea")
}

group = "com.wavetrack"
version = "1.0.0"

java {
    sourceCompatibility = JavaVersion.VERSION_1_8
    targetCompatibility = JavaVersion.VERSION_1_8
}

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
}

dependencies {
    compileOnly("org.bukkit:bukkit:1.8.8-R0.1-SNAPSHOT")
    testImplementation(platform("org.junit:junit-bom:5.10.0"))
    testImplementation("org.junit.jupiter:junit-jupiter")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

tasks.test {
    useJUnitPlatform()
}

tasks.jar {
    archiveFileName.set("WaveTrack-${version}.jar")
}

// ============================================
// Test Server Configuration
// ============================================

val serverDir = file("run")
val pluginsDir = file("run/plugins")
val serverJar = file("run/spigot-1.8.8.jar")

// URLs for server downloads (using GetBukkit archive)
val spigotDownloadUrl = "https://download.getbukkit.org/spigot/spigot-1.8.8-R0.1-SNAPSHOT-latest.jar"

/**
 * Task to set up the test server directory structure
 */
tasks.register("setupServer") {
    group = "server"
    description = "Sets up the test server directory structure"

    doLast {
        // Create directories
        serverDir.mkdirs()
        pluginsDir.mkdirs()

        // Create eula.txt (required to run server)
        val eulaFile = file("run/eula.txt")
        if (!eulaFile.exists()) {
            eulaFile.writeText("eula=true\n")
            println("Created eula.txt")
        }

        // Create server.properties with sensible defaults for testing
        val propsFile = file("run/server.properties")
        if (!propsFile.exists()) {
            propsFile.writeText("""
                # Minecraft server properties (configured for testing)
                server-port=25565
                online-mode=false
                spawn-protection=0
                max-players=20
                level-name=world
                gamemode=0
                difficulty=1
                spawn-monsters=true
                spawn-animals=true
                pvp=true
                enable-command-block=true
                motd=WaveTrack Test Server
                level-type=FLAT
                generate-structures=false
            """.trimIndent())
            println("Created server.properties")
        }

        println("Server directory set up at: ${serverDir.absolutePath}")
        println("")
        println("IMPORTANT: You need to manually download the Spigot 1.8.8 server JAR.")
        println("Due to DMCA and licensing, Spigot JARs cannot be directly downloaded.")
        println("")
        println("Option 1: Build Spigot yourself using BuildTools")
        println("  1. Download BuildTools: https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar")
        println("  2. Run: java -jar BuildTools.jar --rev 1.8.8")
        println("  3. Copy the resulting spigot-1.8.8-R0.1-SNAPSHOT.jar to: ${serverJar.absolutePath}")
        println("")
        println("Option 2: Use a pre-built JAR from a trusted source")
        println("  Place the Spigot 1.8.8 JAR at: ${serverJar.absolutePath}")
    }
}

/**
 * Task to copy the built plugin to the test server
 */
tasks.register<Copy>("copyPlugin") {
    group = "server"
    description = "Copies the built plugin JAR to the test server plugins folder"

    dependsOn(tasks.jar)

    from(tasks.jar.get().archiveFile)
    into(pluginsDir)

    doLast {
        println("Copied WaveTrack-${version}.jar to ${pluginsDir.absolutePath}")
    }
}

/**
 * Task to run the test server
 */
tasks.register<JavaExec>("runServer") {
    group = "server"
    description = "Runs the Spigot 1.8.8 test server with the plugin installed"

    dependsOn("setupServer", "copyPlugin")

    doFirst {
        if (!serverJar.exists()) {
            throw GradleException("""
                Server JAR not found at: ${serverJar.absolutePath}
                
                Please run './gradlew setupServer' and follow the instructions to obtain the Spigot JAR.
            """.trimIndent())
        }
    }

    workingDir = serverDir
    classpath = files(serverJar)
    mainClass.set("org.bukkit.craftbukkit.Main")

    // JVM arguments for the server
    jvmArgs = listOf(
        "-Xms512M",
        "-Xmx1G",
        "-XX:+UseG1GC",
        "-Dfile.encoding=UTF-8"
    )

    // Pass "nogui" to run without GUI
    args = listOf("nogui")

    // Enable console input
    standardInput = System.`in`
}

/**
 * Task to clean the test server (but keep the JAR)
 */
tasks.register<Delete>("cleanServer") {
    group = "server"
    description = "Cleans the test server world and logs (keeps server JAR and config)"

    delete(
        fileTree("run") {
            include("world/**")
            include("world_nether/**")
            include("world_the_end/**")
            include("logs/**")
            include("crash-reports/**")
        }
    )

    doLast {
        println("Cleaned server world and logs")
    }
}

/**
 * Task to fully reset the test server
 */
tasks.register<Delete>("resetServer") {
    group = "server"
    description = "Completely resets the test server directory (deletes everything except server JAR)"

    delete(
        fileTree("run") {
            exclude("spigot-1.8.8.jar")
        }
    )

    doLast {
        println("Reset server directory (kept server JAR if present)")
    }
}

/**
 * Quick rebuild and copy task
 */
tasks.register("deployPlugin") {
    group = "server"
    description = "Rebuilds the plugin and copies it to the test server"

    dependsOn(tasks.clean, tasks.jar, "copyPlugin")

    tasks.findByName("jar")?.mustRunAfter(tasks.clean)
    tasks.findByName("copyPlugin")?.mustRunAfter(tasks.jar)

    doLast {
        println("")
        println("Plugin deployed! Restart the server or use /reload to load changes.")
    }
}

