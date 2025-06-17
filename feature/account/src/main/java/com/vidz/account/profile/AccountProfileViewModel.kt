package com.vidz.account.profile

import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class AccountProfileViewModel @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<AccountProfileViewModel.AccountProfileViewEvent,
        AccountProfileViewModel.AccountProfileViewState,
        AccountProfileViewModel.AccountProfileViewModelState>(
    initState = AccountProfileViewModelState()
) {

    init {
        //TODO: load init data
    }

    override fun onTriggerEvent(event: AccountProfileViewEvent) {
        when (event) {
            // Handle events here
            else -> {}
        }
    }

    data class AccountProfileViewModelState(
        val isLoading: Boolean = false,
        val error: String? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = AccountProfileViewState(
            isLoading = isLoading,
            error = error
        )
    }

    data class AccountProfileViewState(
        val isLoading: Boolean,
        val error: String?
    ) : ViewState()

    sealed class AccountProfileViewEvent : ViewEvent {
        // Define events here
    }
} 
