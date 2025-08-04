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
}

dependencies {
    // Ktor core
    implementation("io.ktor:ktor-server-core:${ktorVersion}")
    implementation("io.ktor:ktor-server-netty:${ktorVersion}")
    implementation("io.ktor:ktor-server-content-negotiation:${ktorVersion}")
    implementation("io.ktor:ktor-serialization-kotlinx-json:${ktorVersion}")
// OpenAPI
    implementation("io.ktor:ktor-server-openapi:${ktorVersion}")
    implementation("io.ktor:ktor-server-swagger:${ktorVersion}")

    // MongoDB
    implementation("org.litote.kmongo:kmongo-coroutine:4.11.0")
    // Logging
    implementation("ch.qos.logback:logback-classic:1.5.13")

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
