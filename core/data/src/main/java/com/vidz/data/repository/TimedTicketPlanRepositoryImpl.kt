package com.vidz.data.repository

import com.vidz.data.flow.IFlow
import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.toDomain
import com.vidz.data.mapper.toDto
import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.TimedTicketPlan
import com.vidz.domain.model.TimedTicketPlanCreateRequest
import com.vidz.domain.model.TimedTicketPlanUpdateRequest
import com.vidz.domain.repository.TimedTicketPlanRepository
import javax.inject.Inject

class TimedTicketPlanRepositoryImpl @Inject constructor(
    private val retrofitServer: RetrofitServer
) : TimedTicketPlanRepository {

    override suspend fun getTimedTicketPlans(
        page: Int?,
        size: Int?,
        search: String?
    ): Result<PageDto<TimedTicketPlan>> {
        val flow: IFlow<PageDto<TimedTicketPlan>> = ServerFlow(
            getData = { retrofitServer.timedTicketPlanApi.getTimedTicketPlans(page, size, search) },
            convert = { response: com.vidz.data.server.dto.PageDto<com.vidz.data.server.dto.TimedTicketPlanDto> -> 
                response.toDomain { dto -> dto.toDomain() } 
            }
        )
        return flow.execute().let { flowResult ->
            var result: Result<PageDto<TimedTicketPlan>> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun getTimedTicketPlanById(id: String): Result<TimedTicketPlan> {
        val flow: IFlow<TimedTicketPlan> = ServerFlow(
            getData = { retrofitServer.timedTicketPlanApi.getTimedTicketPlanById(id) },
            convert = { it.toDomain() }
        )
        return flow.execute().let { flowResult ->
            var result: Result<TimedTicketPlan> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun createTimedTicketPlan(request: TimedTicketPlanCreateRequest): Result<TimedTicketPlan> {
        val flow: IFlow<TimedTicketPlan> = ServerFlow(
            getData = { retrofitServer.timedTicketPlanApi.createTimedTicketPlan(request.toDto()) },
            convert = { it.toDomain() }
        )
        return flow.execute().let { flowResult ->
            var result: Result<TimedTicketPlan> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun updateTimedTicketPlan(
        id: String,
        request: TimedTicketPlanUpdateRequest
    ): Result<TimedTicketPlan> {
        val flow: IFlow<TimedTicketPlan> = ServerFlow(
            getData = { retrofitServer.timedTicketPlanApi.updateTimedTicketPlan(id, request.toDto()) },
            convert = { it.toDomain() }
        )
        return flow.execute().let { flowResult ->
            var result: Result<TimedTicketPlan> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun deleteTimedTicketPlan(id: String): Result<Unit> {
        val flow: IFlow<Unit> = ServerFlow(
            getData = { retrofitServer.timedTicketPlanApi.deleteTimedTicketPlan(id) },
            convert = { Unit }
        )
        return flow.execute().let { flowResult ->
            var result: Result<Unit> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }
} 