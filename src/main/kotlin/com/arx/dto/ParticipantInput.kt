package com.arx.dto

import kotlinx.serialization.Serializable
import com.arx.models.enums.PlayerTag

@Serializable
data class ParticipantInput(
    val name: String,
    val tag: PlayerTag = PlayerTag.PLAYER,
    val paidByPlayer: Int = 0,
    val coveredByOwner: Int = 0
)