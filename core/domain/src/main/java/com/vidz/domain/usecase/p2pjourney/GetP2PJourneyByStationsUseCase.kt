package com.vidz.domain.usecase.p2pjourney

import com.vidz.domain.Result
import com.vidz.domain.model.P2PJourney
import com.vidz.domain.model.PageDto
import com.vidz.domain.repository.P2PJourneyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetP2PJourneyByStationsUseCase @Inject constructor(
    private val repository: P2PJourneyRepository
) {
    suspend operator fun invoke(
        page: Int,
        size: Int,
        startStationId: String? = null,
        endStationId: String? = null
    ): Flow<Result<PageDto<P2PJourney>>> = flow {
        try {
            emit(Result.Init)

            when {
//                startStationId.isNullOrBlank() -> {
//                    emit(Result.ServerError.MissingParam("Start station ID is required"))
//                    return@flow
//                }
//                endStationId.isBlank() -> {
//                    emit(Result.ServerError.MissingParam("End station ID is required"))
//                    return@flow
//                }
                startStationId == endStationId -> {
                    emit(Result.ServerError.MissingParam("Start and end stations cannot be the same"))
                    return@flow
                }
            }
            
            val result = repository.getP2PJourneyByStations(page,size,startStationId, endStationId)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get P2P journey by stations"))
        }
    }
} 