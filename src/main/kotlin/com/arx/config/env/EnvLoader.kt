package com.arx.config.env

import io.github.cdimascio.dotenv.dotenv

/**
 * Loads environment values from (in order of precedence):
 * 1) JVM system properties (-DKEY=val)
 * 2) OS environment variables
 * 3) .env file at project root (optional)
 *
 * Also expands ${VAR} placeholders in text blobs (e.g., YAML).
 */
object EnvLoader {
    private val dotenv = dotenv {
        ignoreIfMissing = true   // .env optional in prod/CI
        ignoreIfMalformed = true
    }

    private val placeholder = Regex("""\$\{([A-Za-z0-9_]+)}""")

    /** Get a single value by key from sys props, env, or .env. */
    fun get(key: String): String? =
        System.getProperty(key)
            ?: System.getenv(key)
            ?: dotenv[key]

    /** Expand ${VAR} occurrences using the same precedence. */
    fun expand(text: String): String =
        placeholder.replace(text) { m ->
            val key = m.groupValues[1]
            get(key) ?: m.value // leave as-is if not found
        }

    /** Read a classpath resource and return its contents after expansion. */
    fun readResourceExpanded(name: String): String {
        val bytes = this::class.java.classLoader
            .getResourceAsStream(name)
            ?.readAllBytes()
            ?: error("Config file not found on classpath: $name")
        return expand(bytes.toString(Charsets.UTF_8))
    }
}
