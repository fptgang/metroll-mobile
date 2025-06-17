package com.vidz.auth.login

import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.User
import com.vidz.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : BaseViewModel<LoginEvent, LoginUiState, LoginViewModelState>(
    initState = LoginViewModelState()
) {
    
    override fun onTriggerEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> updateEmail(event.email)
            is LoginEvent.PasswordChanged -> updatePassword(event.password)
            is LoginEvent.RememberMeToggled -> updateRememberMe(event.enabled)
            is LoginEvent.PasswordVisibilityToggled -> togglePasswordVisibility()
            is LoginEvent.LoginClicked -> performLogin()
            is LoginEvent.BiometricLoginClicked -> performBiometricLogin()
            is LoginEvent.ForgotPasswordClicked -> {
                // Handle forgot password navigation - can be handled in UI
            }
            is LoginEvent.SignUpClicked -> {
                // Handle sign up navigation - can be handled in UI
            }
            is LoginEvent.ClearError -> clearError()
        }
    }
    
    private fun updateEmail(email: String) {
        viewModelState.value = viewModelState.value.copy(
            email = email,
            emailError = null
        )
    }
    
    private fun updatePassword(password: String) {
        viewModelState.value = viewModelState.value.copy(
            password = password,
            passwordError = null
        )
    }
    
    private fun updateRememberMe(enabled: Boolean) {
        viewModelState.value = viewModelState.value.copy(
            rememberMe = enabled
        )
    }
    
    private fun togglePasswordVisibility() {
        viewModelState.value = viewModelState.value.copy(
            isPasswordVisible = !viewModelState.value.isPasswordVisible
        )
    }
    
    private fun performLogin() {
        val currentState = viewModelState.value
        
        // Validate form first
        if (!validateForm(currentState)) {
            return
        }
        
        viewModelState.value = currentState.copy(
            isLoading = true,
            errorMessage = null
        )
        
        viewModelScope.launch {
            authUseCase.login(
                email = currentState.email,
                password = currentState.password,
                rememberMe = currentState.rememberMe
            ).collect { result ->
                when (result) {
                    is Result.Init -> {
                        viewModelState.value = viewModelState.value.copy(isLoading = true)
                    }
                    is Result.Success<*> -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            loginSuccess = true,
                            user = result.data as? User
                        )
                    }
                    is Result.ServerError -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    
    private fun performBiometricLogin() {
        viewModelState.value = viewModelState.value.copy(
            isLoading = true,
            errorMessage = null
        )
        
        viewModelScope.launch {
            authUseCase.authenticateWithBiometrics().collect { result ->
                when (result) {
                    is Result.Init -> {
                        viewModelState.value = viewModelState.value.copy(isLoading = true)
                    }
                    is Result.Success<*> -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            loginSuccess = true,
                            user = result.data as? User
                        )
                    }
                    is Result.ServerError -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            errorMessage = result.message
                        )
                    }
                }
            }
        }
    }
    
    private fun validateForm(state: LoginViewModelState): Boolean {
        var isValid = true
        var updatedState = state
        
        // Email validation
        val emailError = when {
            state.email.isBlank() -> "Email is required"
            !android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches() -> "Please enter a valid email"
            else -> null
        }
        
        // Password validation
        val passwordError = when {
            state.password.isBlank() -> "Password is required"
            state.password.length < 8 -> "Password must be at least 8 characters"
            else -> null
        }
        
        if (emailError != null || passwordError != null) {
            isValid = false
            updatedState = state.copy(
                emailError = emailError,
                passwordError = passwordError
            )
        }
        
        viewModelState.value = updatedState
        return isValid
    }
    
    private fun clearError() {
        viewModelState.value = viewModelState.value.copy(
            errorMessage = null,
            emailError = null,
            passwordError = null
        )
    }
}

// Events
sealed class LoginEvent : ViewEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    data class RememberMeToggled(val enabled: Boolean) : LoginEvent()
    object PasswordVisibilityToggled : LoginEvent()
    object LoginClicked : LoginEvent()
    object BiometricLoginClicked : LoginEvent()
    object ForgotPasswordClicked : LoginEvent()
    object SignUpClicked : LoginEvent()
    object ClearError : LoginEvent()
}

// View Model State (Internal state)
data class LoginViewModelState(
    val email: String = "",
    val password: String = "",
    val rememberMe: Boolean = false,
    val isPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val emailError: String? = null,
    val passwordError: String? = null,
    val loginSuccess: Boolean = false,
    val user: User? = null
) : ViewModelState() {
    override fun toUiState(): ViewState = LoginUiState(
        email = email,
        password = password,
        rememberMe = rememberMe,
        isPasswordVisible = isPasswordVisible,
        isLoading = isLoading,
        errorMessage = errorMessage,
        emailError = emailError,
        passwordError = passwordError,
        loginSuccess = loginSuccess,
        user = user,
        isFormValid = email.isNotBlank() && password.isNotBlank() && emailError == null && passwordError == null,
        canLogin = email.isNotBlank() && password.isNotBlank() && emailError == null && passwordError == null && !isLoading
    )
}

// UI State (Exposed to UI)
data class LoginUiState(
    val email: String,
    val password: String,
    val rememberMe: Boolean,
    val isPasswordVisible: Boolean,
    val isLoading: Boolean,
    val errorMessage: String?,
    val emailError: String?,
    val passwordError: String?,
    val loginSuccess: Boolean,
    val user: User?,
    val isFormValid: Boolean,
    val canLogin: Boolean
) : ViewState()
