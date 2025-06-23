package com.vidz.domain.usecase.ticket

import com.vidz.domain.Result
import com.vidz.domain.model.Ticket
import com.vidz.domain.model.TicketStatus
import com.vidz.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTicketsByStatusUseCase @Inject constructor(
    private val repository: TicketRepository
) {
    suspend operator fun invoke(status: TicketStatus): Flow<Result<List<Ticket>>> = flow {
        try {
            emit(Result.Init)
            val result = repository.getTicketsByStatus(status)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get tickets by status"))
        }
    }
} 