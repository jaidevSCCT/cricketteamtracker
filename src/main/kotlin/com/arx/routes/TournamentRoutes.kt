package com.arx.routes

import com.arx.db.MongoDataSource
import com.arx.models.Tournament
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

fun Route.tournamentRoutes() {
    route("/tournaments") {
        get {
            try {
                val tournaments = MongoDataSource.tournamentCollection.find().toList()
                call.respond(tournaments)
            } catch (e: Exception) {
                application.log.error("Failed to retrieve tournaments", e)
                call.respond(HttpStatusCode.InternalServerError, "Unable to retrieve tournaments.")
            }
        }
        
        get("/{id}") {
            val id = call.parameters["id"]
            if (id == null || !ObjectId.isValid(id)) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID.")
                return@get
            }

            try {
                val tournament = MongoDataSource.tournamentCollection.findOne(Tournament::id eq id)
                if (tournament != null) {
                    call.respond(tournament)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Tournament not found with id: $id")
                }
            } catch (e: Exception) {
                application.log.error("Error retrieving tournament with id=$id", e)
                call.respond(HttpStatusCode.InternalServerError, "Error retrieving tournament.")
            }
        }
        
        post {
            try {
                val newTournament = call.receive<Tournament>()
                val result = MongoDataSource.tournamentCollection.insertOne(newTournament)
                if (result.wasAcknowledged()) {
                    call.respond(HttpStatusCode.Created, newTournament)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create tournament")
                }
            } catch (e: Exception) {
                application.log.error("Error creating tournament", e)
                call.respond(HttpStatusCode.InternalServerError, "Error creating tournament: ${e.message}")
            }
        }
    }
}
