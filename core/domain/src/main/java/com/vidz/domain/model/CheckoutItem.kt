package com.vidz.domain.model

data class CheckoutItem(
    val ticketType: TicketType,
    val p2pJourneyId: String? = null,
    val timedTicketPlanId: String? = null,
    val quantity: Int
) 