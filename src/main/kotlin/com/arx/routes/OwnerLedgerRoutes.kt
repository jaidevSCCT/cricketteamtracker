package com.arx.routes

import com.arx.db.MongoDataSource
import com.arx.models.OwnerLedgerEntry
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

fun Route.ownerLedgerRoutes() {
    route("/owner-ledger") {

        // List all owner-ledger entries
        get {
            try {
                val entries = MongoDataSource.ownerLedgerCollection.find().toList()
                call.respond(entries)
            } catch (e: Exception) {
                application.log.error("Failed to retrieve owner ledger entries", e)
                call.respond(HttpStatusCode.InternalServerError, "Unable to retrieve owner ledger entries.")
            }
        }

        // Create an owner-ledger entry
        post {
            try {
                val incoming = call.receive<OwnerLedgerEntry>()
                val res = MongoDataSource.ownerLedgerCollection.insertOne(incoming)
                if (res.wasAcknowledged()) {
                    call.respond(HttpStatusCode.Created, incoming)
                } else {
                    call.respond(HttpStatusCode.InternalServerError, "Failed to create owner ledger entry.")
                }
            } catch (e: Exception) {
                application.log.error("Failed to create owner ledger entry", e)
                call.respond(HttpStatusCode.InternalServerError, "Failed to create owner ledger entry: ${e.message ?: "Unknown error"}")
            }
        }

        // Total amount owner has given (across all teams)
        get("/total") {
            try {
                val entries = MongoDataSource.ownerLedgerCollection.find().toList()
                val total = entries.sumOf { it.paidAmount }
                call.respond(mapOf("totalOwnerGiven" to total))
            } catch (e: Exception) {
                application.log.error("Failed to compute total owner given", e)
                call.respond(HttpStatusCode.InternalServerError, "Error computing total owner given.")
            }
        }

        // Total by one team, e.g. /owner-ledger/total-by-team?teamName=Some Team
        get("/total-by-team") {
            val teamName = call.request.queryParameters["teamName"]
                ?: return@get call.respond(HttpStatusCode.BadRequest, "Missing teamName")
            try {
                val entries = MongoDataSource.ownerLedgerCollection
                    .find(OwnerLedgerEntry::teamName eq teamName)
                    .toList()
                val total = entries.sumOf { it.paidAmount }
                call.respond(mapOf("teamName" to teamName, "totalOwnerGiven" to total))
            } catch (e: Exception) {
                application.log.error("Failed to compute total by team", e)
                call.respond(HttpStatusCode.InternalServerError, "Error computing total by team.")
            }
        }

        // Rollup by team (array of {teamName, total})
        get("/by-team") {
            try {
                val entries = MongoDataSource.ownerLedgerCollection.find().toList()
                val grouped = entries.groupBy { it.teamName }
                    .map { (team, list) -> mapOf("teamName" to team, "totalOwnerGiven" to list.sumOf { it.paidAmount }) }
                    .sortedBy { it["teamName"] as String }
                call.respond(grouped)
            } catch (e: Exception) {
                application.log.error("Failed to group by team", e)
                call.respond(HttpStatusCode.InternalServerError, "Error grouping by team.")
            }
        }
    }
}
