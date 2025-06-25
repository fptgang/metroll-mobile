package com.vidz.domain.usecase.metro

import com.vidz.domain.Result
import com.vidz.domain.model.MetroLine
import com.vidz.domain.repository.MetroLineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMetroLineByCodeUseCase @Inject constructor(
    private val repository: MetroLineRepository
) {
    suspend operator fun invoke(code: String): Flow<Result<MetroLine>> {
        return repository.getMetroLineByCode(code)
    }
} 