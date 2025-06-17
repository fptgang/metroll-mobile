package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: Long = 0L,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val isEmailVerified: Boolean = false,
    val role: AccountRole = AccountRole.Customer,
    val createdAt: String = "",
    val updatedAt: String = ""
)

@Serializable
sealed class AccountRole {
    @Serializable
    data object Admin : AccountRole()
    @Serializable
    data object Staff : AccountRole()
    @Serializable
    data object Customer : AccountRole()
}