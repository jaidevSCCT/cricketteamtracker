package com.arx.routes

import com.arx.db.MongoDataSource
import com.arx.dto.MatchCreateRequest
import com.arx.dto.ParticipantInput
import com.arx.models.Match
import com.arx.models.Participant
import com.arx.models.Player
import com.arx.models.utils.PlayerTotals
import io.ktor.http.HttpStatusCode

import io.ktor.server.application.log
import io.ktor.server.request.receive
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.post
import io.ktor.server.routing.route
import io.ktor.server.routing.application

import org.litote.kmongo.eq
import org.litote.kmongo.inc
import org.litote.kmongo.combine
import org.litote.kmongo.set
import org.litote.kmongo.setTo
import org.litote.kmongo.EMPTY_BSON
import org.litote.kmongo.div

import java.time.Instant
import java.util.Locale

fun Route.matchRoutes() {
    route("/matches") {

        // list (optional filter by tournamentId)
        get {
            try {
                val tid = call.request.queryParameters["tournamentId"]
                val q = if (tid.isNullOrBlank()) EMPTY_BSON else Match::tournamentId eq tid
                val matches = MongoDataSource.matchCollection.find(q).toList()
                call.respond(matches)
            } catch (e: Exception) {
                application.log.error("Failed to retrieve matches", e)
                call.respond(HttpStatusCode.InternalServerError, "Unable to retrieve matches.")
            }
        }

        // get by id (UUID string, not ObjectId)
        get("/{id}") {
            val id = call.parameters["id"] ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing ID.")
            try {
                val match = MongoDataSource.matchCollection.findOne(Match::id eq id)
                if (match != null) call.respond(match) else call.respond(HttpStatusCode.NotFound, "Match not found")
            } catch (e: Exception) {
                application.log.error("Error retrieving match $id", e)
                call.respond(HttpStatusCode.InternalServerError, "Error retrieving match.")
            }
        }

        // create
        post {
            try {
                val req = call.receive<MatchCreateRequest>()
                if (req.participants.isEmpty()) return@post call.respond(
                    HttpStatusCode.BadRequest, "participants cannot be empty"
                )

                // Resolve players (upsert by normalizedName)
                val playerSnapshots = req.participants.map { p ->
                    val norm = p.name.trim().lowercase(Locale.getDefault())
                    val existing = MongoDataSource.playerCollection.findOne(Player::normalizedName eq norm)
                    val playerId = if (existing != null) {
                        existing.id
                    } else {
                        val created = Player(
                            name = p.name.trim(),
                            normalizedName = norm
                        )
                        MongoDataSource.playerCollection.insertOne(created)
                        created.id
                    }
                    p to playerId
                }

                // Build participants for Match
                val participants = playerSnapshots.map { (p: ParticipantInput, playerId: String) ->
                    Participant(
                        playerId = playerId,
                        name = p.name.trim(),
                        tag = p.tag,
                        paidByPlayer = p.paidByPlayer,
                        coveredByOwner = p.coveredByOwner
                    )
                }

                // Totals
                val totalMatchFee = (req.feePerPlayer.takeIf { it > 0 } ?: 0) * participants.size
                val ownerMatchFee = participants.sumOf { it.coveredByOwner }

                // (optional) sanity check: player's paid + owner coverage should equal total
                val playersPaid = participants.sumOf { it.paidByPlayer }
                if (totalMatchFee != playersPaid + ownerMatchFee) {
                    application.log.warn("Totals mismatch: totalMatchFee=$totalMatchFee, playersPaid=$playersPaid, owner=$ownerMatchFee")
                }

                val now = Instant.now()
                val match = Match(
                    tournamentId = req.tournamentId,
                    date = req.date,
                    overs = req.overs,
                    venue = req.venue,
                    teamA = req.teamA,
                    teamB = req.teamB,
                    ballType = req.ballType,
                    feePerPlayer = req.feePerPlayer,
                    ownerMatchFee = ownerMatchFee,
                    totalMatchFee = totalMatchFee,
                    participants = participants,
                    createdAt = now,
                    updatedAt = now
                )

                MongoDataSource.matchCollection.insertOne(match)

                // Update player rollups
                participants.forEach { p ->
                    MongoDataSource.playerCollection.updateOne(
                        Player::id eq p.playerId,
                        combine(
                            set(Player::updatedAt setTo Instant.now()),
                            inc(Player::totals / PlayerTotals::matchAmount, req.feePerPlayer),
                            inc(Player::totals / PlayerTotals::ownerCovered, p.coveredByOwner),
                            inc(Player::totals / PlayerTotals::matches, 1)
                        )
                    )
                }

                // (optional) tournament rollups (matchCount++)
                MongoDataSource.tournamentCollection.updateOne(
                    com.arx.models.Tournament::id eq req.tournamentId,
                    inc(com.arx.models.Tournament::matchCount, 1)
                )

                call.respond(HttpStatusCode.Created, match)
            } catch (e: Exception) {
                application.log.error("Failed to create match", e)
                call.respond(HttpStatusCode.InternalServerError, "Failed to create match.")
            }
        }
    }
}
