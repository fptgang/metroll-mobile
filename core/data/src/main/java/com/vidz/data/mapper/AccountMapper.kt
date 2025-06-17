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
            id = external.id,
            email = external.email,
            firstName = external.firstName,
            lastName = external.lastName,
            isEmailVerified = external.isVerified,
            role = mapAccountRoleToDomain(external.role),
            createdAt = external.createdAt,
            updatedAt = external.updatedAt
        )
    }

    override fun toRemote(domain: Account): AccountDto {
        return AccountDto(
            id = domain.id,
            email = domain.email,
            firstName = domain.firstName,
            lastName = domain.lastName,
            role = mapAccountRoleToDto(domain.role),
            isVerified = domain.isEmailVerified,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    private fun mapAccountRoleToDomain(dtoRole: RoleEnum): AccountRole {
        return when (dtoRole) {
            RoleEnum.ADMIN -> AccountRole.Admin
            RoleEnum.USER -> AccountRole.Customer
        }
    }

    private fun mapAccountRoleToDto(domainRole: AccountRole): RoleEnum {
        return when (domainRole) {
            AccountRole.Admin -> RoleEnum.ADMIN
            AccountRole.Staff -> RoleEnum.USER
            AccountRole.Customer -> RoleEnum.USER
        }
    }
} 