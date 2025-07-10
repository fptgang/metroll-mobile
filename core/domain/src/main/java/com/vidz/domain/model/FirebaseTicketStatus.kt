package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class FirebaseTicketStatus {
    VALID,      // Ticket is valid and can be used
    IN_USED,    // Ticket is currently being used (for timed tickets during journey) - Firebase only
    USED,       // Ticket has been used (for P2P tickets)
    EXPIRED,    // Ticket has expired
    CANCELLED   // Ticket has been cancelled
} 