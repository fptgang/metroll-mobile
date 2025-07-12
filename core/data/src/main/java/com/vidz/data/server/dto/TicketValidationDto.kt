package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class TicketValidationDto(
    val id: String = "",
    val stationId: String = "",
    val ticketId: String = "",
    val validationType: String = "", // ENTRY or EXIT
    val validationTime: String = "",
    val validatorId: String = "",
    val createdAt: String = ""
)

@Serializable
data class TicketValidationCreateRequestDto(
    val ticketId: String,
    val validationType: String
) 
