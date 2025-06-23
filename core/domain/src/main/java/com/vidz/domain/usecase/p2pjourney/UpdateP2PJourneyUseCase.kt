package com.vidz.domain.usecase.p2pjourney

import com.vidz.domain.Result
import com.vidz.domain.model.P2PJourney
import com.vidz.domain.model.P2PJourneyUpdateRequest
import com.vidz.domain.repository.P2PJourneyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class UpdateP2PJourneyUseCase @Inject constructor(
    private val repository: P2PJourneyRepository
) {
    suspend operator fun invoke(id: String, request: P2PJourneyUpdateRequest): Flow<Result<P2PJourney>> = flow {
        try {
            emit(Result.Init)
            
            if (id.isBlank()) {
                emit(Result.ServerError.MissingParam("P2P journey ID is required"))
                return@flow
            }
            
            // Validate request
            request.startStationId?.let { startId ->
                request.endStationId?.let { endId ->
                    if (startId == endId) {
                        emit(Result.ServerError.MissingParam("Start and end stations cannot be the same"))
                        return@flow
                    }
                }
            }
            
            request.basePrice?.let { price ->
                if (price < 0) {
                    emit(Result.ServerError.MissingParam("Base price cannot be negative"))
                    return@flow
                }
            }
            
            request.distance?.let { distance ->
                if (distance < 0) {
                    emit(Result.ServerError.MissingParam("Distance cannot be negative"))
                    return@flow
                }
            }
            
            request.travelTime?.let { time ->
                if (time <= 0) {
                    emit(Result.ServerError.MissingParam("Travel time must be greater than 0"))
                    return@flow
                }
            }
            
            val result = repository.updateP2PJourney(id, request)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to update P2P journey"))
        }
    }
} 