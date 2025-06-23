package com.vidz.auth.register

import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.User
import com.vidz.domain.usecase.auth.HybridRegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val hybridRegisterUseCase: HybridRegisterUseCase
) : BaseViewModel<RegisterViewModel.RegisterEvent, RegisterViewModel.RegisterViewState, RegisterViewModel.RegisterViewModelState>(
    initState = RegisterViewModelState()
) {

    override fun onTriggerEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.EmailChanged -> {
                viewModelState.value = viewModelState.value.copy(
                    email = event.email,
                    emailError = null
                )
            }
            is RegisterEvent.DisplayNameChanged -> {
                viewModelState.value = viewModelState.value.copy(
                    displayName = event.displayName,
                    displayNameError = null
                )
            }
            is RegisterEvent.PasswordChanged -> {
                viewModelState.value = viewModelState.value.copy(
                    password = event.password,
                    passwordError = null
                )
            }
            is RegisterEvent.ConfirmPasswordChanged -> {
                viewModelState.value = viewModelState.value.copy(
                    confirmPassword = event.confirmPassword,
                    confirmPasswordError = null
                )
            }
            is RegisterEvent.PasswordVisibilityToggled -> {
                viewModelState.value = viewModelState.value.copy(
                    isPasswordVisible = !viewModelState.value.isPasswordVisible
                )
            }
            is RegisterEvent.ConfirmPasswordVisibilityToggled -> {
                viewModelState.value = viewModelState.value.copy(
                    isConfirmPasswordVisible = !viewModelState.value.isConfirmPasswordVisible
                )
            }
            is RegisterEvent.RegisterClicked -> {
                performRegister()
            }
            is RegisterEvent.ErrorDismissed -> {
                viewModelState.value = viewModelState.value.copy(
                    errorMessage = null
                )
            }
        }
    }

    private fun performRegister() {
        val currentState = viewModelState.value
        
        // Validate inputs
        var hasError = false
        val updatedState = currentState.copy()
        
        if (currentState.email.isBlank()) {
            updatedState.copy(emailError = "Email is required")
            hasError = true
        }
        
        if (currentState.displayName.isBlank()) {
            updatedState.copy(displayNameError = "Display name is required")
            hasError = true
        }
        
        if (currentState.password.isBlank()) {
            updatedState.copy(passwordError = "Password is required")
            hasError = true
        } else if (currentState.password.length < 6) {
            updatedState.copy(passwordError = "Password must be at least 6 characters")
            hasError = true
        }
        
        if (currentState.confirmPassword.isBlank()) {
            updatedState.copy(confirmPasswordError = "Please confirm your password")
            hasError = true
        } else if (currentState.password != currentState.confirmPassword) {
            updatedState.copy(confirmPasswordError = "Passwords do not match")
            hasError = true
        }
        
        if (hasError) {
            viewModelState.value = updatedState
            return
        }

        viewModelScope.launch {
            hybridRegisterUseCase(
                email = currentState.email,
                password = currentState.password,
                confirmPassword = currentState.confirmPassword,
                displayName = currentState.displayName.takeIf { it.isNotBlank() }
            ).collect { result ->
                when (result) {
                    is Result.Init -> {
                        viewModelState.value = viewModelState.value.copy(isLoading = true)
                    }
                    is Result.Success -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            isRegistrationSuccessful = true,
                            user = result.data
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

    data class RegisterViewModelState(
        val email: String = "",
        val displayName: String = "",
        val password: String = "",
        val confirmPassword: String = "",
        val isPasswordVisible: Boolean = false,
        val isConfirmPasswordVisible: Boolean = false,
        val isLoading: Boolean = false,
        val emailError: String? = null,
        val displayNameError: String? = null,
        val passwordError: String? = null,
        val confirmPasswordError: String? = null,
        val errorMessage: String? = null,
        val isRegistrationSuccessful: Boolean = false,
        val user: User? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = RegisterViewState(
            email = email,
            displayName = displayName,
            password = password,
            confirmPassword = confirmPassword,
            isPasswordVisible = isPasswordVisible,
            isConfirmPasswordVisible = isConfirmPasswordVisible,
            isLoading = isLoading,
            emailError = emailError,
            displayNameError = displayNameError,
            passwordError = passwordError,
            confirmPasswordError = confirmPasswordError,
            errorMessage = errorMessage,
            isRegistrationSuccessful = isRegistrationSuccessful
        )
    }

    data class RegisterViewState(
        val email: String,
        val displayName: String,
        val password: String,
        val confirmPassword: String,
        val isPasswordVisible: Boolean,
        val isConfirmPasswordVisible: Boolean,
        val isLoading: Boolean,
        val emailError: String?,
        val displayNameError: String?,
        val passwordError: String?,
        val confirmPasswordError: String?,
        val errorMessage: String?,
        val isRegistrationSuccessful: Boolean
    ) : ViewState()

    sealed class RegisterEvent : ViewEvent {
        data class EmailChanged(val email: String) : RegisterEvent()
        data class DisplayNameChanged(val displayName: String) : RegisterEvent()
        data class PasswordChanged(val password: String) : RegisterEvent()
        data class ConfirmPasswordChanged(val confirmPassword: String) : RegisterEvent()
        object PasswordVisibilityToggled : RegisterEvent()
        object ConfirmPasswordVisibilityToggled : RegisterEvent()
        object RegisterClicked : RegisterEvent()
        object ErrorDismissed : RegisterEvent()
    }
}
