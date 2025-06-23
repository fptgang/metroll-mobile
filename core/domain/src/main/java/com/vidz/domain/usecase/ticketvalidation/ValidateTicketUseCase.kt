package com.vidz.domain.usecase.ticketvalidation

import com.vidz.domain.Result
import com.vidz.domain.model.TicketValidation
import com.vidz.domain.model.TicketValidationCreateRequest
import com.vidz.domain.repository.TicketValidationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class ValidateTicketUseCase @Inject constructor(
    private val repository: TicketValidationRepository
) {
    suspend operator fun invoke(request: TicketValidationCreateRequest): Flow<Result<TicketValidation>> = flow {
        try {
            emit(Result.Init)
            
            // Validate request
            when {
                request.stationId.isBlank() -> {
                    emit(Result.ServerError.MissingParam("Station ID is required"))
                    return@flow
                }
                request.ticketId.isBlank() -> {
                    emit(Result.ServerError.MissingParam("Ticket ID is required"))
                    return@flow
                }
                request.deviceId.isBlank() -> {
                    emit(Result.ServerError.MissingParam("Device ID is required"))
                    return@flow
                }
            }
            
            val result = repository.validateTicket(request)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to validate ticket"))
        }
    }
} 