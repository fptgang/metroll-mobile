package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class TicketValidation(
    val id: String = "",
    val stationId: String = "",
    val ticketId: String = "",
    val validationType: ValidationType = ValidationType.ENTRY,
    val validationTime: String = "",
    val deviceId: String = "",
    val createdAt: String = ""
)

@Serializable
data class TicketValidationCreateRequest(
    val ticketId: String,
    val validationType: ValidationType
) 
