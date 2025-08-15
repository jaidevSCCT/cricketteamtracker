package com.arx.models

import kotlinx.serialization.Serializable
import java.util.UUID
import org.bson.codecs.pojo.annotations.BsonId
import com.arx.models.enums.PlayerTag
import com.arx.models.utils.PlayerTotals
import java.time.Instant
import com.arx.serializers.InstantSerializer

@Serializable
data class Player(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val name: String,
    val normalizedName: String,
    val tag: PlayerTag = PlayerTag.PLAYER,
    val totals: PlayerTotals = PlayerTotals(),
    @Serializable(with = InstantSerializer::class)
    val updatedAt: Instant = Instant.now()
)