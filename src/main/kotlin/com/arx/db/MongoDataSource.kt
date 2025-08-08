package com.arx.db

import com.arx.config.ConfigLoader
import com.arx.models.Match
import com.arx.models.Tournament
import com.arx.models.Player
import org.litote.kmongo.coroutine.coroutine
import org.litote.kmongo.reactivestreams.KMongo
import org.litote.kmongo.coroutine.CoroutineDatabase

object MongoDataSource {
    private val mongoConfig = ConfigLoader.loadMongoConfig()

    private val client = KMongo.createClient(mongoConfig.uri)
    private val database: CoroutineDatabase = client.getDatabase(mongoConfig.database).coroutine

    val matchCollection = database.getCollection<Match>("matches")
    val tournamentCollection = database.getCollection<Tournament>("tournaments")
    val playerCollection = database.getCollection<Player>("players")
}
