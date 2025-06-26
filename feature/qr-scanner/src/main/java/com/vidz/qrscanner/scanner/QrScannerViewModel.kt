package com.vidz.qrscanner.scanner

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.TicketValidationCreateRequest
import com.vidz.domain.usecase.ticketvalidation.ValidateTicketUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import javax.inject.Inject

enum class ValidationType {
    ENTRY,
    EXIT
}

@HiltViewModel
class QrScannerViewModel @Inject constructor(
    private val validateTicketUseCase: ValidateTicketUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<QrScannerViewModel.QrScannerViewEvent,
        QrScannerViewModel.QrScannerViewState,
        QrScannerViewModel.QrScannerViewModelState>(
    initState = QrScannerViewModelState()
) {

    // Cache of scanned ticket id to attempt count
    private val scannedAttempts = mutableMapOf<String, Int>()

    // JSON parser with unknown keys ignored
    private val jsonParser = Json { ignoreUnknownKeys = true }

    override fun onTriggerEvent(event: QrScannerViewEvent) {
        when (event) {
            is QrScannerViewEvent.QrDetected -> handleQrDetected(event.raw)
            is QrScannerViewEvent.ClearStatus -> clearStatus()
            is QrScannerViewEvent.ChangeValidationType -> changeValidationType(event.type)
            is QrScannerViewEvent.ScanMore -> scanMore()
            is QrScannerViewEvent.ShowScannerScreen -> showScannerScreen()
        }
    }

    private fun handleQrDetected(raw: String) {
        // Ignore empty strings
        if (raw.isBlank()) return
        Log.d(
            "QrScannerViewModel",
            "QR Detected: $raw"
        )
        val currentStatus = viewModelState.value.status
        // If currently validating, ignore new inputs to avoid race
        if (currentStatus == ScannerStatus.Validating) return

        try {
            val jsonElement = jsonParser.parseToJsonElement(raw)
            val obj = jsonElement.jsonObject

            val ticketId = obj["id"]?.jsonPrimitive?.content ?: obj["ticketNumber"]?.jsonPrimitive?.content ?: ""

            if (ticketId.isBlank()) {
                updateStatus(ScannerStatus.Error("Invalid QR: missing ticketId"))
                return
            }

            val stationId = "684707b570690b4a7a67e0dd"
            val deviceIdVal = "android-device"

            // Use the selected validation type
            val domainValidationType = when (viewModelState.value.selectedValidationType) {
                ValidationType.ENTRY -> com.vidz.domain.model.ValidationType.ENTRY
                ValidationType.EXIT -> com.vidz.domain.model.ValidationType.EXIT
            }

            val request = TicketValidationCreateRequest(
                stationId = stationId,
                ticketId = ticketId,
                validationType = domainValidationType,
                deviceId = deviceIdVal
            )

            // Check attempt cache
            val attempts = scannedAttempts[request.ticketId] ?: 0
            if (attempts >= 3) {
                // Already attempted too many times
                updateStatus(ScannerStatus.Error("Ticket already processed 3 times"))
                return
            }

            viewModelScope.launch(ioDispatcher) {
                validateTicketUseCase(request).collect { result ->
                    when (result) {
                        is Result.Init -> {
                            updateStatus(ScannerStatus.Validating)
                        }
                        is Result.Success<*> -> {
                            scannedAttempts[request.ticketId] = attempts + 1
                            updateStatus(ScannerStatus.Success)
                            updateScreenState(ScreenState.SuccessResult)
                        }
                        is Result.ServerError -> {
                            scannedAttempts[request.ticketId] = attempts + 1
                            updateStatus(ScannerStatus.Error(result.message))
                            updateScreenState(ScreenState.FailureResult)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            Log.e("QrScannerViewModel", "Error parsing QR data: ${e.message}", e)
            updateStatus(ScannerStatus.Error("Invalid QR data"))
            updateScreenState(ScreenState.FailureResult)
        }
    }

    private fun clearStatus() {
        updateStatus(ScannerStatus.Waiting)
    }

    private fun changeValidationType(type: ValidationType) {
        viewModelState.update { it.copy(selectedValidationType = type) }
    }

    private fun scanMore() {
        updateStatus(ScannerStatus.Waiting)
        updateScreenState(ScreenState.Scanner)
    }

    private fun showScannerScreen() {
        updateScreenState(ScreenState.Scanner)
    }

    private fun updateStatus(status: ScannerStatus) {
        viewModelState.update { it.copy(status = status) }
    }

    private fun updateScreenState(screenState: ScreenState) {
        viewModelState.update { it.copy(currentScreen = screenState) }
    }

    data class QrScannerViewModelState(
        val status: ScannerStatus = ScannerStatus.Waiting,
        val selectedValidationType: ValidationType = ValidationType.ENTRY,
        val currentScreen: ScreenState = ScreenState.Scanner,
        val error: String? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = QrScannerViewState(
            status = status,
            selectedValidationType = selectedValidationType,
            currentScreen = currentScreen
        )
    }

    data class QrScannerViewState(
        val status: ScannerStatus,
        val selectedValidationType: ValidationType,
        val currentScreen: ScreenState
    ) : ViewState()

    sealed class QrScannerViewEvent : ViewEvent {
        data class QrDetected(val raw: String) : QrScannerViewEvent()
        object ClearStatus : QrScannerViewEvent()
        data class ChangeValidationType(val type: ValidationType) : QrScannerViewEvent()
        object ScanMore : QrScannerViewEvent()
        object ShowScannerScreen : QrScannerViewEvent()
    }

    sealed class ScannerStatus {
        object Waiting : ScannerStatus()
        object Validating : ScannerStatus()
        object Success : ScannerStatus()
        data class Error(val message: String) : ScannerStatus()

        fun isBusy() = this is Validating
    }

    sealed class ScreenState {
        object Scanner : ScreenState()
        object SuccessResult : ScreenState()
        object FailureResult : ScreenState()
    }
} 
