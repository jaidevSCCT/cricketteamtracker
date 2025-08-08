package com.arx.config

data class CorsConfig(
    val allowedOrigins: List<String> = emptyList(),
    val allowedMethods: List<String> = emptyList(),
    val allowedHeaders: List<String> = emptyList()
)
