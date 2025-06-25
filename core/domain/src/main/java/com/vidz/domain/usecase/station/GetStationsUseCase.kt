package com.vidz.domain.usecase.station

import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.Station
import com.vidz.domain.repository.StationRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStationsUseCase @Inject constructor(
    private val repository: StationRepository
) {
    suspend operator fun invoke(
        name: String? = null,
        code: String? = null,
        status: String? = null,
        lineCode: String? = null,
        page: Int? = null,
        size: Int? = null
    ): Flow<Result<PageDto<Station>>> {
        return repository.getStations(name, code, status, lineCode, page, size)
    }
} 