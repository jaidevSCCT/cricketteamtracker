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
import io.ktor.server.routing.route
import io.ktor.server.routing.application
import org.bson.types.ObjectId
import org.litote.kmongo.eq

fun Route.playerRoutes() {
    route("/players") {
        // Get all players
        get {
            try {
                val players = MongoDataSource.playerCollection.find().toList()
                call.respond(players)
            } catch (e: Exception) {
                application.log.error("Failed to retrieve players", e)
                call.respond(HttpStatusCode.InternalServerError, "Unable to retrieve players.")
            }
        }

        // Get player by ID
        get("/{id}") {
            val id = call.parameters["id"]
            if (id == null || !ObjectId.isValid(id)) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing player ID.")
                return@get
            }

            try {
                val player = MongoDataSource.playerCollection.findOne(Player::id eq id)
                if (player != null) {
                    call.respond(player)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Player not found with id: $id")
                }
            } catch (e: Exception) {
                application.log.error("Error retrieving player with id=$id", e)
                call.respond(HttpStatusCode.InternalServerError, "Error retrieving player.")
            }
        }

        // Create a new player
        post {
            try {
                val player = call.receive<Player>()
                val result = MongoDataSource.playerCollection.insertOne(player)
                if (result.wasAcknowledged()) {
                    call.respond(HttpStatusCode.Created, player)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create player")
                }
            } catch (e: Exception) {
                application.log.error("Failed to create player", e)
                call.respond(
                    HttpStatusCode.InternalServerError,
                    "Failed to create player: ${e.message ?: "Unknown error"}"
                )
            }
        }
    }
}