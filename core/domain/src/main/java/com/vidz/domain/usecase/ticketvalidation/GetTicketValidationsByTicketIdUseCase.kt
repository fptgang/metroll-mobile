package com.vidz.domain.usecase.ticketvalidation

import com.vidz.domain.Result
import com.vidz.domain.model.TicketValidation
import com.vidz.domain.repository.TicketValidationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTicketValidationsByTicketIdUseCase @Inject constructor(
    private val repository: TicketValidationRepository
) {
    suspend operator fun invoke(ticketId: String): Flow<Result<List<TicketValidation>>> = flow {
        try {
            emit(Result.Init)
            
            if (ticketId.isBlank()) {
                emit(Result.ServerError.MissingParam("Ticket ID is required"))
                return@flow
            }
            
            val result = repository.getTicketValidationsByTicketId(ticketId)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get ticket validations by ticket ID"))
        }
    }
} 