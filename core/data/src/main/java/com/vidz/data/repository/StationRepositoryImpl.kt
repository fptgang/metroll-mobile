package com.vidz.data.repository

import com.vidz.data.flow.IFlow
import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.toDomain
import com.vidz.data.mapper.toDto
import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.Station
import com.vidz.domain.repository.StationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class StationRepositoryImpl @Inject constructor(
    private val retrofitServer: RetrofitServer
) : StationRepository {

    override suspend fun getStations(
        name: String?,
        code: String?,
        status: String?,
        lineCode: String?,
        page: Int?,
        size: Int?
    ): Flow<Result<PageDto<Station>>> = flow {
        val serverFlow: IFlow<PageDto<Station>> = ServerFlow(
            getData = { retrofitServer.stationApi.getStations(name, code, status, lineCode, page, size) },
            convert = { response: com.vidz.data.server.dto.PageDto<com.vidz.data.server.dto.StationDto> -> 
                response.toDomain { dto -> dto.toDomain() } 
            }
        )
        serverFlow.execute().collect { emit(it) }
    }

    override suspend fun getStationByCode(code: String): Flow<Result<Station>> = flow {
        val serverFlow: IFlow<Station> = ServerFlow(
            getData = { retrofitServer.stationApi.getStationByCode(code) },
            convert = { it.toDomain() }
        )
        serverFlow.execute().collect { emit(it) }
    }

    override suspend fun createStation(station: Station): Flow<Result<Station>> = flow {
        val serverFlow: IFlow<Station> = ServerFlow(
            getData = { retrofitServer.stationApi.createStation(station.toDto()) },
            convert = { it.toDomain() }
        )
        serverFlow.execute().collect { emit(it) }
    }

    override suspend fun updateStation(station: Station): Flow<Result<Station>> = flow {
        val serverFlow: IFlow<Station> = ServerFlow(
            getData = { retrofitServer.stationApi.createStation(station.toDto()) },
            convert = { it.toDomain() }
        )
        serverFlow.execute().collect { emit(it) }
    }

    override suspend fun createStationList(stations: List<Station>): Flow<Result<List<Station>>> = flow {
        val serverFlow: IFlow<List<Station>> = ServerFlow(
            getData = { retrofitServer.stationApi.createStationList(stations.map { it.toDto() }) },
            convert = { it.map { dto -> dto.toDomain() } }
        )
        serverFlow.execute().collect { emit(it) }
    }
}