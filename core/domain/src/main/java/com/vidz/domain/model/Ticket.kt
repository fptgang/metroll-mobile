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

@Serializable
data class TicketDashboard(
    val totalTickets: Long = 0,
    val ticketsByStatus: Map<String, Long> = emptyMap(),
    val ticketsByType: Map<String, Long> = emptyMap(),
    val totalValidations: Long = 0,
    val validationsByType: Map<String, Long> = emptyMap(),
    val todayValidations: Long = 0,
    val totalP2PJourneys: Long = 0,
    val validationsLast7Days: Map<String, Long> = emptyMap(),
    val lastUpdated: String = ""
) 