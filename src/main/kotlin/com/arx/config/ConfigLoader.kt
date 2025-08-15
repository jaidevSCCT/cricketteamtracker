package com.arx.config

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import com.arx.config.env.EnvLoader

object ConfigLoader {
    private val mapper = ObjectMapper(YAMLFactory()).registerKotlinModule()

    /** Default config file can be overridden via -DAPP_CONFIG=application-test.yaml */
    private fun defaultResource(): String =
        System.getProperty("APP_CONFIG") ?: "application.yaml"

    private fun readRoot(resource: String): com.fasterxml.jackson.databind.JsonNode {
        val expandedYaml = EnvLoader.readResourceExpanded(resource)
        return mapper.readTree(expandedYaml)
    }

    fun loadMongoConfig(resource: String = defaultResource()): MongoConfig {
        val rootNode = readRoot(resource)
        val mongoNode = rootNode.get("mongo") ?: error("Missing 'mongo' section in $resource")
        return mapper.treeToValue(mongoNode, MongoConfig::class.java)
    }

    fun loadCorsConfig(resource: String = defaultResource()): CorsConfig {
        val rootNode = readRoot(resource)
        val corsNode = rootNode.path("ktor").path("cors")
        if (corsNode.isMissingNode) error("Missing 'ktor.cors' section in $resource")
        return mapper.treeToValue(corsNode, CorsConfig::class.java)
    }
}
