package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.AccountDto
import com.vidz.domain.model.Account
import com.vidz.domain.model.AccountRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountMapper @Inject constructor() : BaseRemoteMapper<Account, AccountDto> {

    override fun toDomain(external: AccountDto): Account {
        return Account(
            id = external.id,
            email = external.email,
            fullName = external.fullName,
            phoneNumber = external.phoneNumber,
            role = mapStringToAccountRole(external.role),
            active = external.active,
            createdAt = external.createdAt.toString(),
            updatedAt = external.updatedAt.toString(),
            assignedStation = external.assignedStation
        )
    }

    override fun toRemote(domain: Account): AccountDto {
        return AccountDto(
            id = domain.id,
            email = domain.email,
            fullName = domain.fullName,
            phoneNumber = domain.phoneNumber,
            role = domain.role.name,
            active = domain.active,
            createdAt = domain.createdAt.toDoubleOrNull() ?: 0.0,
            updatedAt = domain.updatedAt.toDoubleOrNull() ?: 0.0,
            assignedStation = domain.assignedStation
        )
    }

    private fun mapStringToAccountRole(roleString: String): AccountRole {
        return when (roleString.uppercase()) {
            "ADMIN" -> AccountRole.ADMIN
            "STAFF" -> AccountRole.STAFF
            "CUSTOMER" -> AccountRole.CUSTOMER
            "USER" -> AccountRole.CUSTOMER // Map legacy USER to CUSTOMER
            else -> AccountRole.CUSTOMER // Default fallback
        }
    }
} 
