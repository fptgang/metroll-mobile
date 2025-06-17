package com.vidz.home.staffhome

import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class StaffHomeViewModel @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<StaffHomeViewModel.StaffHomeViewEvent,
        StaffHomeViewModel.StaffHomeViewState,
        StaffHomeViewModel.StaffHomeViewModelState>(
    initState = StaffHomeViewModelState()
) {

    init {
        //TODO: load init data
    }

    override fun onTriggerEvent(event: StaffHomeViewEvent) {
        when (event) {
            // Handle events here
            else -> {}
        }
    }

    data class StaffHomeViewModelState(
        val isLoading: Boolean = false,
        val error: String? = null
        // Add other state properties here
    ) : ViewModelState() {
        override fun toUiState(): ViewState = StaffHomeViewState(
            isLoading = isLoading,
            error = error
            // Map other properties here
        )
    }

    data class StaffHomeViewState(
        val isLoading: Boolean,
        val error: String?
        // Add other UI state properties here
    ) : ViewState()

    sealed class StaffHomeViewEvent : ViewEvent {
        // Define events here
    }
}
