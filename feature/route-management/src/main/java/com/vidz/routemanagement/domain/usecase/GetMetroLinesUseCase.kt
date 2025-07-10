package com.vidz.routemanagement.domain.usecase

import com.vidz.domain.Result
import com.vidz.domain.model.MetroLine
import com.vidz.domain.model.PageDto
import com.vidz.domain.repository.MetroLineRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMetroLinesUseCase @Inject constructor(
    private val repository: MetroLineRepository
) {
    suspend operator fun invoke(
        name: String? = null,
        code: String? = null,
        status: String? = null,
        page: Int = 0,
        size: Int = 10
    ): Flow<Result<PageDto<MetroLine>>> {
        return repository.getMetroLines(
            name = name,
            code = code,
            status = status,
            page = page,
            size = size
        )
    }
} 