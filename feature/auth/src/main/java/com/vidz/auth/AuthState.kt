package com.vidz.auth

/**
 * Defines the state for
 */
data class AuthState(
    val isLoading: Boolean = false,
    val error: String? = null,
)
