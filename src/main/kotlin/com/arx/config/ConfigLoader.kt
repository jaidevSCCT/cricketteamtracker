package com.arx.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule

object ConfigLoader {
    private val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

    fun loadMongoConfig(): MongoConfig {
        val input = this::class.java.classLoader.getResourceAsStream("application.yaml")
            ?: error("application.yaml not found in resources")
        val rootNode = mapper.readTree(input)
        val mongoNode = rootNode.get("mongo") ?: error("Missing 'mongo' section in YAML")
        return mapper.treeToValue(mongoNode, MongoConfig::class.java)
    }

    fun loadCorsConfig(): CorsConfig {
        val input = this::class.java.classLoader.getResourceAsStream("application.yaml")
            ?: error("application.yaml not found in resources")
        val rootNode = mapper.readTree(input)
        val corsNode = rootNode.path("ktor").path("cors")
        if (corsNode.isMissingNode) error("Missing 'ktor.cors' section in YAML")
        return mapper.treeToValue(corsNode, CorsConfig::class.java)
    }
}
