package com.vidz.domain.usecase.p2pjourney

import com.vidz.domain.Result
import com.vidz.domain.model.P2PJourney
import com.vidz.domain.model.PageDto
import com.vidz.domain.repository.P2PJourneyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetP2PJourneysUseCase @Inject constructor(
    private val repository: P2PJourneyRepository
) {
    suspend operator fun invoke(
        page: Int? = null,
        size: Int? = null,
        search: String? = null
    ): Flow<Result<PageDto<P2PJourney>>> = flow {
        try {
            emit(Result.Init)
            val result = repository.getP2PJourneys(page, size, search)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get P2P journeys"))
        }
    }
} 