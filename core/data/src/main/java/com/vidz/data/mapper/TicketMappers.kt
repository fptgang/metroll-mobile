package com.vidz.data.mapper

import com.vidz.data.server.dto.P2PJourneyCreateRequestDto
import com.vidz.data.server.dto.P2PJourneyDto
import com.vidz.data.server.dto.P2PJourneyUpdateRequestDto
import com.vidz.data.server.dto.TicketDashboardDto
import com.vidz.data.server.dto.TicketDto
import com.vidz.data.server.dto.TicketUpsertRequestDto
import com.vidz.data.server.dto.TicketValidationCreateRequestDto
import com.vidz.data.server.dto.TicketValidationDto
import com.vidz.data.server.dto.TimedTicketPlanCreateRequestDto
import com.vidz.data.server.dto.TimedTicketPlanDto
import com.vidz.data.server.dto.TimedTicketPlanUpdateRequestDto
import com.vidz.domain.model.P2PJourney
import com.vidz.domain.model.P2PJourneyCreateRequest
import com.vidz.domain.model.P2PJourneyUpdateRequest
import com.vidz.domain.model.Ticket
import com.vidz.domain.model.TicketDashboard
import com.vidz.domain.model.TicketStatus
import com.vidz.domain.model.TicketType
import com.vidz.domain.model.TicketUpsertRequest
import com.vidz.domain.model.TicketValidation
import com.vidz.domain.model.TicketValidationCreateRequest
import com.vidz.domain.model.TimedTicketPlan
import com.vidz.domain.model.TimedTicketPlanCreateRequest
import com.vidz.domain.model.TimedTicketPlanUpdateRequest
import com.vidz.domain.model.ValidationType

// TimedTicketPlan Mappers
fun TimedTicketPlanDto.toDomain(): TimedTicketPlan {
    return TimedTicketPlan(
        id = id,
        name = name,
        validDuration = validDuration,
        basePrice = basePrice,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun TimedTicketPlanCreateRequest.toDto(): TimedTicketPlanCreateRequestDto {
    return TimedTicketPlanCreateRequestDto(
        name = name,
        validDuration = validDuration,
        basePrice = basePrice
    )
}

fun TimedTicketPlanUpdateRequest.toDto(): TimedTicketPlanUpdateRequestDto {
    return TimedTicketPlanUpdateRequestDto(
        name = name,
        validDuration = validDuration,
        basePrice = basePrice
    )
}

// P2PJourney Mappers
fun P2PJourneyDto.toDomain(): P2PJourney {
    return P2PJourney(
        id = id,
        startStationId = startStationId,
        endStationId = endStationId,
        basePrice = basePrice,
        distance = distance,
        travelTime = travelTime,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun P2PJourneyCreateRequest.toDto(): P2PJourneyCreateRequestDto {
    return P2PJourneyCreateRequestDto(
        startStationId = startStationId,
        endStationId = endStationId,
        basePrice = basePrice,
        distance = distance,
        travelTime = travelTime
    )
}

fun P2PJourneyUpdateRequest.toDto(): P2PJourneyUpdateRequestDto {
    return P2PJourneyUpdateRequestDto(
        startStationId = startStationId,
        endStationId = endStationId,
        basePrice = basePrice,
        distance = distance,
        travelTime = travelTime
    )
}

// Ticket Mappers
fun TicketDto.toDomain(): Ticket {
    return Ticket(
        id = id,
        ticketType = TicketType.valueOf(ticketType),
        ticketNumber = ticketNumber,
        ticketOrderDetailId = ticketOrderDetailId,
        purchaseDate = purchaseDate,
        validUntil = validUntil,
        status = TicketStatus.valueOf(status),
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun TicketUpsertRequest.toDto(): TicketUpsertRequestDto {
    return TicketUpsertRequestDto(
        ticketType = ticketType.name,
        ticketNumber = ticketNumber,
        ticketOrderDetailId = ticketOrderDetailId,
        validUntil = validUntil,
        status = status.name
    )
}

fun TicketDashboardDto.toDomain(): TicketDashboard {
    return TicketDashboard(
        totalTickets = totalTickets,
        ticketsByStatus = ticketsByStatus,
        ticketsByType = ticketsByType,
        totalValidations = totalValidations,
        validationsByType = validationsByType,
        todayValidations = todayValidations,
        totalP2PJourneys = totalP2PJourneys,
        validationsLast7Days = validationsLast7Days,
        lastUpdated = lastUpdated
    )
}

// TicketValidation Mappers
fun TicketValidationDto.toDomain(): TicketValidation {
    return TicketValidation(
        id = id,
        stationId = stationId,
        ticketId = ticketId,
        validationType = ValidationType.valueOf(validationType),
        validationTime = validationTime,
        validatorId = validatorId,
        createdAt = createdAt
    )
}

fun TicketValidationCreateRequest.toDto(): TicketValidationCreateRequestDto {
    return TicketValidationCreateRequestDto(
        ticketId = ticketId,
        validationType = validationType.name
    )
}

// Page Mappers
fun <T, R> com.vidz.data.server.dto.PageDto<T>.toDomain(transform: (T) -> R): com.vidz.domain.model.PageDto<R> {
    return com.vidz.domain.model.PageDto(
        content = this.content.map(transform),
        pageNumber = this.pageNumber,
        pageSize = this.pageSize,
        totalElements = this.totalElements,
        totalPages = this.totalPages,
        last = this.last
    )
} 
