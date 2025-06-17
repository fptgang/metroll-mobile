package com.vidz.auth.login

/**
 * Defines the state for
 */
data class LoginState(
    val isLoading: Boolean = false,
    val error: String? = null,
)
