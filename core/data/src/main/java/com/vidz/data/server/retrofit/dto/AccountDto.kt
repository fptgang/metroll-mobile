package com.vidz.data.server.retrofit.dto

import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class AccountDto(
    val id: String = "",
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val role: String = "CUSTOMER",
    val active: Boolean = true,
    val createdAt: Double = 0.0,
    val updatedAt: Double = 0.0
)

// Legacy DTO for backward compatibility
@JsonClass(generateAdapter = true)
data class LegacyAccountDto(
    val id: Long = 0L,
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val role: RoleEnum = RoleEnum.USER,
    val isVerified: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
)

enum class RoleEnum(val value: String) {
    ADMIN("ADMIN"),
    USER("USER")
}
