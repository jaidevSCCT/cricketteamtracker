package com.arx.routes

import com.arx.db.MongoDataSource
import com.arx.models.Match
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.log
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.application
import org.litote.kmongo.eq

fun Route.financeRoutes() {
    route("/finance") {

        // Overall total owner loss
        get("/total-loss") {
            try {
                val matches = MongoDataSource.matchCollection.find().toList()
                val totalLoss = matches.sumOf { ownerLossOf(it) }
                call.respond(mapOf("totalOwnerLoss" to totalLoss))
            } catch (e: Exception) {
                application.log.error("Failed to calculate total loss", e)
                call.respond(HttpStatusCode.InternalServerError, "Error calculating total loss")
            }
        }

        // Owner loss per match
        get("/by-match") {
            try {
                val matches = MongoDataSource.matchCollection.find().toList()
                val data = matches.map { m ->
                    mapOf(
                        "matchId" to m.id,
                        "date" to m.date,
                        "tournamentId" to m.tournamentId,
                        "ownerLoss" to ownerLossOf(m)
                    )
                }
                call.respond(data)
            } catch (e: Exception) {
                application.log.error("Failed to list by-match loss", e)
                call.respond(HttpStatusCode.InternalServerError, "Error listing by-match loss")
            }
        }

        // Total owner loss for a specific tournament
        get("/by-tournament/{tournamentId}") {
            val tid = call.parameters["tournamentId"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing tournamentId")
            try {
                val matches = MongoDataSource.matchCollection.find(Match::tournamentId eq tid).toList()
                val total = matches.sumOf { ownerLossOf(it) }
                call.respond(mapOf("tournamentId" to tid, "totalOwnerLoss" to total))
            } catch (e: Exception) {
                application.log.error("Failed to calculate tournament loss", e)
                call.respond(HttpStatusCode.InternalServerError, "Error calculating tournament loss")
            }
        }
    }
}

private fun ownerLossOf(m: Match): Int =
    // by construction this equals m.ownerMatchFee, but we derive to be robust
    m.participants.sumOf { it.coveredByOwner }
