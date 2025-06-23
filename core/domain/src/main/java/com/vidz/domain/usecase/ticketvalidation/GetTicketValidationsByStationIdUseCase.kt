package com.vidz.domain.usecase.ticketvalidation

import com.vidz.domain.Result
import com.vidz.domain.model.TicketValidation
import com.vidz.domain.repository.TicketValidationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTicketValidationsByStationIdUseCase @Inject constructor(
    private val repository: TicketValidationRepository
) {
    suspend operator fun invoke(stationId: String): Flow<Result<List<TicketValidation>>> = flow {
        try {
            emit(Result.Init)
            
            if (stationId.isBlank()) {
                emit(Result.ServerError.MissingParam("Station ID is required"))
                return@flow
            }
            
            val result = repository.getTicketValidationsByStationId(stationId)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get ticket validations by station ID"))
        }
    }
} 