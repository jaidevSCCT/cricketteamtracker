package com.arx.routes

import com.arx.db.MongoDataSource
import io.ktor.http.HttpStatusCode
import io.ktor.server.application.log
import io.ktor.server.response.respond
import io.ktor.server.routing.Route
import io.ktor.server.routing.get
import io.ktor.server.routing.route
import io.ktor.server.routing.application
import org.bson.Document

fun Route.financeOwnerTotalRoute() {
    route("/finance") {
        get("/owner-total") {
            try {
                // Sum coveredByOwner across all participants of all matches (null-safe)
                val matchAggList = MongoDataSource.matchCollection.aggregate<Document>(
                    listOf(
                        Document("\$unwind", Document("path", "\$participants").append("preserveNullAndEmptyArrays", true)),
                        Document(
                            "\$group",
                            Document("_id", null).append(
                                "total",
                                Document("\$sum", Document("\$ifNull", listOf("\$participants.coveredByOwner", 0)))
                            )
                        )
                    )
                ).toList()
                val matchOwnerLoss = (matchAggList.firstOrNull()?.get("total") as? Number)?.toInt() ?: 0

                // Sum paidAmount from owner_ledger (null-safe)
                val ledgerAggList = MongoDataSource.ownerLedgerCollection.aggregate<Document>(
                    listOf(
                        Document(
                            "\$group",
                            Document("_id", null).append(
                                "total",
                                Document("\$sum", Document("\$ifNull", listOf("\$paidAmount", 0)))
                            )
                        )
                    )
                ).toList()
                val ownerLedgerTotal = (ledgerAggList.firstOrNull()?.get("total") as? Number)?.toInt() ?: 0

                call.respond(
                    mapOf(
                        "matchOwnerLoss" to matchOwnerLoss,
                        "ownerLedgerTotal" to ownerLedgerTotal,
                        "grandTotalOutflow" to (matchOwnerLoss + ownerLedgerTotal)
                    )
                )
            } catch (e: Exception) {
                application.log.error("owner-total failed", e)
                call.respond(
                    HttpStatusCode.InternalServerError,
                    mapOf("message" to "Error computing owner totals", "error" to (e.message ?: "unknown"))
                )
            }
        }
    }
}
