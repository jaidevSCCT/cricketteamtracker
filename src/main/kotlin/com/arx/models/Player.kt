package com.arx.models

import java.util.UUID

data class Player(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val paidAmount: Double
)
