package com.vidz.domain.usecase.metro

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
        page: Int? = null,
        size: Int? = null
    ): Flow<Result<PageDto<MetroLine>>> {
        return repository.getMetroLines(name, code, status, page, size)
    }
} 