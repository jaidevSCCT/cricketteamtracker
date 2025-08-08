package com.arx

import com.arx.config.ConfigLoader
import io.ktor.server.application.Application
import io.ktor.server.application.install
import io.ktor.server.engine.embeddedServer
import io.ktor.server.netty.Netty
import io.ktor.server.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import io.ktor.server.routing.routing
import com.arx.routes.tournamentRoutes
import com.arx.routes.matchRoutes
import com.arx.routes.playerRoutes
import com.arx.routes.financeRoutes
import com.arx.plugins.configureOpenApi
import io.ktor.server.plugins.cors.routing.CORS
import io.ktor.http.HttpMethod

fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

    // Load CORS config using Jackson
    val corsConfig = ConfigLoader.loadCorsConfig()

    install(CORS) {
        corsConfig.allowedOrigins.forEach { host ->
            allowHost(host)
        }
        corsConfig.allowedMethods.forEach { method ->
            allowMethod(HttpMethod.parse(method))
        }
        corsConfig.allowedHeaders.forEach { header ->
            allowHeader(header)
        }
    }

    // Configure OpenAPI and Swagger UI
    configureOpenApi()

    routing {
        tournamentRoutes()
        matchRoutes()
        playerRoutes()
        financeRoutes()
    }
}

// Optional: allows direct run using ./gradlew run
fun main() {
    val port = System.getenv("PORT")?.toIntOrNull() ?: 9090
    val host = System.getenv("HOST") ?: "0.0.0.0"
    
    embeddedServer(Netty, port = port, host = host, module = Application::module).start(wait = true)
}
