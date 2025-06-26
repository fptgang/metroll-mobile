package com.vidz.home.customerhome

import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.Account
import com.vidz.domain.usecase.account.ObserveLocalAccountInfoUseCase
import com.vidz.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CustomerHomeViewModel @Inject constructor(
    private val observeLocalAccountInfoUseCase: ObserveLocalAccountInfoUseCase,
    private val logoutUseCase: LogoutUseCase
) : BaseViewModel<CustomerHomeViewModel.CustomerHomeEvent, CustomerHomeViewModel.CustomerHomeViewState, CustomerHomeViewModel.CustomerHomeViewModelState>(
    initState = CustomerHomeViewModelState()
) {

    init {
        observeLocalAccountInfo()
    }

    private fun observeLocalAccountInfo() {
        viewModelScope.launch {
            observeLocalAccountInfoUseCase().collect { account ->
                viewModelState.value = viewModelState.value.copy(
                    localAccount = account,
                    customerName = account?.fullName ?: "Guest User",
                    isLoggedIn = account != null
                )
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            viewModelState.value = viewModelState.value.copy(
                isLoggingOut = true
            )
            
            logoutUseCase().collect { result ->
                when (result) {
                    is Result.Success -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoggingOut = false,
                            logoutSuccessful = true
                        )
                    }
                    is Result.ServerError -> {
                        viewModelState.value = viewModelState.value.copy(
                            isLoggingOut = false,
                            snackbarMessage = result.message
                        )
                    }
                    is Result.Init -> {
                        // Keep loading state
                    }
                }
            }
        }
    }

    override fun onTriggerEvent(event: CustomerHomeEvent) {
        when (event) {
            is CustomerHomeEvent.ShowSnackbar -> {
                viewModelState.value = viewModelState.value.copy(
                    snackbarMessage = event.message
                )
            }
            is CustomerHomeEvent.DismissSnackbar -> {
                viewModelState.value = viewModelState.value.copy(
                    snackbarMessage = null
                )
            }
            is CustomerHomeEvent.LogoutClicked -> {
                logout()
            }
            is CustomerHomeEvent.LogoutSuccessAcknowledged -> {
                viewModelState.value = viewModelState.value.copy(
                    logoutSuccessful = false
                )
            }
        }
    }

    data class CustomerHomeViewModelState(
        val localAccount: Account? = null,
        val customerName: String = "Guest User",
        val isLoggedIn: Boolean = false,
        val isLoggingOut: Boolean = false,
        val logoutSuccessful: Boolean = false,
        val snackbarMessage: String? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = CustomerHomeViewState(
            localAccount = localAccount,
            customerName = customerName,
            isLoggedIn = isLoggedIn,
            isLoggingOut = isLoggingOut,
            logoutSuccessful = logoutSuccessful,
            snackbarMessage = snackbarMessage
        )
    }

    data class CustomerHomeViewState(
        val localAccount: Account?,
        val customerName: String,
        val isLoggedIn: Boolean,
        val isLoggingOut: Boolean,
        val logoutSuccessful: Boolean,
        val snackbarMessage: String?
    ) : ViewState()

    sealed class CustomerHomeEvent : ViewEvent {
        data class ShowSnackbar(val message: String) : CustomerHomeEvent()
        object DismissSnackbar : CustomerHomeEvent()
        object LogoutClicked : CustomerHomeEvent()
        object LogoutSuccessAcknowledged : CustomerHomeEvent()
    }
} 
