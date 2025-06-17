package com.vidz.routemanagement.management

import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class RouteManagementViewModel @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<RouteManagementViewModel.RouteManagementViewEvent,
        RouteManagementViewModel.RouteManagementViewState,
        RouteManagementViewModel.RouteManagementViewModelState>(
    initState = RouteManagementViewModelState()
) {

    init {
        //TODO: load init data
    }

    override fun onTriggerEvent(event: RouteManagementViewEvent) {
        when (event) {
            // Handle events here
            else -> {}
        }
    }

    data class RouteManagementViewModelState(
        val isLoading: Boolean = false,
        val error: String? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = RouteManagementViewState(
            isLoading = isLoading,
            error = error
        )
    }

    data class RouteManagementViewState(
        val isLoading: Boolean,
        val error: String?
    ) : ViewState()

    sealed class RouteManagementViewEvent : ViewEvent {
        // Define events here
    }
} 
