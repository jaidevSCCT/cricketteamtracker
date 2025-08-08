package com.arx.models

import java.util.UUID
import org.bson.codecs.pojo.annotations.BsonId
import kotlinx.serialization.Serializable

@Serializable
data class Tournament(
    @BsonId
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val matches: List<String> // match IDs
)
