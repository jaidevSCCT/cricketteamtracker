package com.arx.models

import java.util.UUID
import org.bson.codecs.pojo.annotations.BsonId
import kotlinx.serialization.Serializable
import java.time.Instant
import com.arx.serializers.InstantSerializer

@Serializable
data class Match(
    @BsonId
    val id: String = UUID.randomUUID().toString(),
    val tournamentId: String,
    val date: String,
    val overs: Int = 20,
    val venue: String,
    val teamA: String = "ARX11",
    val teamB: String = "ARX12",
    val ballType: String = "Leather",

    val feePerPlayer: Int = 0,
    val ownerMatchFee: Int = 0,
    val totalMatchFee: Int = 0,

    val participants: List<Participant> = emptyList(),
    @Serializable(with = InstantSerializer::class)
    val createdAt: Instant = Instant.now(),
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant = Instant.now()
)

