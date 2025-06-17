package com.vidz.auth.register

/**
 * Defines the state for
 */
data class RegisterState(
    val isLoading: Boolean = false,
    val error: String? = null,
)
