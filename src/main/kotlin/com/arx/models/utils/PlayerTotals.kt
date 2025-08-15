package com.arx.models.utils
import kotlinx.serialization.Serializable

@Serializable
data class PlayerTotals(
    val matchAmount: Int = 0,
    val ownerCovered: Int = 0,
    val matches: Int = 0
)
