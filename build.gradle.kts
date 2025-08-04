
plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.ktor)
    alias(libs.plugins.kotlin.plugin.serialization)
}

group = "com.arx"
version = "0.0.1"

application {
    mainClass.set("com.arx.ApplicationKt")
    applicationDefaultJvmArgs = listOf("-Dio.ktor.development=true", "-Dio.ktor.config=application.yaml")
}


repositories {
    mavenCentral()
}

dependencies {
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.server.content.negotiation)
    implementation(libs.ktor.serialization.gson)
    implementation(libs.ktor.server.netty)
    implementation(libs.logback.classic)
    implementation(libs.ktor.server.config.yaml)
    implementation("org.litote.kmongo:kmongo-coroutine:4.11.0")
    
    // Test dependencies
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
    
    // MongoDB test dependencies
    testImplementation("de.flapdoodle.embed:de.flapdoodle.embed.mongo:4.9.2")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.8.0")
    testImplementation("io.ktor:ktor-client-json:2.3.7")
    testImplementation("io.ktor:ktor-client-serialization:2.3.7")
    testImplementation("io.ktor:ktor-client-content-negotiation:2.3.7")
    
    // For JUnit 5
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.0")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.0")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

tasks.test {
    useJUnitPlatform()
    testLogging {
        events("passed", "skipped", "failed")
    }
}
