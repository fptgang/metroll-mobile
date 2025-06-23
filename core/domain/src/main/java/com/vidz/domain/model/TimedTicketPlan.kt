package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TimedTicketPlan(
    val id: String = "",
    val name: String = "",
    val validDuration: Int = 0, // Duration in minutes
    val basePrice: Double = 0.0,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class TimedTicketPlanCreateRequest(
    val name: String,
    val validDuration: Int,
    val basePrice: Double
)

@Serializable
data class TimedTicketPlanUpdateRequest(
    val name: String? = null,
    val validDuration: Int? = null,
    val basePrice: Double? = null
) 
