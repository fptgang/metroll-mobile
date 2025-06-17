package com.vidz.data.server.retrofit.dto

data class ResetPasswordRequestDto(
    val token: String = "",
    val newPassword: String = "",
    val confirmPassword: String = ""
) 