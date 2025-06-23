package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Ticket(
    val id: String = "",
    val ticketType: TicketType = TicketType.P2P,
    val ticketNumber: String = "",
    val ticketOrderDetailId: String = "",
    val purchaseDate: String = "",
    val validUntil: String = "",
    val status: TicketStatus = TicketStatus.VALID,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class TicketUpsertRequest(
    val ticketType: TicketType,
    val ticketNumber: String,
    val ticketOrderDetailId: String,
    val validUntil: String,
    val status: TicketStatus
) 