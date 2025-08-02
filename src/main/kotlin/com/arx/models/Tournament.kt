package com.arx.models

import java.util.UUID

data class Tournament(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val matches: List<Match>
)
