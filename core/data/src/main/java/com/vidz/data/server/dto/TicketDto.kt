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

@Serializable
data class TicketDashboardDto(
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