package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class P2PJourneyDto(
    val id: String = "",
    val startStationId: String = "",
    val endStationId: String = "",
    val basePrice: Double = 0.0,
    val distance: Double = 0.0,
    val travelTime: Int = 0,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class P2PJourneyCreateRequestDto(
    val startStationId: String,
    val endStationId: String,
    val basePrice: Double,
    val distance: Double,
    val travelTime: Int
)

@Serializable
data class P2PJourneyUpdateRequestDto(
    val startStationId: String? = null,
    val endStationId: String? = null,
    val basePrice: Double? = null,
    val distance: Double? = null,
    val travelTime: Int? = null
)