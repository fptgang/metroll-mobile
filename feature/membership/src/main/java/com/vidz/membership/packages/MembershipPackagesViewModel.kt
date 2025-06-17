package com.vidz.membership.packages

import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class MembershipPackagesViewModel @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<MembershipPackagesViewModel.MembershipPackagesViewEvent,
        MembershipPackagesViewModel.MembershipPackagesViewState,
        MembershipPackagesViewModel.MembershipPackagesViewModelState>(
    initState = MembershipPackagesViewModelState()
) {

    init {
        //TODO: load init data
    }

    override fun onTriggerEvent(event: MembershipPackagesViewEvent) {
        when (event) {
            // Handle events here
            else -> {}
        }
    }

    data class MembershipPackagesViewModelState(
        val isLoading: Boolean = false,
        val error: String? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = MembershipPackagesViewState(
            isLoading = isLoading,
            error = error
        )
    }

    data class MembershipPackagesViewState(
        val isLoading: Boolean,
        val error: String?
    ) : ViewState()

    sealed class MembershipPackagesViewEvent : ViewEvent {
        // Define events here
    }
} 
