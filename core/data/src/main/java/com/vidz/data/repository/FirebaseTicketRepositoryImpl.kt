package com.vidz.data.repository

import com.vidz.data.datasource.firebase.FirebaseTicketDataSource
import com.vidz.domain.Result
import com.vidz.domain.model.FirebaseTicket
import com.vidz.domain.model.FirebaseTicketStatus
import com.vidz.domain.repository.FirebaseTicketRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseTicketRepositoryImpl @Inject constructor(
    private val firebaseTicketDataSource: FirebaseTicketDataSource
) : FirebaseTicketRepository {
    
    override suspend fun getTicketStatus(ticketId: String): Flow<Result<FirebaseTicket?>> = flow {
        emit(Result.Init)
        
        try {
            firebaseTicketDataSource.getTicketStatus(ticketId)
                .map { firebaseTicket ->
                    Result.Success(firebaseTicket)
                }
                .catch { exception ->
                    emit(Result.ServerError.General(exception.message ?: "Failed to get ticket status"))
                }
                .collect { result ->
                    emit(result)
                }
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to get ticket status"))
        }
    }
    
    override suspend fun updateTicketStatus(
        ticketId: String, 
        status: FirebaseTicketStatus
    ): Flow<Result<Unit>> = flow {
        emit(Result.Init)
        
        try {
            firebaseTicketDataSource.updateTicketStatus(ticketId, status)
                .map { success ->
                    if (success) {
                        Result.Success(Unit)
                    } else {
                        Result.ServerError.General("Failed to update ticket status")
                    }
                }
                .catch { exception ->
                    emit(Result.ServerError.General(exception.message ?: "Failed to update ticket status"))
                }
                .collect { result ->
                    emit(result)
                }
        } catch (e: Exception) {
            emit(Result.ServerError.General(e.message ?: "Failed to update ticket status"))
        }
    }
} 