package com.vidz.domain.usecase.ticket

import com.vidz.domain.Result
import com.vidz.domain.model.TicketDashboard
import com.vidz.domain.repository.TicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTicketDashboardUseCase @Inject constructor(
    private val repository: TicketRepository
) {
    suspend operator fun invoke(): Flow<Result<TicketDashboard>> = flow {
        try {
            emit(Result.Init)
            val result = repository.getTicketDashboard()
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get ticket dashboard"))
        }
    }
} 