package com.vidz.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Account(
    val id: String = "",
    val email: String = "",
    val fullName: String = "",
    val phoneNumber: String = "",
    val role: AccountRole = AccountRole.CUSTOMER,
    val active: Boolean = true,
    val createdAt: String = "",
    val updatedAt: String = "",
    val assignedStation: String = ""
)

@Serializable
enum class AccountRole {
    ADMIN,
    STAFF,
    CUSTOMER
}

@Serializable
data class AccountCreateRequest(
    val email: String,
    val fullName: String,
    val phoneNumber: String,
    val assignedStation: String = "",
    val role: AccountRole
)

@Serializable
data class AccountUpdateRequest(
    val fullName: String? = null,
    val phoneNumber: String? = null,
    val assignedStation: String? = null,
    val role: AccountRole? = null
)

@Serializable
data class AccountListParams(
    val page: Int? = null,
    val size: Int? = null,
    val search: String? = null
)
