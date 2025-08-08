package com.arx.models

import kotlinx.serialization.Serializable
import java.util.UUID
import org.bson.codecs.pojo.annotations.BsonId

@Serializable
data class Player(
    @BsonId
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val paidAmount: Double,
    val paidByOwner: Double = 0.0 // ✅ money paid by you to player
)

