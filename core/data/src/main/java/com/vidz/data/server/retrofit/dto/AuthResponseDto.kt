package com.vidz.data.server.retrofit.dto

data class AuthResponseDto(
    val token: String = "",
    val refreshToken: String = "",
    val email: String = "",
    val accountResponseDTO: AccountDto = AccountDto()
) 