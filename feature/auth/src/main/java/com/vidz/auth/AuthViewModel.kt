package com.vidz.auth

import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<AuthViewModel.AuthViewEvent,
        AuthViewModel.AuthViewState,
        AuthViewModel.AuthViewModelState>(
    initState = AuthViewModelState()
) {

    init {
        //TODO: load init data
    }

    override fun onTriggerEvent(event: AuthViewEvent) {
        when (event) {
            is AuthViewEvent.ShowLoginScreen -> {
                viewModelState.value = viewModelState.value.copy(
                    currentScreen = AuthScreen.LOGIN
                )
            }
            is AuthViewEvent.ShowRegisterScreen -> {
                viewModelState.value = viewModelState.value.copy(
                    currentScreen = AuthScreen.REGISTER
                )
            }
            is AuthViewEvent.ShowForgotPasswordScreen -> {
                viewModelState.value = viewModelState.value.copy(
                    currentScreen = AuthScreen.FORGOT_PASSWORD
                )
            }
        }
    }

    data class AuthViewModelState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val currentScreen: AuthScreen = AuthScreen.LOGIN
    ) : ViewModelState() {
        override fun toUiState(): ViewState = AuthViewState(
            isLoading = isLoading,
            error = error,
            currentScreen = currentScreen
        )
    }

    data class AuthViewState(
        val isLoading: Boolean,
        val error: String?,
        val currentScreen: AuthScreen
    ) : ViewState()

    sealed class AuthViewEvent : ViewEvent {
        object ShowLoginScreen : AuthViewEvent()
        object ShowRegisterScreen : AuthViewEvent()
        object ShowForgotPasswordScreen : AuthViewEvent()
    }

    enum class AuthScreen {
        LOGIN,
        REGISTER,
        FORGOT_PASSWORD
    }
}
