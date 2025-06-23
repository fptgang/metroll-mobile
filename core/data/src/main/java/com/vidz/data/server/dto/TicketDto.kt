package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class TicketDto(
    val id: String = "",
    val ticketType: String = "", // P2P or TIMED
    val ticketNumber: String = "",
    val ticketOrderDetailId: String = "",
    val purchaseDate: String = "",
    val validUntil: String = "",
    val status: String = "", // VALID, USED, EXPIRED, CANCELLED
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class TicketUpsertRequestDto(
    val ticketType: String,
    val ticketNumber: String,
    val ticketOrderDetailId: String,
    val validUntil: String,
    val status: String
) 