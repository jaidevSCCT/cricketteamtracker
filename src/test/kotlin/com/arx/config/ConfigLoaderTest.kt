package com.arx.config

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.BeforeEach

class ConfigLoaderTest {
    private lateinit var mongoConfig: MongoConfig
    private lateinit var corsConfig: CorsConfig

    @BeforeEach
    fun setUp() {
        try {
            mongoConfig = ConfigLoader.loadMongoConfig()
        } catch (e: Exception) {
            println("Failed to load mongo config: ${e.message}")
            throw e
        }

        try {
            corsConfig = ConfigLoader.loadCorsConfig()
        } catch (e: Exception) {
            println("Failed to load cors config: ${e.message}")
            throw e
        }
    }


    @Test
    fun `should load mongo configuration from yaml`() {
        println("Loaded mongo URI: ${mongoConfig.uri}")
        println("Loaded mongo DB: ${mongoConfig.database}")

        assertEquals("mongodb://localhost:27017", mongoConfig.uri)
        assertEquals("cricket_db", mongoConfig.database)
    }

    @Test
    fun `should load cors configuration from yaml`() {
        assertEquals(
            listOf("localhost:9090", "127.0.0.1:9090"),
            corsConfig.allowedOrigins
        )
        assertEquals(
            listOf("GET", "POST", "PUT", "DELETE"),
            corsConfig.allowedMethods
        )
        assertEquals(
            listOf("Content-Type", "Authorization"),
            corsConfig.allowedHeaders
        )
    }
}