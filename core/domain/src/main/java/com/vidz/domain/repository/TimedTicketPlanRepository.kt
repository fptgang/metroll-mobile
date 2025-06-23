package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.TimedTicketPlan
import com.vidz.domain.model.TimedTicketPlanCreateRequest
import com.vidz.domain.model.TimedTicketPlanUpdateRequest

interface TimedTicketPlanRepository {
    suspend fun getTimedTicketPlans(
        page: Int? = null,
        size: Int? = null,
        search: String? = null
    ): Result<PageDto<TimedTicketPlan>>
    
    suspend fun getTimedTicketPlanById(id: String): Result<TimedTicketPlan>
    
    suspend fun createTimedTicketPlan(request: TimedTicketPlanCreateRequest): Result<TimedTicketPlan>
    
    suspend fun updateTimedTicketPlan(id: String, request: TimedTicketPlanUpdateRequest): Result<TimedTicketPlan>
    
    suspend fun deleteTimedTicketPlan(id: String): Result<Unit>
} 