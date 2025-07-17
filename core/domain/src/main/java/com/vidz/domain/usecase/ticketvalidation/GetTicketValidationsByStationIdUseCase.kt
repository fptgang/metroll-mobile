package com.vidz.domain.usecase.ticketvalidation

import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.TicketValidation
import com.vidz.domain.repository.TicketValidationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTicketValidationsByStationIdUseCase @Inject constructor(
    private val repository: TicketValidationRepository
) {
    suspend operator fun invoke(
        stationCode: String,
        page: Int? = null,
        size: Int? = null,
        search: String? = null,
        validationType: String? = null,
        startDate: String? = null,
        endDate: String? = null
    ): Flow<Result<PageDto<TicketValidation>>> = flow {
        try {
            android.util.Log.d("GetTicketValidationsUseCase", "UseCase called with stationCode: $stationCode")
            emit(Result.Init)
            
            if (stationCode.isBlank()) {
                android.util.Log.w("GetTicketValidationsUseCase", "Station code is blank")
                emit(Result.ServerError.MissingParam("Station code is required"))
                return@flow
            }
            
            android.util.Log.d("GetTicketValidationsUseCase", "Calling repository.getTicketValidationsByStationCode")
            val result = repository.getTicketValidationsByStationCode(
                stationCode, page, size, search, validationType, startDate, endDate
            )
            android.util.Log.d("GetTicketValidationsUseCase", "Repository result: $result")
            emit(result)
        } catch (e: Exception) {
            android.util.Log.e("GetTicketValidationsUseCase", "Exception in use case", e)
            emit(Result.ServerError.General(e.message ?: "Failed to get ticket validations by station code"))
        }
    }
} 