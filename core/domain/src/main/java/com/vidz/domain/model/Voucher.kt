package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Voucher(
    val id: String = "",
    val code: String = "",
    val discountAmount: Double = 0.0,
    val minTransactionAmount: Double = 0.0,
    val validFrom: String = "",
    val validUntil: String = "",
    val status: VoucherStatus = VoucherStatus.PRESERVED,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
enum class VoucherStatus {
    PRESERVED,
    VALID,
    USED,
    EXPIRED,
    REVOKED
}

@Serializable
data class VoucherCreateRequest(
    val discountAmount: Double,
    val minTransactionAmount: Double,
    val validFrom: String,
    val validUntil: String
)

@Serializable
data class VoucherUpdateRequest(
    val discountAmount: Double? = null,
    val minTransactionAmount: Double? = null,
    val validFrom: String? = null,
    val validUntil: String? = null
)

@Serializable
data class VoucherListParams(
    val page: Int? = null,
    val size: Int? = null,
    val userId: String? = null
) 
