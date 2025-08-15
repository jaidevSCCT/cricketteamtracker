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

    private val effectiveUri = System.getenv("MONGO_URI") ?: mongoConfig.uri
    
    init {
        println("MongoDB Connection URI: $effectiveUri")
    }
    
    private val client = KMongo.createClient(effectiveUri)
    private val database: CoroutineDatabase = client.getDatabase(mongoConfig.database).coroutine

    val matchCollection = database.getCollection<Match>("matches")
    val tournamentCollection = database.getCollection<Tournament>("tournaments")
    val playerCollection = database.getCollection<Player>("players")
}
