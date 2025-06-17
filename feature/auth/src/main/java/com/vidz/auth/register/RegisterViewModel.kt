package com.vidz.auth.register

import androidx.lifecycle.viewModelScope
import com.vidz.auth.register.RegisterViewModel.RegisterEvent
import com.vidz.auth.register.RegisterViewModel.RegisterUiState
import com.vidz.auth.register.RegisterViewModel.RegisterViewModelState
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result.Init
import com.vidz.domain.Result.ServerError
import com.vidz.domain.Result.Success
import com.vidz.domain.model.Account
import com.vidz.domain.model.RegisterRequest
import com.vidz.domain.usecase.RegisterUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
) : BaseViewModel<RegisterEvent, RegisterUiState, RegisterViewModelState>(
    initState = RegisterViewModelState()
) {

    override fun onTriggerEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.UpdateEmail -> updateEmail(event.email)
            is RegisterEvent.UpdatePassword -> updatePassword(event.password)
            is RegisterEvent.UpdateConfirmPassword -> updateConfirmPassword(event.confirmPassword)
            is RegisterEvent.UpdateFirstName -> updateFirstName(event.firstName)
            is RegisterEvent.UpdateLastName -> updateLastName(event.lastName)
            is RegisterEvent.Register -> register()
            is RegisterEvent.ClearError -> clearError()
            is RegisterEvent.ClearSuccess -> clearSuccess()
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

    private fun updateConfirmPassword(confirmPassword: String) {
        viewModelState.value = viewModelState.value.copy(
            confirmPassword = confirmPassword,
            confirmPasswordError = null
        )
    }

    private fun updateFirstName(firstName: String) {
        viewModelState.value = viewModelState.value.copy(
            firstName = firstName,
            firstNameError = null
        )
    }

    private fun updateLastName(lastName: String) {
        viewModelState.value = viewModelState.value.copy(
            lastName = lastName,
            lastNameError = null
        )
    }

    private fun register() {
        val currentState = viewModelState.value
        
        // Validate input
        if (!validateInput(currentState)) {
            return
        }

        viewModelState.value = currentState.copy(
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            registerUseCase(
                RegisterRequest(
                    email = currentState.email,
                    password = currentState.password,
                    firstName = currentState.firstName,
                    lastName = currentState.lastName
                )
            ).collect { result ->
                when (result) {
                    is Init -> {
                        viewModelState.value = viewModelState.value.copy(isLoading = true)
                    }
                    is Success<*> -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            isRegistrationSuccessful = true,
                            registeredAccount = result.data as? Account
                        )
                    }
                    is ServerError -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun validateInput(state: RegisterViewModelState): Boolean {
        var isValid = true
        var updatedState = state

        if (state.firstName.isBlank()) {
            updatedState = updatedState.copy(firstNameError = "First name is required")
            isValid = false
        }

        if (state.lastName.isBlank()) {
            updatedState = updatedState.copy(lastNameError = "Last name is required")
            isValid = false
        }

        if (state.email.isBlank()) {
            updatedState = updatedState.copy(emailError = "Email is required")
            isValid = false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(state.email).matches()) {
            updatedState = updatedState.copy(emailError = "Please enter a valid email")
            isValid = false
        }

        if (state.password.isBlank()) {
            updatedState = updatedState.copy(passwordError = "Password is required")
            isValid = false
        } else if (state.password.length < 6) {
            updatedState = updatedState.copy(passwordError = "Password must be at least 6 characters")
            isValid = false
        }

        if (state.confirmPassword.isBlank()) {
            updatedState = updatedState.copy(confirmPasswordError = "Please confirm your password")
            isValid = false
        } else if (state.password != state.confirmPassword) {
            updatedState = updatedState.copy(confirmPasswordError = "Passwords do not match")
            isValid = false
        }

        viewModelState.value = updatedState
        return isValid
    }

    private fun clearError() {
        viewModelState.value = viewModelState.value.copy(error = null)
    }

    private fun clearSuccess() {
        viewModelState.value = viewModelState.value.copy(
            isRegistrationSuccessful = false,
            registeredAccount = null
        )
    }

// Events
sealed class RegisterEvent : ViewEvent {
    data class UpdateEmail(val email: String) : RegisterEvent()
    data class UpdatePassword(val password: String) : RegisterEvent()
    data class UpdateConfirmPassword(val confirmPassword: String) : RegisterEvent()
    data class UpdateFirstName(val firstName: String) : RegisterEvent()
    data class UpdateLastName(val lastName: String) : RegisterEvent()
    object Register : RegisterEvent()
    object ClearError : RegisterEvent()
    object ClearSuccess : RegisterEvent()
}

// View Model State
data class RegisterViewModelState(
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val confirmPasswordError: String? = null,
    val firstNameError: String? = null,
    val lastNameError: String? = null,
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistrationSuccessful: Boolean = false,
    val registeredAccount: Account? = null
) : ViewModelState() {
    override fun toUiState(): ViewState = RegisterUiState(
        email = email,
        password = password,
        confirmPassword = confirmPassword,
        firstName = firstName,
        lastName = lastName,
        emailError = emailError,
        passwordError = passwordError,
        confirmPasswordError = confirmPasswordError,
        firstNameError = firstNameError,
        lastNameError = lastNameError,
        isLoading = isLoading,
        error = error,
        isRegistrationSuccessful = isRegistrationSuccessful,
        isFormValid = firstName.isNotBlank() && lastName.isNotBlank() && 
                      email.isNotBlank() && password.isNotBlank() && 
                      confirmPassword.isNotBlank()
    )
}

// UI State
data class RegisterUiState(
    val email: String,
    val password: String,
    val confirmPassword: String,
    val firstName: String,
    val lastName: String,
    val emailError: String?,
    val passwordError: String?,
    val confirmPasswordError: String?,
    val firstNameError: String?,
    val lastNameError: String?,
    val isLoading: Boolean,
    val error: String?,
    val isRegistrationSuccessful: Boolean,
    val isFormValid: Boolean
) : ViewState()
}
