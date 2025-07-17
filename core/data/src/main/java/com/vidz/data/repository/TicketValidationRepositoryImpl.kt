package com.vidz.data.repository

import com.vidz.data.flow.IFlow
import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.toDomain
import com.vidz.data.mapper.toDto
import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.TicketValidation
import com.vidz.domain.model.TicketValidationCreateRequest
import com.vidz.domain.repository.TicketValidationRepository
import javax.inject.Inject

class TicketValidationRepositoryImpl @Inject constructor(
    private val retrofitServer: RetrofitServer
) : TicketValidationRepository {

    override suspend fun getTicketValidations(
        page: Int?,
        size: Int?,
        search: String?
    ): Result<PageDto<TicketValidation>> {
        val flow: IFlow<PageDto<TicketValidation>> = ServerFlow(
            getData = { retrofitServer.ticketValidationApi.getTicketValidations(page, size, search) },
            convert = { response: com.vidz.data.server.dto.PageDto<com.vidz.data.server.dto.TicketValidationDto> -> 
                response.toDomain { dto -> dto.toDomain() } 
            }
        )
        return flow.execute().let { flowResult ->
            var result: Result<PageDto<TicketValidation>> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun getTicketValidationById(id: String): Result<TicketValidation> {
        val flow: IFlow<TicketValidation> = ServerFlow(
            getData = { retrofitServer.ticketValidationApi.getTicketValidationById(id) },
            convert = { it.toDomain() }
        )
        return flow.execute().let { flowResult ->
            var result: Result<TicketValidation> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun getTicketValidationsByTicketId(ticketId: String): Result<List<TicketValidation>> {
        val flow: IFlow<List<TicketValidation>> = ServerFlow(
            getData = { retrofitServer.ticketValidationApi.getTicketValidationsByTicketId(ticketId) },
            convert = { it.map { dto -> dto.toDomain() } }
        )
        return flow.execute().let { flowResult ->
            var result: Result<List<TicketValidation>> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun getTicketValidationsByStationCode(
        stationCode: String,
        page: Int?,
        size: Int?,
        search: String?,
        validationType: String?,
        startDate: String?,
        endDate: String?
    ): Result<PageDto<TicketValidation>> {
        return try {
            android.util.Log.d("TicketValidationRepo", "getTicketValidationsByStationCode called with stationCode: $stationCode")
            
            val flow: IFlow<PageDto<TicketValidation>> = ServerFlow(
                getData = { 
                    android.util.Log.d("TicketValidationRepo", "Making API call to getTicketValidationsByStationCode")
                    retrofitServer.ticketValidationApi.getTicketValidationsByStationCode(
                        stationCode, page, size, search, validationType, startDate, endDate
                    )
                },
                convert = { response: com.vidz.data.server.dto.PageDto<com.vidz.data.server.dto.TicketValidationDto> -> 
                    android.util.Log.d("TicketValidationRepo", "Converting response: ${response.content.size} items")
                    response.toDomain { dto -> dto.toDomain() } 
                }
            )
            
            var result: Result<PageDto<TicketValidation>> = Result.Init
            flow.execute().collect { 
                android.util.Log.d("TicketValidationRepo", "Flow result: $it")
                result = it 
            }
            android.util.Log.d("TicketValidationRepo", "Final result: $result")
            result
        } catch (e: Exception) {
            android.util.Log.e("TicketValidationRepo", "Exception in getTicketValidationsByStationCode", e)
            Result.ServerError.General("Failed to get ticket validations: ${e.message}")
        }
    }

    override suspend fun validateTicket(request: TicketValidationCreateRequest): Result<TicketValidation> {
        val flow: IFlow<TicketValidation> = ServerFlow(
            getData = { retrofitServer.ticketValidationApi.validateTicket(request.toDto()) },
            convert = { it.toDomain() }
        )
        return flow.execute().let { flowResult ->
            var result: Result<TicketValidation> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }
}