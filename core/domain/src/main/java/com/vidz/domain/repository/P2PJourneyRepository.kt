package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.P2PJourney
import com.vidz.domain.model.P2PJourneyCreateRequest
import com.vidz.domain.model.P2PJourneyUpdateRequest
import com.vidz.domain.model.PageDto

interface P2PJourneyRepository {
    suspend fun getP2PJourneys(
        page: Int? = null,
        size: Int? = null,
        search: String? = null
    ): Result<PageDto<P2PJourney>>
    
    suspend fun getP2PJourneyById(id: String): Result<P2PJourney>
    
    suspend fun getP2PJourneyByStations(
        page: Int? = null,
        size: Int? = null,
        startStationId: String,
        endStationId: String
    ): Result<PageDto<P2PJourney>>
    
    suspend fun createP2PJourney(request: P2PJourneyCreateRequest): Result<P2PJourney>
    
    suspend fun updateP2PJourney(id: String, request: P2PJourneyUpdateRequest): Result<P2PJourney>
    
    suspend fun deleteP2PJourney(id: String): Result<Unit>
} 