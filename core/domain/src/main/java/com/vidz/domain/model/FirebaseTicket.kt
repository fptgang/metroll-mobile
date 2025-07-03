package com.vidz.domain.model

import kotlinx.serialization.Serializable
import java.time.Instant

@Serializable
data class FirebaseTicket(
    val ticketId: String = "",
    val ticketType: TicketType = TicketType.P2P,
    val status: FirebaseTicketStatus = FirebaseTicketStatus.VALID,
    val validUntil: String = "", // Using String to match Firebase format
    
    // P2P ticket journey information
    val startStationId: String? = null,
    val endStationId: String? = null
) 