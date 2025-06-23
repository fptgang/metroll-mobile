package com.vidz.domain.usecase.ticketvalidation

import com.vidz.domain.Result
import com.vidz.domain.model.TicketValidation
import com.vidz.domain.repository.TicketValidationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTicketValidationByIdUseCase @Inject constructor(
    private val repository: TicketValidationRepository
) {
    suspend operator fun invoke(id: String): Flow<Result<TicketValidation>> = flow {
        try {
            emit(Result.Init)
            
            if (id.isBlank()) {
                emit(Result.ServerError.MissingParam("Ticket validation ID is required"))
                return@flow
            }
            
            val result = repository.getTicketValidationById(id)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get ticket validation"))
        }
    }
} 