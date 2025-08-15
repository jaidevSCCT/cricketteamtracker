plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.arx"
version = "0.0.1"
val ktorVersion = "2.3.12"

application {
    mainClass.set("com.arx.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true", "-Dio.ktor.config=application.yaml")
}

repositories {
    mavenCentral()
    maven("https://jitpack.io")  // Required for dotenv-kotlin
}

dependencies {
    // Ktor core
    implementation("io.ktor:ktor-server-core:${ktorVersion}")
    implementation("io.ktor:ktor-server-netty:${ktorVersion}")
    implementation("io.ktor:ktor-server-content-negotiation:${ktorVersion}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
    implementation("io.ktor:ktor-server-cors:${ktorVersion}")
    
    // OpenAPI and Swagger
    implementation("io.ktor:ktor-server-openapi:${ktorVersion}")
    implementation("io.ktor:ktor-server-swagger:${ktorVersion}")

    // MongoDB
    implementation("org.litote.kmongo:kmongo-coroutine:4.11.0")
    
    // Kotlinx Serialization DateTime for Instant serialization
    implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.4.1")
    
    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.13")

    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.19.2")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.19.1")


    // Environment variables
    implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

    // Kotlin Test + JUnit 5
    testImplementation(kotlin("test"))
    testImplementation("org.junit.jupiter:junit-jupiter:5.10.0")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
}

// Configure Ktor plugin
ktor {
    fatJar {
        archiveFileName.set("cricketteamtracker.jar")
    }
}
