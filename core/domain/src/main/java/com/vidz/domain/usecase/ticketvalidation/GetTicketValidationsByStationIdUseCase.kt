package com.vidz.domain.usecase.ticketvalidation

import com.vidz.domain.Result
import com.vidz.domain.model.TicketValidation
import com.vidz.domain.repository.TicketValidationRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class GetTicketValidationsByStationIdUseCase @Inject constructor(
    private val repository: TicketValidationRepository
) {
    suspend operator fun invoke(stationId: String): Flow<Result<List<TicketValidation>>> = flow {
        try {
            android.util.Log.d("GetTicketValidationsUseCase", "UseCase called with stationId: $stationId")
            emit(Result.Init)
            
            if (stationId.isBlank()) {
                android.util.Log.w("GetTicketValidationsUseCase", "Station ID is blank")
                emit(Result.ServerError.MissingParam("Station ID is required"))
                return@flow
            }
            
            android.util.Log.d("GetTicketValidationsUseCase", "Calling repository.getTicketValidationsByStationId")
            val result = repository.getTicketValidationsByStationId(stationId)
            android.util.Log.d("GetTicketValidationsUseCase", "Repository result: $result")
            emit(result)
        } catch (e: Exception) {
            android.util.Log.e("GetTicketValidationsUseCase", "Exception in use case", e)
            emit(Result.ServerError.General(e.message ?: "Failed to get ticket validations by station ID"))
        }
    }
} 