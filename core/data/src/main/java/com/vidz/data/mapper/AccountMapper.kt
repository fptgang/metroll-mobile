package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.AccountDto
import com.vidz.data.server.retrofit.dto.RoleEnum
import com.vidz.domain.model.Account
import com.vidz.domain.model.AccountRole
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountMapper @Inject constructor() : BaseRemoteMapper<Account, AccountDto> {

    override fun toDomain(external: AccountDto): Account {
        return Account(
            id = external.id.toString(),
            email = external.email,
            fullName = "${external.firstName} ${external.lastName}".trim(),
            phoneNumber = "", // Not available in DTO
            role = mapAccountRoleToDomain(external.role),
            active = external.isVerified,
            createdAt = external.createdAt,
            updatedAt = external.updatedAt
        )
    }

    override fun toRemote(domain: Account): AccountDto {
        val nameParts = domain.fullName.split(" ", limit = 2)
        val firstName = nameParts.getOrNull(0) ?: ""
        val lastName = nameParts.getOrNull(1) ?: ""
        
        return AccountDto(
            id = domain.id.toLongOrNull() ?: 0L,
            email = domain.email,
            firstName = firstName,
            lastName = lastName,
            role = mapAccountRoleToDto(domain.role),
            isVerified = domain.active,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    private fun mapAccountRoleToDomain(dtoRole: RoleEnum): AccountRole {
        return when (dtoRole) {
            RoleEnum.ADMIN -> AccountRole.ADMIN
            RoleEnum.USER -> AccountRole.CUSTOMER
        }
    }

    private fun mapAccountRoleToDto(domainRole: AccountRole): RoleEnum {
        return when (domainRole) {
            AccountRole.ADMIN -> RoleEnum.ADMIN
            AccountRole.STAFF -> RoleEnum.USER
            AccountRole.CUSTOMER -> RoleEnum.USER
        }
    }
} 