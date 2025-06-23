package com.vidz.domain.usecase.ticket

import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.Ticket
import com.vidz.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTicketsUseCase @Inject constructor(
    private val repository: TicketRepository
) {
    suspend operator fun invoke(
        page: Int? = null,
        size: Int? = null,
        search: String? = null
    ): Flow<Result<PageDto<Ticket>>> = flow {
        try {
            emit(Result.Init)
            val result = repository.getTickets(page, size, search)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get tickets"))
        }
    }
} 