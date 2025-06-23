package com.vidz.domain.usecase.ticket

import com.vidz.domain.Result
import com.vidz.domain.model.Ticket
import com.vidz.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTicketsByOrderDetailIdUseCase @Inject constructor(
    private val repository: TicketRepository
) {
    suspend operator fun invoke(orderDetailId: String): Flow<Result<List<Ticket>>> = flow {
        try {
            emit(Result.Init)
            
            if (orderDetailId.isBlank()) {
                emit(Result.ServerError.MissingParam("Order detail ID is required"))
                return@flow
            }
            
            val result = repository.getTicketsByOrderDetailId(orderDetailId)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get tickets by order detail ID"))
        }
    }
} 