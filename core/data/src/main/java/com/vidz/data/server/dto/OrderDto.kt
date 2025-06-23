package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class OrderDto(
    val id: String,
    val staffId: String? = null,
    val customerId: String,
    val discountPackage: String? = null,
    val voucher: String? = null,
    val baseTotal: Double,
    val discountTotal: Double,
    val finalTotal: Double,
    val paymentMethod: String,
    val status: String,
    val transactionReference: String? = null,
    val paymentUrl: String? = null,
    val qrCode: String? = null,
    val orderDetails: List<OrderDetailDto> = emptyList(),
    val createdAt: String,
    val updatedAt: String
) 