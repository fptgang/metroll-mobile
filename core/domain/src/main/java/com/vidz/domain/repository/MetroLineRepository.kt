package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.MetroLine
import com.vidz.domain.model.PageDto
import kotlinx.coroutines.flow.Flow

interface MetroLineRepository {
    suspend fun getMetroLines(
        name: String? = null,
        code: String? = null,
        status: String? = null,
        page: Int? = null,
        size: Int? = null
    ): Flow<Result<PageDto<MetroLine>>>
    
    suspend fun getMetroLineByCode(code: String): Flow<Result<MetroLine>>
    
    suspend fun createMetroLine(metroLine: MetroLine): Flow<Result<MetroLine>>
    
    suspend fun updateMetroLine(code: String, metroLine: MetroLine): Flow<Result<MetroLine>>
} 