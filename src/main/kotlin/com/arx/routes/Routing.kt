package com.arx.routes

import com.arx.models.Tournament
import io.ktor.server.response.respond
import io.ktor.server.request.receive
import io.ktor.server.routing.get
import io.ktor.server.routing.Route
import io.ktor.http.HttpStatusCode
import io.ktor.server.routing.post
import io.ktor.server.routing.route

val tournaments = mutableListOf<Tournament>()

fun Route.tournamentRoutes() {

    route("/tournaments") {

        get {
            call.respond(tournaments)
        }

        post {
            val newTournament = call.receive<Tournament>()
            tournaments.add(newTournament)
            call.respond(HttpStatusCode.Created, newTournament)
        }
    }
}
