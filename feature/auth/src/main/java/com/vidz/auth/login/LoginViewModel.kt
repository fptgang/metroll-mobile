package com.vidz.auth.login

import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.Account
import com.vidz.domain.model.User
import com.vidz.domain.usecase.account.ObserveLocalAccountInfoUseCase
import com.vidz.domain.usecase.auth.HybridLoginUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val hybridLoginUseCase: HybridLoginUseCase,
    private val observeLocalAccountInfoUseCase: ObserveLocalAccountInfoUseCase
) : BaseViewModel<LoginViewModel.LoginEvent, LoginViewModel.LoginViewState, LoginViewModel.LoginViewModelState>(
    initState = LoginViewModelState()
) {

    init {
        // Observe local account info changes
        viewModelScope.launch {
            observeLocalAccountInfoUseCase().collect { account ->
                if (account != null) {
                    viewModelState.value = viewModelState.value.copy(
                        localAccount = account,
                        isLoginSuccessful = true
                    )
                }
            }
        }
    }

    override fun onTriggerEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                viewModelState.value = viewModelState.value.copy(
                    email = event.email,
                    emailError = null
                )
            }
            is LoginEvent.PasswordChanged -> {
                viewModelState.value = viewModelState.value.copy(
                    password = event.password,
                    passwordError = null
                )
            }
            is LoginEvent.PasswordVisibilityToggled -> {
                viewModelState.value = viewModelState.value.copy(
                    isPasswordVisible = !viewModelState.value.isPasswordVisible
                )
            }
            is LoginEvent.LoginClicked -> {
                performLogin()
            }
            is LoginEvent.ErrorDismissed -> {
                viewModelState.value = viewModelState.value.copy(
                    errorMessage = null
                )
            }
        }
    }

    private fun performLogin() {
        val currentState = viewModelState.value
        
        // Validate inputs
        if (currentState.email.isBlank()) {
            viewModelState.value = currentState.copy(emailError = "Email is required")
            return
        }
        
        if (currentState.password.isBlank()) {
            viewModelState.value = currentState.copy(passwordError = "Password is required")
            return
        }

        viewModelScope.launch {
            hybridLoginUseCase(currentState.email, currentState.password).collect { result ->
                when (result) {
                    is Result.Init -> {
                        viewModelState.value = viewModelState.value.copy(isLoading = true)
                    }
                    is Result.Success -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoading = false,
                            isLoginSuccessful = true,
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

    data class LoginViewModelState(
        val email: String = "",
        val password: String = "",
        val isPasswordVisible: Boolean = false,
        val isLoading: Boolean = false,
        val emailError: String? = null,
        val passwordError: String? = null,
        val errorMessage: String? = null,
        val isLoginSuccessful: Boolean = false,
        val user: User? = null,
        val localAccount: Account? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = LoginViewState(
            email = email,
            password = password,
            isPasswordVisible = isPasswordVisible,
            isLoading = isLoading,
            emailError = emailError,
            passwordError = passwordError,
            errorMessage = errorMessage,
            isLoginSuccessful = isLoginSuccessful,
            localAccount = localAccount
        )
    }

    data class LoginViewState(
        val email: String,
        val password: String,
        val isPasswordVisible: Boolean,
        val isLoading: Boolean,
        val emailError: String?,
        val passwordError: String?,
        val errorMessage: String?,
        val isLoginSuccessful: Boolean,
        val localAccount: Account?
    ) : ViewState()

    sealed class LoginEvent : ViewEvent {
        data class EmailChanged(val email: String) : LoginEvent()
        data class PasswordChanged(val password: String) : LoginEvent()
        object PasswordVisibilityToggled : LoginEvent()
        object LoginClicked : LoginEvent()
        object ErrorDismissed : LoginEvent()
    }
}
