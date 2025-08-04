package com.arx.routes

import com.arx.db.MongoDataSource
import com.arx.models.Match
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.log
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.application
import io.ktor.server.routing.post
import org.bson.types.ObjectId
import org.litote.kmongo.eq

fun Route.matchRoutes() {
    route("/matches") {

        get {
            try {
                val matches: List<Match> = MongoDataSource.matchCollection.find().toList()
                call.respond(matches)
            } catch (e: Exception) {
                application.log.error("Failed to retrieve matches", e)
                call.respond(HttpStatusCode.InternalServerError, "Unable to retrieve matches.")
            }
        }

        get("/{id}") {
            val id = call.parameters["id"]
            if (id == null || !ObjectId.isValid(id)) {
                call.respond(HttpStatusCode.BadRequest, "Invalid or missing ID.")
                return@get
            }

            try {
                val match = MongoDataSource.matchCollection.findOne(Match::id eq id)
                if (match != null) {
                    call.respond(match)
                } else {
                    call.respond(HttpStatusCode.NotFound, "Match not found with id: $id")
                }
            } catch (e: Exception) {
                application.log.error("Error retrieving match with id=$id", e)
                call.respond(HttpStatusCode.InternalServerError, "Error retrieving match.")
            }
        }

        post {
            try {
                val match = call.receive<Match>()
                MongoDataSource.matchCollection.insertOne(match)
                call.respond(HttpStatusCode.Created, match)
            } catch (e: Exception) {
                application.log.error("Failed to create match", e)
                call.respond(HttpStatusCode.InternalServerError, "Failed to create match.")
            }
        }
    }
}
