package com.vidz.domain.usecase.timedticketplan

import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.TimedTicketPlan
import com.vidz.domain.repository.TimedTicketPlanRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTimedTicketPlansUseCase @Inject constructor(
    private val repository: TimedTicketPlanRepository
) {
    suspend operator fun invoke(
        page: Int? = null,
        size: Int? = null,
        search: String? = null
    ): Flow<Result<PageDto<TimedTicketPlan>>> = flow {
        try {
            emit(Result.Init)
            val result = repository.getTimedTicketPlans(page, size, search)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get timed ticket plans"))
        }
    }
} 