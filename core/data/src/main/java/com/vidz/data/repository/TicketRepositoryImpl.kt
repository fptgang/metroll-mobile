package com.vidz.data.repository

import com.vidz.data.flow.IFlow
import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.toDomain
import com.vidz.data.mapper.toDto
import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.Ticket
import com.vidz.domain.model.TicketDashboard
import com.vidz.domain.model.TicketStatus
import com.vidz.domain.model.TicketUpsertRequest
import com.vidz.domain.repository.TicketRepository
import javax.inject.Inject
import kotlinx.coroutines.flow.Flow

class TicketRepositoryImpl @Inject constructor(
    private val retrofitServer: RetrofitServer
) : TicketRepository {

    override suspend fun getTickets(
        page: Int?,
        size: Int?,
        search: String?
    ): Result<PageDto<Ticket>> {
        val flow: IFlow<PageDto<Ticket>> = ServerFlow(
            getData = { retrofitServer.ticketApi.getTickets(page, size, search) },
            convert = { response: com.vidz.data.server.dto.PageDto<com.vidz.data.server.dto.TicketDto> -> 
                response.toDomain { dto -> dto.toDomain() } 
            }
        )
        return flow.execute().let { flowResult ->
            var result: Result<PageDto<Ticket>> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun getTicketById(id: String): Result<Ticket> {
        val flow: IFlow<Ticket> = ServerFlow(
            getData = { retrofitServer.ticketApi.getTicketById(id) },
            convert = { it.toDomain() }
        )
        return flow.execute().let { flowResult ->
            var result: Result<Ticket> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun getTicketByNumber(ticketNumber: String): Result<Ticket> {
        val flow: IFlow<Ticket> = ServerFlow(
            getData = { retrofitServer.ticketApi.getTicketByNumber(ticketNumber) },
            convert = { it.toDomain() }
        )
        return flow.execute().let { flowResult ->
            var result: Result<Ticket> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun getTicketsByStatus(status: TicketStatus): Result<List<Ticket>> {
        val flow: IFlow<List<Ticket>> = ServerFlow(
            getData = { retrofitServer.ticketApi.getTicketsByStatus(status.name) },
            convert = { it.map { dto -> dto.toDomain() } }
        )
        return flow.execute().let { flowResult ->
            var result: Result<List<Ticket>> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun getTicketsByOrderDetailId(orderDetailId: String): Result<Ticket> {
        val flow: IFlow<Ticket> = ServerFlow(
            getData = { retrofitServer.ticketApi.getTicketsByOrderDetailId(orderDetailId) },
            convert = { it.toDomain() }
        )
        return flow.execute().let { flowResult ->
            var result: Result<Ticket> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun getTicketDashboard(): Result<TicketDashboard> {
        val flow: IFlow<TicketDashboard> = ServerFlow(
            getData = { retrofitServer.ticketApi.getTicketDashboard() },
            convert = { it.toDomain() }
        )
        return flow.execute().let { flowResult ->
            var result: Result<TicketDashboard> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun createTicket(request: TicketUpsertRequest): Result<Ticket> {
        val flow: IFlow<Ticket> = ServerFlow(
            getData = { retrofitServer.ticketApi.createTicket(request.toDto()) },
            convert = { it.toDomain() }
        )
        return flow.execute().let { flowResult ->
            var result: Result<Ticket> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun createTickets(requests: List<TicketUpsertRequest>): Result<List<Ticket>> {
        val flow: IFlow<List<Ticket>> = ServerFlow(
            getData = { retrofitServer.ticketApi.createTickets(requests.map { it.toDto() }) },
            convert = { it.map { dto -> dto.toDomain() } }
        )
        return flow.execute().let { flowResult ->
            var result: Result<List<Ticket>> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun updateTicketStatus(id: String, status: TicketStatus): Result<Unit> {
        val flow: IFlow<Unit> = ServerFlow(
            getData = { retrofitServer.ticketApi.updateTicketStatus(id, status.name) },
            convert = { Unit }
        )
        return flow.execute().let { flowResult ->
            var result: Result<Unit> = Result.Init
            flowResult.collect { result = it }
            result
        }
    }

    override suspend fun getTicketQRCode(ticketId: String): Flow<Result<String>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.ticketApi.getTicketQRCode(ticketId)
                val body = response.body() ?: throw NullPointerException("QR code response body is null")
                body.string()
            },
            convert = { qrCodeString ->
                qrCodeString
            }
        ).execute()
    }
} 