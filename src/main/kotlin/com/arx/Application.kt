package com.arx

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


// Required for application.yaml to work
fun Application.module() {
    install(ContentNegotiation) {
        json()
    }

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
