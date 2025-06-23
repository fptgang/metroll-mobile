package com.vidz.domain.usecase.p2pjourney

import com.vidz.domain.Result
import com.vidz.domain.model.P2PJourney
import com.vidz.domain.repository.P2PJourneyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetP2PJourneyByIdUseCase @Inject constructor(
    private val repository: P2PJourneyRepository
) {
    suspend operator fun invoke(id: String): Flow<Result<P2PJourney>> = flow {
        try {
            emit(Result.Init)
            
            if (id.isBlank()) {
                emit(Result.ServerError.MissingParam("P2P journey ID is required"))
                return@flow
            }
            
            val result = repository.getP2PJourneyById(id)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get P2P journey"))
        }
    }
} 