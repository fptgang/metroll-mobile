package com.vidz.ticket.qr

import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.FirebaseTicket
import com.vidz.domain.usecase.firebase.GetFirebaseTicketStatusUseCase
import com.vidz.domain.usecase.ticket.GetQRByTicketIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class QRDisplayViewModel @Inject constructor(
    private val getFirebaseTicketStatusUseCase: GetFirebaseTicketStatusUseCase,
    private val getQRByTicketIdUseCase: GetQRByTicketIdUseCase
) : BaseViewModel<QRDisplayViewModel.QRDisplayEvent, QRDisplayViewModel.QRDisplayUiState, QRDisplayViewModel.QRDisplayViewModelState>(
    initState = QRDisplayViewModelState()
) {

    override fun onTriggerEvent(event: QRDisplayEvent) {
        when (event) {
            is QRDisplayEvent.LoadTicketData -> loadTicketData(event.ticketId)
            is QRDisplayEvent.LoadTicketStatus -> loadTicketStatus(event.ticketId)
            is QRDisplayEvent.ClearError -> clearError()
        }
    }

    private fun loadTicketData(ticketId: String) {
        // Load both QR code and Firebase status
        loadQRCode(ticketId)
        loadTicketStatus(ticketId)
    }

    private fun loadQRCode(ticketId: String) {
        viewModelScope.launch {
            getQRByTicketIdUseCase(ticketId)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            updateState { copy(isLoadingQR = true, qrError = null) }
                        }
                        is Result.Success -> {
                            updateState {
                                copy(
                                    isLoadingQR = false,
                                    qrCodeData = result.data,
                                    qrError = null
                                )
                            }
                        }
                        is Result.ServerError -> {
                            updateState {
                                copy(
                                    isLoadingQR = false,
                                    qrError = result.message
                                )
                            }
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun loadTicketStatus(ticketId: String) {
        viewModelScope.launch {
            getFirebaseTicketStatusUseCase(ticketId)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            updateState { copy(isLoadingStatus = true, statusError = null) }
                        }
                        is Result.Success -> {
                            updateState {
                                copy(
                                    isLoadingStatus = false,
                                    firebaseTicket = result.data,
                                    statusError = null
                                )
                            }
                        }
                        is Result.ServerError -> {
                            updateState {
                                copy(
                                    isLoadingStatus = false,
                                    statusError = result.message
                                )
                            }
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun clearError() {
        updateState { copy(statusError = null, qrError = null) }
    }

    private fun updateState(update: QRDisplayViewModelState.() -> QRDisplayViewModelState) {
        viewModelState.value = viewModelState.value.update()
    }

    sealed interface QRDisplayEvent : ViewEvent {
        data class LoadTicketData(val ticketId: String) : QRDisplayEvent
        data class LoadTicketStatus(val ticketId: String) : QRDisplayEvent
        object ClearError : QRDisplayEvent
    }

    data class QRDisplayViewModelState(
        val firebaseTicket: FirebaseTicket? = null,
        val isLoadingStatus: Boolean = false,
        val statusError: String? = null,
        val qrCodeData: String? = null,
        val isLoadingQR: Boolean = false,
        val qrError: String? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = QRDisplayUiState(
            firebaseTicket = firebaseTicket,
            isLoadingStatus = isLoadingStatus,
            statusError = statusError,
            qrCodeData = qrCodeData,
            isLoadingQR = isLoadingQR,
            qrError = qrError
        )
    }

    data class QRDisplayUiState(
        val firebaseTicket: FirebaseTicket?,
        val isLoadingStatus: Boolean,
        val statusError: String?,
        val qrCodeData: String?,
        val isLoadingQR: Boolean,
        val qrError: String?
    ) : ViewState()
} 