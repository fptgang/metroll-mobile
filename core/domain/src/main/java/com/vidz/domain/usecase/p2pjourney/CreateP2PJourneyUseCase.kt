package com.vidz.domain.usecase.p2pjourney

import com.vidz.domain.Result
import com.vidz.domain.model.P2PJourney
import com.vidz.domain.model.P2PJourneyCreateRequest
import com.vidz.domain.repository.P2PJourneyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class CreateP2PJourneyUseCase @Inject constructor(
    private val repository: P2PJourneyRepository
) {
    suspend operator fun invoke(request: P2PJourneyCreateRequest): Flow<Result<P2PJourney>> = flow {
        try {
            emit(Result.Init)
            
            // Validate request
            when {
                request.startStationId.isBlank() -> {
                    emit(Result.ServerError.MissingParam("Start station ID is required"))
                    return@flow
                }
                request.endStationId.isBlank() -> {
                    emit(Result.ServerError.MissingParam("End station ID is required"))
                    return@flow
                }
                request.startStationId == request.endStationId -> {
                    emit(Result.ServerError.MissingParam("Start and end stations cannot be the same"))
                    return@flow
                }
                request.basePrice < 0 -> {
                    emit(Result.ServerError.MissingParam("Base price cannot be negative"))
                    return@flow
                }
                request.distance < 0 -> {
                    emit(Result.ServerError.MissingParam("Distance cannot be negative"))
                    return@flow
                }
                request.travelTime <= 0 -> {
                    emit(Result.ServerError.MissingParam("Travel time must be greater than 0"))
                    return@flow
                }
            }
            
            val result = repository.createP2PJourney(request)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to create P2P journey"))
        }
    }
} 