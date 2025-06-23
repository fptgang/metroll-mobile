package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.TicketValidation
import com.vidz.domain.model.TicketValidationCreateRequest

interface TicketValidationRepository {
    suspend fun getTicketValidations(
        page: Int? = null,
        size: Int? = null,
        search: String? = null
    ): Result<PageDto<TicketValidation>>
    
    suspend fun getTicketValidationById(id: String): Result<TicketValidation>
    
    suspend fun getTicketValidationsByTicketId(ticketId: String): Result<List<TicketValidation>>
    
    suspend fun getTicketValidationsByStationId(stationId: String): Result<List<TicketValidation>>
    
    suspend fun validateTicket(request: TicketValidationCreateRequest): Result<TicketValidation>
} 