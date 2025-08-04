package com.arx.models

import java.util.UUID

data class Match(
    val id: String = UUID.randomUUID().toString(),
    val date: String,
    val overs: Int = 20,
    val venue: String,
    val teamA: String = "ARX11",
    val teamB: String = "ARX12",
    val ballType: String = "LEATHER",
    val players: List<String>,
    val unpaidPlayers: List<String> = emptyList(), // ✅
    val ownerMatchFee: Double = 0.0 // ✅
)

