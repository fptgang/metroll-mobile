package com.vidz.data.mapper

import com.vidz.data.server.dto.CheckoutItemRequestDto
import com.vidz.data.server.dto.CheckoutRequestDto
import com.vidz.data.server.dto.OrderDetailDto
import com.vidz.data.server.dto.OrderDto
import com.vidz.domain.model.CheckoutItem
import com.vidz.domain.model.CheckoutRequest
import com.vidz.domain.model.Order
import com.vidz.domain.model.OrderDetail
import com.vidz.domain.model.OrderStatus
import com.vidz.domain.model.TicketType
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class OrderMapper @Inject constructor() : BaseRemoteMapper<Order, OrderDto> {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    private fun parseTimestamp(timestamp: String): LocalDateTime {
        return try {
            // Try parsing as Unix timestamp (with decimal seconds)
            val instant = Instant.ofEpochSecond(
                timestamp.substringBefore('.').toLong(),
                (timestamp.substringAfter('.', "0").take(9).padEnd(9, '0')).toLong()
            )
            val result = LocalDateTime.ofInstant(instant, ZoneOffset.UTC)
            println("Parsed timestamp '$timestamp' as Unix timestamp: $result")
            result
        } catch (e: NumberFormatException) {
            try {
                // Fallback to ISO LocalDateTime format
                val result = LocalDateTime.parse(timestamp, dateFormatter)
                println("Parsed timestamp '$timestamp' as ISO LocalDateTime: $result")
                result
            } catch (e2: Exception) {
                // Last resort: return current time
                val result = LocalDateTime.now()
                println("Failed to parse timestamp '$timestamp', using current time: $result")
                result
            }
        }
    }

    override fun toDomain(external: OrderDto): Order {
        return Order(
            id = external.id,
            staffId = external.staffId,
            customerId = external.customerId,
            discountPackage = external.discountPackage,
            voucher = external.voucher,
            baseTotal = external.baseTotal,
            discountTotal = external.discountTotal,
            finalTotal = external.finalTotal,
            paymentMethod = external.paymentMethod,
            status = OrderStatus.valueOf(external.status),
            transactionReference = external.transactionReference,
            paymentUrl = external.paymentUrl,
            qrCode = external.qrCode,
            orderDetails = external.orderDetails.map { orderDetailDtoToDomain(it) },
            createdAt = parseTimestamp(external.createdAt),
            updatedAt = parseTimestamp(external.updatedAt)
        )
    }

    override fun toRemote(domain: Order): OrderDto {
        return OrderDto(
            id = domain.id,
            staffId = domain.staffId,
            customerId = domain.customerId,
            discountPackage = domain.discountPackage,
            voucher = domain.voucher,
            baseTotal = domain.baseTotal,
            discountTotal = domain.discountTotal,
            finalTotal = domain.finalTotal,
            paymentMethod = domain.paymentMethod,
            status = domain.status.name,
            transactionReference = domain.transactionReference,
            paymentUrl = domain.paymentUrl,
            qrCode = domain.qrCode,
            orderDetails = domain.orderDetails.map { orderDetailDomainToDto(it) },
            createdAt = domain.createdAt.format(dateFormatter),
            updatedAt = domain.updatedAt.format(dateFormatter)
        )
    }

    private fun orderDetailDtoToDomain(dto: OrderDetailDto): OrderDetail {
        val ticketId = dto.ticketId.ifEmpty { "" }
        return OrderDetail(
            id = dto.id,
            orderId = dto.orderId,
            ticketId = ticketId,
            ticketType = TicketType.valueOf(dto.ticketType),
            p2pJourney = dto.p2pJourney,
            timedTicketPlan = dto.timedTicketPlan,
            quantity = dto.quantity,
            unitPrice = dto.unitPrice,
            baseTotal = dto.baseTotal,
            discountTotal = dto.discountTotal,
            finalTotal = dto.finalTotal,
            createdAt = parseTimestamp(dto.createdAt)
        )
    }

    private fun orderDetailDomainToDto(domain: OrderDetail): OrderDetailDto {
        val ticketId = domain.ticketId.ifEmpty { "" }
        return OrderDetailDto(
            id = domain.id,
            orderId = domain.orderId,
            ticketId = ticketId,
            ticketType = domain.ticketType.name,
            p2pJourney = domain.p2pJourney,
            timedTicketPlan = domain.timedTicketPlan,
            quantity = domain.quantity,
            unitPrice = domain.unitPrice,
            baseTotal = domain.baseTotal,
            discountTotal = domain.discountTotal,
            finalTotal = domain.finalTotal,
            createdAt = domain.createdAt.format(dateFormatter)
        )
    }

    fun checkoutRequestToDto(domain: CheckoutRequest): CheckoutRequestDto {
        return CheckoutRequestDto(
            items = domain.items.map { checkoutItemToDto(it) },
            paymentMethod = domain.paymentMethod,
            voucherId = domain.voucherId,
//            discountPackage = domain.discountPackage,
            customerId = domain.customerId
        )
    }

    private fun checkoutItemToDto(domain: CheckoutItem): CheckoutItemRequestDto {
        return CheckoutItemRequestDto(
            ticketType = domain.ticketType.name,
            p2pJourneyId = domain.p2pJourneyId,
            timedTicketPlanId = domain.timedTicketPlanId,
            quantity = domain.quantity
        )
    }
} 