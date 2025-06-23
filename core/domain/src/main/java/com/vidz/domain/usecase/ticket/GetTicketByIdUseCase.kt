package com.vidz.domain.usecase.ticket

import com.vidz.domain.Result
import com.vidz.domain.model.Ticket
import com.vidz.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTicketByIdUseCase @Inject constructor(
    private val repository: TicketRepository
) {
    suspend operator fun invoke(id: String): Flow<Result<Ticket>> = flow {
        try {
            emit(Result.Init)
            
            if (id.isBlank()) {
                emit(Result.ServerError.MissingParam("Ticket ID is required"))
                return@flow
            }
            
            val result = repository.getTicketById(id)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get ticket"))
        }
    }
} 