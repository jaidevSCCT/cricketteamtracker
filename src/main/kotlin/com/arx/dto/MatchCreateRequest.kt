package com.arx.dto

import kotlinx.serialization.Serializable

@Serializable
data class MatchCreateRequest(
    val tournamentId: String,
    val date: String,
    val overs: Int = 20,
    val venue: String,
    val teamA: String = "ARX11",
    val teamB: String = "ARX12",
    val ballType: String = "Leather",
    val feePerPlayer: Int = 0,
    val participants: List<ParticipantInput> = emptyList()
)