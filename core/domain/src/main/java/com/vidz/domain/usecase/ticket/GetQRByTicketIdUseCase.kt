package com.vidz.domain.usecase.ticket

import com.vidz.domain.Result
import com.vidz.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetQRByTicketIdUseCase @Inject constructor(
    private val ticketRepository: TicketRepository
) {
    suspend operator fun invoke(ticketId: String): Flow<Result<String>> = flow {
        try {
            emit(Result.Init)
            
            if (ticketId.isBlank()) {
                emit(Result.ServerError.MissingParam("Ticket ID is required"))
                return@flow
            }
            
            ticketRepository.getTicketQRCode(ticketId).collect { result ->
                emit(result)
            }
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get QR code"))
        }
    }
} 