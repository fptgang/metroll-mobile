package com.vidz.domain.model

import java.time.LocalDateTime

data class OrderDetail(
    val id: String,
    val orderId: String,
    val ticketId: String="",
    val ticketType: TicketType,
    val p2pJourney: String? = null,
    val timedTicketPlan: String? = null,
    val quantity: Int,
    val unitPrice: Double,
    val baseTotal: Double,
    val discountTotal: Double,
    val finalTotal: Double,
    val createdAt: LocalDateTime
) 