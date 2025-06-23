package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.Ticket
import com.vidz.domain.model.TicketStatus
import com.vidz.domain.model.TicketUpsertRequest

interface TicketRepository {
    suspend fun getTickets(
        page: Int? = null,
        size: Int? = null,
        search: String? = null
    ): Result<PageDto<Ticket>>
    
    suspend fun getTicketById(id: String): Result<Ticket>
    
    suspend fun getTicketByNumber(ticketNumber: String): Result<Ticket>
    
    suspend fun getTicketsByStatus(status: TicketStatus): Result<List<Ticket>>
    
    suspend fun getTicketsByOrderDetailId(orderDetailId: String): Result<List<Ticket>>
    
    suspend fun createTicket(request: TicketUpsertRequest): Result<Ticket>
    
    suspend fun createTickets(requests: List<TicketUpsertRequest>): Result<List<Ticket>>
    
    suspend fun updateTicketStatus(id: String, status: TicketStatus): Result<Unit>
} 