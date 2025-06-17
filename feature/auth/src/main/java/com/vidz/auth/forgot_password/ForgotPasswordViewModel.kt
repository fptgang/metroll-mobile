package com.vidz.auth.forgot_password

import androidx.lifecycle.viewModelScope
import com.vidz.auth.forgot_password.ForgotPasswordViewModel.ForgotPasswordEvent
import com.vidz.auth.forgot_password.ForgotPasswordViewModel.ForgotPasswordUiState
import com.vidz.auth.forgot_password.ForgotPasswordViewModel.ForgotPasswordViewModelState
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.usecase.ForgotPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ForgotPasswordViewModel @Inject constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase
) : BaseViewModel<ForgotPasswordEvent, ForgotPasswordUiState, ForgotPasswordViewModelState>(
    initState = ForgotPasswordViewModelState()
) {

    override fun onTriggerEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.UpdateEmail -> updateEmail(event.email)
            is ForgotPasswordEvent.SendResetLink -> sendResetLink()
            is ForgotPasswordEvent.ClearError -> clearError()
            is ForgotPasswordEvent.ClearSuccess -> clearSuccess()
        }
    }

    private fun updateEmail(email: String) {
        viewModelState.value = viewModelState.value.copy(
            email = email,
            emailError = null
        )
    }

    private fun sendResetLink() {
        val currentState = viewModelState.value
        
        // Validate input
        if (!validateInput(currentState.email)) {
            return
        }

        viewModelState.value = currentState.copy(
            isLoading = true,
            error = null,
            isResetLinkSent = false
        )

        viewModelScope.launch {
            forgotPasswordUseCase(currentState.email).collect { result ->
                when (result) {
                    is Result.Init -> {
                        viewModelState.value = viewModelState.value.copy(isLoading = true)
                    }
                    is Result.Success<*> -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            isResetLinkSent = true
                        )
                    }
                    is Result.ServerError -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            error = result.message
                        )
                    }
                }
            }
        }
    }

    private fun validateInput(email: String): Boolean {
        val currentState = viewModelState.value

        if (email.isBlank()) {
            viewModelState.value = currentState.copy(
                emailError = "Email is required"
            )
            return false
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            viewModelState.value = currentState.copy(
                emailError = "Please enter a valid email"
            )
            return false
        }

        return true
    }

    private fun clearError() {
        viewModelState.value = viewModelState.value.copy(error = null)
    }

    private fun clearSuccess() {
        viewModelState.value = viewModelState.value.copy(isResetLinkSent = false)
    }

    // Events
    sealed class ForgotPasswordEvent : ViewEvent {
        data class UpdateEmail(val email: String) : ForgotPasswordEvent()
        object SendResetLink : ForgotPasswordEvent()
        object ClearError : ForgotPasswordEvent()
        object ClearSuccess : ForgotPasswordEvent()
    }

    // View Model State
    data class ForgotPasswordViewModelState(
        val email: String = "",
        val emailError: String? = null,
        val isLoading: Boolean = false,
        val error: String? = null,
        val isResetLinkSent: Boolean = false
    ) : ViewModelState() {
        override fun toUiState(): ViewState = ForgotPasswordUiState(
            email = email,
            emailError = emailError,
            isLoading = isLoading,
            error = error,
            isResetLinkSent = isResetLinkSent,
            isFormValid = email.isNotBlank()
        )
    }

    // UI State
    data class ForgotPasswordUiState(
        val email: String,
        val emailError: String?,
        val isLoading: Boolean,
        val error: String?,
        val isResetLinkSent: Boolean,
        val isFormValid: Boolean
    ) : ViewState()
} 
