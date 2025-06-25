package com.vidz.domain.usecase.station

import com.vidz.domain.Result
import com.vidz.domain.model.Station
import com.vidz.domain.repository.StationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStationByCodeUseCase @Inject constructor(
    private val repository: StationRepository
) {
    suspend operator fun invoke(code: String): Flow<Result<Station>> {
        return repository.getStationByCode(code)
    }
} 