
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
    testImplementation(libs.ktor.server.test.host)
    testImplementation(libs.kotlin.test.junit)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}
