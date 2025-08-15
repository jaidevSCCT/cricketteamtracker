package com.arx.models

import kotlinx.serialization.Serializable
import com.arx.models.enums.PlayerTag

// participant snapshot per match
@Serializable
data class Participant(
    val playerId: String,
    val name: String,
    val tag: PlayerTag = PlayerTag.PLAYER,
    val paidByPlayer: Int = 0,
    val coveredByOwner: Int = 0
)
