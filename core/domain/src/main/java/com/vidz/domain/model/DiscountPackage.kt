package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class DiscountPackage(
    val id: String = "",
    val name: String = "",
    val description: String = "",
    val discountPercentage: Float = 0f,
    val duration: Int = 0,
    val status: DiscountPackageStatus = DiscountPackageStatus.ACTIVE,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
enum class DiscountPackageStatus {
    ACTIVE,
    TERMINATED
}

@Serializable
data class DiscountPackageCreateRequest(
    val name: String,
    val description: String,
    val discountPercentage: Float,
    val duration: Int
)

@Serializable
data class DiscountPackageUpdateRequest(
    val name: String,
    val description: String,
    val discountPercentage: Float,
    val duration: Int
)

@Serializable
data class DiscountPackageListParams(
    val page: Int? = null,
    val size: Int? = null,
    val search: String? = null
) 