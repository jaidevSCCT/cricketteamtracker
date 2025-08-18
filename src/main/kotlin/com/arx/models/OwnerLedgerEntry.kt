package com.arx.models

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import java.util.UUID

@Serializable
data class OwnerLedgerEntry(
    @BsonId val id: String = UUID.randomUUID().toString(),
    val date: String,        // e.g. "2025-08-03"
    val paidAmount: Long,    // safer than Int for long-term totals
    val teamName: String,
    val ground: String
)
