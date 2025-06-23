package com.vidz.domain.usecase.timedticketplan

import com.vidz.domain.Result
import com.vidz.domain.repository.TimedTicketPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class DeleteTimedTicketPlanUseCase @Inject constructor(
    private val repository: TimedTicketPlanRepository
) {
    suspend operator fun invoke(id: String): Flow<Result<Unit>> = flow {
        try {
            emit(Result.Init)
            
            if (id.isBlank()) {
                emit(Result.ServerError.MissingParam("Timed ticket plan ID is required"))
                return@flow
            }
            
            val result = repository.deleteTimedTicketPlan(id)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to delete timed ticket plan"))
        }
    }
} 