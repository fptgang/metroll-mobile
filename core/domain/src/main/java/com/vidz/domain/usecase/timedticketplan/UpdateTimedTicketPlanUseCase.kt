package com.vidz.domain.usecase.timedticketplan

import com.vidz.domain.Result
import com.vidz.domain.model.TimedTicketPlan
import com.vidz.domain.model.TimedTicketPlanUpdateRequest
import com.vidz.domain.repository.TimedTicketPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateTimedTicketPlanUseCase @Inject constructor(
    private val repository: TimedTicketPlanRepository
) {
    suspend operator fun invoke(id: String, request: TimedTicketPlanUpdateRequest): Flow<Result<TimedTicketPlan>> = flow {
        try {
            emit(Result.Init)
            
            if (id.isBlank()) {
                emit(Result.ServerError.MissingParam("Timed ticket plan ID is required"))
                return@flow
            }
            
            // Validate request
            request.validDuration?.let { duration ->
                if (duration <= 0) {
                    emit(Result.ServerError.MissingParam("Valid duration must be greater than 0"))
                    return@flow
                }
            }
            
            request.basePrice?.let { price ->
                if (price < 0) {
                    emit(Result.ServerError.MissingParam("Base price cannot be negative"))
                    return@flow
                }
            }
            
            val result = repository.updateTimedTicketPlan(id, request)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to update timed ticket plan"))
        }
    }
} 