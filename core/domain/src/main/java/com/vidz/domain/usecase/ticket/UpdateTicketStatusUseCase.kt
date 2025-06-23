package com.vidz.domain.usecase.ticket

import com.vidz.domain.Result
import com.vidz.domain.model.TicketStatus
import com.vidz.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateTicketStatusUseCase @Inject constructor(
    private val repository: TicketRepository
) {
    suspend operator fun invoke(id: String, status: TicketStatus): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Init)
            
            if (id.isBlank()) {
                emit(Result.ServerError.MissingParam("Ticket ID is required"))
                return@flow
            }
            
            val result = repository.updateTicketStatus(id, status)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to update ticket status"))
        }
    }
} 