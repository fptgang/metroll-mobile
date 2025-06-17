package com.vidz.staff.scanner

import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class StaffQrScannerViewModel @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<StaffQrScannerViewModel.StaffQrScannerViewEvent,
        StaffQrScannerViewModel.StaffQrScannerViewState,
        StaffQrScannerViewModel.StaffQrScannerViewModelState>(
    initState = StaffQrScannerViewModelState()
) {

    init {
        //TODO: load init data
    }

    override fun onTriggerEvent(event: StaffQrScannerViewEvent) {
        when (event) {
            // Handle events here
            else -> {}
        }
    }

    data class StaffQrScannerViewModelState(
        val isLoading: Boolean = false,
        val error: String? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = StaffQrScannerViewState(
            isLoading = isLoading,
            error = error
        )
    }

    data class StaffQrScannerViewState(
        val isLoading: Boolean,
        val error: String?
    ) : ViewState()

    sealed class StaffQrScannerViewEvent : ViewEvent {
        // Define events here
    }
} 
