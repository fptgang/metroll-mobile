package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderDetailDto(
    val id: String,
    val ticketOrderId: String,
    val ticketType: String,
    val p2pJourney: String? = null,
    val timedTicketPlan: String? = null,
    val quantity: Int,
    val unitPrice: Double,
    val baseTotal: Double,
    val discountTotal: Double,
    val finalTotal: Double,
    val createdAt: String
) 