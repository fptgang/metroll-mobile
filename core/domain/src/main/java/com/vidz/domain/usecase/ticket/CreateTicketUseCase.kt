package com.vidz.domain.usecase.ticket

import com.vidz.domain.Result
import com.vidz.domain.model.Ticket
import com.vidz.domain.model.TicketUpsertRequest
import com.vidz.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateTicketUseCase @Inject constructor(
    private val repository: TicketRepository
) {
    suspend operator fun invoke(request: TicketUpsertRequest): Flow<Result<Ticket>> = flow {
        try {
            emit(Result.Init)
            
            // Validate request
            when {
                request.ticketNumber.isBlank() -> {
                    emit(Result.ServerError.MissingParam("Ticket number is required"))
                    return@flow
                }
                request.ticketOrderDetailId.isBlank() -> {
                    emit(Result.ServerError.MissingParam("Ticket order detail ID is required"))
                    return@flow
                }
                request.validUntil.isBlank() -> {
                    emit(Result.ServerError.MissingParam("Valid until date is required"))
                    return@flow
                }
            }
            
            val result = repository.createTicket(request)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to create ticket"))
        }
    }
} 