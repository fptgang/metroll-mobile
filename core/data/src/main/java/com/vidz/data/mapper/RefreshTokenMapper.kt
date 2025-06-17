package com.vidz.data.mapper

import com.vidz.data.server.retrofit.dto.RefreshTokenDto
import com.vidz.domain.model.RefreshToken
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RefreshTokenMapper @Inject constructor() : BaseRemoteMapper<RefreshToken, RefreshTokenDto> {

    override fun toDomain(external: RefreshTokenDto): RefreshToken {
        return RefreshToken(
            refreshTokenId = external.refreshTokenId,
            accountId = external.accountId,
            token = external.token,
            ipAddress = external.ipAddress,
            sessionId = external.sessionId,
            clientInfo = external.clientInfo,
            expiryDate = external.expiryDate
        )
    }

    override fun toRemote(domain: RefreshToken): RefreshTokenDto {
        return RefreshTokenDto(
            refreshTokenId = domain.refreshTokenId,
            accountId = domain.accountId,
            token = domain.token,
            ipAddress = domain.ipAddress,
            sessionId = domain.sessionId,
            clientInfo = domain.clientInfo,
            expiryDate = domain.expiryDate
        )
    }
} 