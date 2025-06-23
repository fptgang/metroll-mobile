package com.vidz.domain.usecase.ticketvalidation

import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.TicketValidation
import com.vidz.domain.repository.TicketValidationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTicketValidationsUseCase @Inject constructor(
    private val repository: TicketValidationRepository
) {
    suspend operator fun invoke(
        page: Int? = null,
        size: Int? = null,
        search: String? = null
    ): Flow<Result<PageDto<TicketValidation>>> = flow {
        try {
            emit(Result.Init)
            val result = repository.getTicketValidations(page, size, search)
            emit(result)
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get ticket validations"))
        }
    }
} 