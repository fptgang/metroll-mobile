package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class P2PJourney(
    val id: String = "",
    val startStationId: String = "",
    val endStationId: String = "",
    val basePrice: Double = 0.0,
    val distance: Double = 0.0, // Distance in kilometers
    val travelTime: Int = 0, // Travel time in minutes
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class P2PJourneyCreateRequest(
    val startStationId: String,
    val endStationId: String,
    val basePrice: Double,
    val distance: Double,
    val travelTime: Int
)

@Serializable
data class P2PJourneyUpdateRequest(
    val startStationId: String? = null,
    val endStationId: String? = null,
    val basePrice: Double? = null,
    val distance: Double? = null,
    val travelTime: Int? = null
) 