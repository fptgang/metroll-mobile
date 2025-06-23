package com.vidz.domain.usecase.ticket

import com.vidz.domain.Result
import com.vidz.domain.model.Ticket
import com.vidz.domain.model.TicketUpsertRequest
import com.vidz.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateTicketsUseCase @Inject constructor(
    private val repository: TicketRepository
) {
    suspend operator fun invoke(requests: List<TicketUpsertRequest>): Flow<Result<List<Ticket>>> = flow {
        try {
            emit(Result.Init)
            
            if (requests.isEmpty()) {
                emit(Result.ServerError.MissingParam("At least one ticket request is required"))
                return@flow
            }
            
            // Validate all requests
            requests.forEachIndexed { index, request ->
                when {
                    request.ticketNumber.isBlank() -> {
                        emit(Result.ServerError.MissingParam("Ticket number is required for ticket at index $index"))
                        return@flow
                    }
                    request.ticketOrderDetailId.isBlank() -> {
                        emit(Result.ServerError.MissingParam("Ticket order detail ID is required for ticket at index $index"))
                        return@flow
                    }
                    request.validUntil.isBlank() -> {
                        emit(Result.ServerError.MissingParam("Valid until date is required for ticket at index $index"))
                        return@flow
                    }
                }
            }
            
            val result = repository.createTickets(requests)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to create tickets"))
        }
    }
} 