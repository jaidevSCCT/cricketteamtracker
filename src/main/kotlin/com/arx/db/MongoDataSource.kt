package com.arx.db

import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import com.arx.models.Match
import com.arx.models.Tournament
import com.arx.models.Player


object MongoDataSource {
    private val client = KMongo.createClient()
    private val database = client.getDatabase("cricket_db").coroutine
    val matchCollection = database.getCollection<Match>("matches")
    val tournamentCollection = database.getCollection<Tournament>("tournaments")
    val playerCollection = database.getCollection<Player>("players")
}
