package com.vidz.domain.usecase.timedticketplan

import com.vidz.domain.Result
import com.vidz.domain.model.TimedTicketPlan
import com.vidz.domain.model.TimedTicketPlanCreateRequest
import com.vidz.domain.repository.TimedTicketPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateTimedTicketPlanUseCase @Inject constructor(
    private val repository: TimedTicketPlanRepository
) {
    suspend operator fun invoke(request: TimedTicketPlanCreateRequest): Flow<Result<TimedTicketPlan>> = flow {
        try {
            emit(Result.Init)
            
            // Validate request
            when {
                request.name.isBlank() -> {
                    emit(Result.ServerError.MissingParam("Name is required"))
                    return@flow
                }
                request.validDuration <= 0 -> {
                    emit(Result.ServerError.MissingParam("Valid duration must be greater than 0"))
                    return@flow
                }
                request.basePrice < 0 -> {
                    emit(Result.ServerError.MissingParam("Base price cannot be negative"))
                    return@flow
                }
            }
            
            val result = repository.createTimedTicketPlan(request)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to create timed ticket plan"))
        }
    }
} 