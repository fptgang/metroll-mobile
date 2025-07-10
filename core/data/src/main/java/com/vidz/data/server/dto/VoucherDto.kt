package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class VoucherDto(
    val id: String = "",
    val ownerId: String = "",
    val code: String = "",
    val discountAmount: Double = 0.0,
    val minTransactionAmount: Double = 0.0,
    val validFrom: String = "",
    val validUntil: String = "",
    val status: String = "",
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
data class VoucherCreateRequestDto(
    val discountAmount: Double,
    val minTransactionAmount: Double,
    val validFrom: String,
    val validUntil: String,
    val ownerIds: List<String>
)

@Serializable
data class VoucherUpdateRequestDto(
    val discountAmount: Double? = null,
    val minTransactionAmount: Double? = null,
    val validFrom: String? = null,
    val validUntil: String? = null
) 