package com.arx.routes

import com.arx.db.MongoDataSource
import com.arx.models.Match
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.log
import io.ktor.server.response.respond
import io.ktor.server.routing.route
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.application
import com.arx.models.Player


fun Route.financeRoutes() {
    route("/finance") {
        get("/total-loss") {
            try {
                val matches: List<Match> = MongoDataSource.matchCollection.find().toList()
                val players: List<Player> = MongoDataSource.playerCollection.find().toList()

                val totalLoss = calculateTotalLoss(matches, players)
                call.respond(mapOf("totalLoss" to totalLoss))
            } catch (e: Exception) {
                application.log.error("Failed to calculate total loss", e)
                call.respond(HttpStatusCode.InternalServerError, "Error calculating total loss")
            }
        }
    }
}

private fun calculateTotalLoss(
    matches: List<Match>,
    players: List<Player>
): Double {
    val unpaidPlayerFees = matches.sumOf { match ->
        val avgFee = match.ownerMatchFee
        val unpaidCount = match.unpaidPlayers.size
        unpaidCount * avgFee
    }

    val ownerMatchFees = matches.sumOf { it.ownerMatchFee }

    val paidToPlayers = players.sumOf { it.paidByOwner }

    return unpaidPlayerFees + ownerMatchFees + paidToPlayers
}
