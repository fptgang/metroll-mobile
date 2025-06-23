package com.vidz.domain.model

import java.time.LocalDateTime

data class Order(
    val id: String,
    val staffId: String? = null,
    val customerId: String,
    val discountPackage: String? = null,
    val voucher: String? = null,
    val baseTotal: Double,
    val discountTotal: Double,
    val finalTotal: Double,
    val paymentMethod: String,
    val status: OrderStatus,
    val transactionReference: String? = null,
    val paymentUrl: String? = null,
    val qrCode: String? = null,
    val orderDetails: List<OrderDetail> = emptyList(),
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime
) 