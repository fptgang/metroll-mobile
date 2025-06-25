package com.vidz.data.repository

import com.vidz.data.flow.IFlow
import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.toDomain
import com.vidz.data.mapper.toRequestDto
import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.domain.Result
import com.vidz.domain.model.MetroLine
import com.vidz.domain.model.PageDto
import com.vidz.domain.repository.MetroLineRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MetroLineRepositoryImpl @Inject constructor(
    private val retrofitServer: RetrofitServer
) : MetroLineRepository {

    override suspend fun getMetroLines(
        name: String?,
        code: String?,
        status: String?,
        page: Int?,
        size: Int?
    ): Flow<Result<PageDto<MetroLine>>> = flow {
        val serverFlow: IFlow<PageDto<MetroLine>> = ServerFlow(
            getData = { retrofitServer.metroLineApi.getMetroLines(name, code, status, page, size) },
            convert = { response: com.vidz.data.server.dto.PageDto<com.vidz.data.server.dto.MetroLineDto> -> 
                response.toDomain { dto -> dto.toDomain() } 
            }
        )
        serverFlow.execute().collect { emit(it) }
    }

    override suspend fun getMetroLineByCode(code: String): Flow<Result<MetroLine>> = flow {
        val serverFlow: IFlow<MetroLine> = ServerFlow(
            getData = { retrofitServer.metroLineApi.getMetroLineByCode(code) },
            convert = { it.toDomain() }
        )
        serverFlow.execute().collect { emit(it) }
    }

    override suspend fun createMetroLine(metroLine: MetroLine): Flow<Result<MetroLine>> = flow {
        val serverFlow: IFlow<MetroLine> = ServerFlow(
            getData = { retrofitServer.metroLineApi.createMetroLine(metroLine.toRequestDto()) },
            convert = { it.toDomain() }
        )
        serverFlow.execute().collect { emit(it) }
    }

    override suspend fun updateMetroLine(code: String, metroLine: MetroLine): Flow<Result<MetroLine>> = flow {
        val serverFlow: IFlow<MetroLine> = ServerFlow(
            getData = { retrofitServer.metroLineApi.updateMetroLine(code, metroLine.toRequestDto()) },
            convert = { it.toDomain() }
        )
        serverFlow.execute().collect { emit(it) }
    }
} 