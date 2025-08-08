package com.arx.models

import java.util.UUID
import org.bson.codecs.pojo.annotations.BsonId
import kotlinx.serialization.Serializable

@Serializable
data class Match(
    @BsonId
    val id: String = UUID.randomUUID().toString(),
    val date: String,
    val overs: Int = 20,
    val venue: String,
    val teamA: String = "ARX11",
    val teamB: String = "ARX12",
    val ballType: String = "LEATHER",
    val players: List<String>,
    val unpaidPlayers: List<String> = emptyList(), // ✅
    val ownerMatchFee: Double = 0.0, // ✅
    val totalMatchFee: Int = 0
)

