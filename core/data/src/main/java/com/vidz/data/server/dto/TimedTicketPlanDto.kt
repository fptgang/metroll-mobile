package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class TimedTicketPlanDto(
    val id: String = "",
    val name: String = "",
    val validDuration: Int = 0,
    val basePrice: Double = 0.0,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class TimedTicketPlanCreateRequestDto(
    val name: String,
    val validDuration: Int,
    val basePrice: Double
)

@Serializable
data class TimedTicketPlanUpdateRequestDto(
    val name: String? = null,
    val validDuration: Int? = null,
    val basePrice: Double? = null
)