package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class AccountDiscountPackage(
    val id: String = "",
    val accountId: String = "",
    val discountPackageId: String = "",
    val activateDate: String = "",
    val validUntil: String = "",
    val status: AccountDiscountPackageStatus = AccountDiscountPackageStatus.ACTIVATED,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
enum class AccountDiscountPackageStatus {
    ACTIVATED,
    EXPIRED,
    CANCELLED
}

@Serializable
data class AccountDiscountAssignRequest(
    val accountId: String,
    val discountPackageId: String
)

@Serializable
data class AccountDiscountPackageListParams(
    val page: Int? = null,
    val size: Int? = null,
    val accountId: String? = null,
    val packageId: String? = null
) 