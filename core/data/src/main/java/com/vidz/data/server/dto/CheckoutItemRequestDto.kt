package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class CheckoutItemRequestDto(
    val ticketType: String,
    val p2pJourneyId: String? = null,
    val timedTicketPlanId: String? = null,
    val quantity: Int
) 