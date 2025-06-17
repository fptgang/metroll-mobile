package com.vidz.data.server.retrofit.dto

data class RegisterRequestDto(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstName: String = "",
    val lastName: String = ""
) 