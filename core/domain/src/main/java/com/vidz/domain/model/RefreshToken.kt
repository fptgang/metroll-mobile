package com.vidz.domain.model

data class RefreshToken(
    val refreshTokenId: Long = 0L,
    val accountId: Long = 0L,
    val token: String = "",
    val ipAddress: String = "",
    val sessionId: String = "",
    val clientInfo: String = "",
    val expiryDate: String = ""
) 