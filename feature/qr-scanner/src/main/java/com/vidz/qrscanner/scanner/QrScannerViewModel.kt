package com.vidz.qrscanner.scanner

import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<QrScannerViewModel.QrScannerViewEvent,
        QrScannerViewModel.QrScannerViewState,
        QrScannerViewModel.QrScannerViewModelState>(
    initState = QrScannerViewModelState()
) {

    init {
        //TODO: load init data
    }

    override fun onTriggerEvent(event: QrScannerViewEvent) {
        when (event) {
            // Handle events here
            else -> {}
        }
    }

    data class QrScannerViewModelState(
        val isLoading: Boolean = false,
        val error: String? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = QrScannerViewState(
            isLoading = isLoading,
            error = error
        )
    }

    data class QrScannerViewState(
        val isLoading: Boolean,
        val error: String?
    ) : ViewState()

    sealed class QrScannerViewEvent : ViewEvent {
        // Define events here
    }
} 
