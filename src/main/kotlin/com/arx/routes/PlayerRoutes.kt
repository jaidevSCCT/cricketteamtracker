package com.arx.routes

import com.arx.db.MongoDataSource
import com.arx.models.Player
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.log
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.application
import io.ktor.server.routing.route
import org.litote.kmongo.eq
import java.util.Locale

fun Route.playerRoutes() {
    route("/players") {

        get {
            try {
                val players = MongoDataSource.playerCollection.find().toList()
                call.respond(players)
            } catch (e: Exception) {
                application.log.error("Failed to retrieve players", e)
                call.respond(HttpStatusCode.InternalServerError, "Unable to retrieve players.")
            }
        }

        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing player ID.")
            try {
                val player = MongoDataSource.playerCollection.findOne(Player::id eq id)
                if (player != null) call.respond(player) else call.respond(HttpStatusCode.NotFound, "Player not found")
            } catch (e: Exception) {
                application.log.error("Error retrieving player $id", e)
                call.respond(HttpStatusCode.InternalServerError, "Error retrieving player.")
            }
        }

        post {
            try {
                val incoming = call.receive<Player>()
                // ensure normalizedName is consistent if client didn't send it
                val norm = incoming.name.trim().lowercase(Locale.getDefault())
                val toSave = incoming.copy(normalizedName = incoming.normalizedName.ifBlank { norm })
                val result = MongoDataSource.playerCollection.insertOne(toSave)
                if (result.wasAcknowledged()) call.respond(HttpStatusCode.Created, toSave)
                else call.respond(HttpStatusCode.InternalServerError, "Failed to create player")
            } catch (e: Exception) {
                application.log.error("Failed to create player", e)
                call.respond(HttpStatusCode.InternalServerError, "Failed to create player: ${e.message ?: "Unknown error"}")
            }
        }
    }
}
