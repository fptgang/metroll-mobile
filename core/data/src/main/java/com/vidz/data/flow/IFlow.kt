package com.vidz.data.flow

import com.vidz.domain.Result
import kotlinx.coroutines.flow.Flow

interface IFlow<Domain> {
    fun execute(): Flow<Result<Domain>>

}
