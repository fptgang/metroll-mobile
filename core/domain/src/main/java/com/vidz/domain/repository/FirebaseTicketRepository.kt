package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.FirebaseTicket
import kotlinx.coroutines.flow.Flow

interface FirebaseTicketRepository {
    suspend fun getTicketStatus(ticketId: String): Flow<Result<FirebaseTicket?>>
    suspend fun updateTicketStatus(ticketId: String, status: com.vidz.domain.model.FirebaseTicketStatus): Flow<Result<Unit>>
} 