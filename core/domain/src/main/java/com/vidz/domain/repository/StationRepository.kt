package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.Station
import kotlinx.coroutines.flow.Flow

interface StationRepository {
    suspend fun getStations(
        name: String? = null,
        code: String? = null,
        status: String? = null,
        lineCode: String? = null,
        page: Int? = null,
        size: Int? = null
    ): Flow<Result<PageDto<Station>>>
    
    suspend fun getStationByCode(code: String): Flow<Result<Station>>
    
    suspend fun createStation(station: Station): Flow<Result<Station>>
    
    suspend fun updateStation(station: Station): Flow<Result<Station>>
    
    suspend fun createStationList(stations: List<Station>): Flow<Result<List<Station>>>
} 