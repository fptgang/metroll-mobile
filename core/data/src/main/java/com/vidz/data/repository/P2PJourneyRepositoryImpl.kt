package com.vidz.data.repository

import com.vidz.data.flow.IFlow
import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.toDomain
import com.vidz.data.mapper.toDto
import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.domain.Result
import com.vidz.domain.model.P2PJourney
import com.vidz.domain.model.P2PJourneyCreateRequest
import com.vidz.domain.model.P2PJourneyUpdateRequest
import com.vidz.domain.model.PageDto
import com.vidz.domain.repository.P2PJourneyRepository
import javax.inject.Inject

class P2PJourneyRepositoryImpl @Inject constructor(
    private val retrofitServer: RetrofitServer
) : P2PJourneyRepository {

    override suspend fun getP2PJourneys(
        page: Int?,
        size: Int?,
        search: String?
    ): Result<PageDto<P2PJourney>> {
        val flow: IFlow<PageDto<P2PJourney>> = ServerFlow(
            getData = { retrofitServer.p2pJourneyApi.getP2PJourneys(page, size, search) },
            convert = { response: com.vidz.data.server.dto.PageDto<com.vidz.data.server.dto.P2PJourneyDto> -> 
                response.toDomain { dto -> dto.toDomain() } 
            }
        )
        return flow.execute().let { flowResult ->
            var result: Result<PageDto<P2PJourney>> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun getP2PJourneyById(id: String): Result<P2PJourney> {
        val flow: IFlow<P2PJourney> = ServerFlow(
            getData = { retrofitServer.p2pJourneyApi.getP2PJourneyById(id) },
            convert = { it.toDomain() }
        )
        return flow.execute().let { flowResult ->
            var result: Result<P2PJourney> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun getP2PJourneyByStations(
        page: Int?,
        size: Int?,
        startStationId: String,
        endStationId: String
    ): Result<PageDto<P2PJourney>> {
        val flow: IFlow<PageDto<P2PJourney>> = ServerFlow(
            getData = { retrofitServer.p2pJourneyApi.getP2PJourneyByStations(page, size,startStationId, endStationId) },
            convert = { response: com.vidz.data.server.dto.PageDto<com.vidz.data.server.dto.P2PJourneyDto> ->
                response.toDomain { dto -> dto.toDomain() }
            }
        )
        return flow.execute().let { flowResult ->
            var result: Result<PageDto<P2PJourney>> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun createP2PJourney(request: P2PJourneyCreateRequest): Result<P2PJourney> {
        val flow: IFlow<P2PJourney> = ServerFlow(
            getData = { retrofitServer.p2pJourneyApi.createP2PJourney(request.toDto()) },
            convert = { it.toDomain() }
        )
        return flow.execute().let { flowResult ->
            var result: Result<P2PJourney> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun updateP2PJourney(
        id: String,
        request: P2PJourneyUpdateRequest
    ): Result<P2PJourney> {
        val flow: IFlow<P2PJourney> = ServerFlow(
            getData = { retrofitServer.p2pJourneyApi.updateP2PJourney(id, request.toDto()) },
            convert = { it.toDomain() }
        )
        return flow.execute().let { flowResult ->
            var result: Result<P2PJourney> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun deleteP2PJourney(id: String): Result<Unit> {
        val flow: IFlow<Unit> = ServerFlow(
            getData = { retrofitServer.p2pJourneyApi.deleteP2PJourney(id) },
            convert = { Unit }
        )
        return flow.execute().let { flowResult ->
            var result: Result<Unit> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }
} 