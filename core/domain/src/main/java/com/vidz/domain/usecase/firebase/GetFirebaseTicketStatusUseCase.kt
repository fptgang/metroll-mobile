package com.vidz.domain.usecase.firebase

import com.vidz.domain.Result
import com.vidz.domain.model.FirebaseTicket
import com.vidz.domain.repository.FirebaseTicketRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFirebaseTicketStatusUseCase @Inject constructor(
    private val firebaseTicketRepository: FirebaseTicketRepository
) {
    suspend operator fun invoke(ticketId: String): Flow<Result<FirebaseTicket?>> {
        return firebaseTicketRepository.getTicketStatus(ticketId)
    }
} 