package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class CheckoutItem(
    val ticketType: TicketType,
    val p2pJourneyId: String? = null,
    val timedTicketPlanId: String? = null,
    val quantity: Int
) 