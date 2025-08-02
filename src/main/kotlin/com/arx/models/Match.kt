package com.arx.models

import java.util.UUID

data class Match(
    val id: String = UUID.randomUUID().toString(),
    val date: String,
    val overs: Int = 20,                     // Match format (T20, etc.)
    val venue: String,                       // Ground name
    val teamA: String = "ARX11",             // Optional field
    val teamB: String = "ARX12",                       // Optional field
    val ballType: String = "LEATHER",         // Optional field
    val players: List<String>                // Player IDs from both teams
)
