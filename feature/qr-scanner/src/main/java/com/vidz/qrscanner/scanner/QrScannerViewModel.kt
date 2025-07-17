package com.vidz.qrscanner.scanner

import android.util.Log
import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.Account
import com.vidz.domain.model.Station
import com.vidz.domain.model.TicketValidationCreateRequest
import com.vidz.domain.usecase.account.GetMeUseCase
import com.vidz.domain.usecase.station.GetStationByCodeUseCase
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
    private val getMeUseCase: GetMeUseCase,
    private val getStationByCodeUseCase: GetStationByCodeUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<QrScannerViewModel.QrScannerViewEvent,
        QrScannerViewModel.QrScannerViewState,
        QrScannerViewModel.QrScannerViewModelState>(
    initState = QrScannerViewModelState()
) {



    // JSON parser with unknown keys ignored
    private val jsonParser = Json { ignoreUnknownKeys = true }

    // Blocking mechanism to prevent duplicate processing of same ticket ID
    // Enhanced blocking mechanism
    @Volatile
    private var currentlyProcessingTicketId: String? = null
    @Volatile
    private var lastProcessedTicketId: String? = null
    @Volatile
    private var lastProcessedTime: Long = 0

    // Prevent rapid re-processing of same ticket
    private val MIN_REPROCESS_INTERVAL = 2000L // 2 seconds

    init {
        loadUserAccount()
    }

    private fun loadUserAccount() {
        viewModelScope.launch(ioDispatcher) {
            getMeUseCase().collect { result ->
                when (result) {
                    is Result.Success -> {
                        val account = result.data
                        viewModelState.update { it.copy(account = account) }

                        // Load station details if assigned station exists
                        if (account.assignedStation.isNotBlank()) {
                            loadStationDetails(account.assignedStation)
                        }
                    }

                    is Result.ServerError -> {
                        viewModelState.update { it.copy(error = result.message) }
                        updateStatus(ScannerStatus.Error("Could not load user account: ${result.message}"))
                    }

                    else -> {
                        // Init or other states
                    }
                }
            }
        }
    }

    private fun loadStationDetails(stationCode: String) {
        viewModelScope.launch(ioDispatcher) {
            getStationByCodeUseCase(stationCode).collect { result ->
                when (result) {
                    is Result.Success -> {
                        viewModelState.update { it.copy(assignedStationDetails = result.data) }
                    }
                    is Result.ServerError -> {
                        // Don't show error for station details, just log it
                        viewModelState.update { it.copy(stationError = result.message) }
                    }
                    else -> {
                        // Init or other states
                    }
                }
            }
        }
    }

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

        Log.d("QrScannerViewModel", "📱 QR DETECTED: Processing QR data...")
        Log.d("QrScannerViewModel", "🔍 Current processing lock: $currentlyProcessingTicketId")

        val account = viewModelState.value.account
        if (account == null) {
            updateStatus(ScannerStatus.Error("User account not loaded. Cannot validate ticket."))
            updateScreenState(ScreenState.FailureResult)
            return
        }

        if (account.assignedStation.isBlank()) {
            updateStatus(ScannerStatus.Error("No assigned station. Cannot validate ticket."))
            updateScreenState(ScreenState.FailureResult)
            return
        }

        try {
            val jsonElement = jsonParser.parseToJsonElement(raw)
            val obj = jsonElement.jsonObject

            val ticketId = obj["id"]?.jsonPrimitive?.content ?: obj["ticketNumber"]?.jsonPrimitive?.content ?: ""

            Log.d("QrScannerViewModel", "🎫 Extracted ticket ID: '$ticketId'")

            if (ticketId.isBlank()) {
                Log.e("QrScannerViewModel", "❌ Invalid QR: missing ticketId")
                updateStatus(ScannerStatus.Error("Invalid QR: missing ticketId"))
                updateScreenState(ScreenState.FailureResult)
                return
            }

            val currentTime = System.currentTimeMillis()

            // ENHANCED BLOCKING MECHANISM
            synchronized(this) {
                // Check if same ticket is currently being processed
                if (currentlyProcessingTicketId == ticketId) {
                    Log.d("QrScannerViewModel", "🚫 BLOCKED: Ticket $ticketId already being processed")
                    return
                }

                // Check if same ticket was recently processed
                if (lastProcessedTicketId == ticketId &&
                    (currentTime - lastProcessedTime) < MIN_REPROCESS_INTERVAL) {
                    Log.d("QrScannerViewModel", "🚫 BLOCKED: Ticket $ticketId was recently processed")
                    return
                }

                // Lock this ticket ID for processing
                currentlyProcessingTicketId = ticketId
                Log.d("QrScannerViewModel", "🔒 LOCKED: Now processing ticket: $ticketId")
            }

            // Use the selected validation type
            val domainValidationType = when (viewModelState.value.selectedValidationType) {
                ValidationType.ENTRY -> com.vidz.domain.model.ValidationType.ENTRY
                ValidationType.EXIT -> com.vidz.domain.model.ValidationType.EXIT
            }

            val request = TicketValidationCreateRequest(
                ticketId = ticketId,
                validationType = domainValidationType,
            )

            // Process the QR
            viewModelScope.launch(ioDispatcher) {
                try {
                    validateTicketUseCase(request).collect { result ->
                        Log.d("QrScannerViewModel", "📥 Flow result for $ticketId: $result")

                        // Double-check we still own the lock for this ticket ID
                        synchronized(this@QrScannerViewModel) {
                            if (currentlyProcessingTicketId != ticketId) {
                                Log.d("QrScannerViewModel", "🚫 FLOW CANCELLED: Lock lost for $ticketId")
                                return@collect
                            }
                        }

                        when (result) {
                            is Result.Init -> {
                                Log.d("QrScannerViewModel", "⏳ Starting validation for $ticketId")
                                updateStatus(ScannerStatus.Validating)
                            }

                            is Result.Success<*> -> {
                                Log.d("QrScannerViewModel", "✅ Success for $ticketId")
                                updateStatus(ScannerStatus.Success)
                                updateScreenState(ScreenState.SuccessResult)
                                clearProcessingLock(ticketId)
                            }

                            is Result.ServerError -> {
                                Log.d("QrScannerViewModel", "❌ Error for $ticketId: ${result.message}")
                                updateStatus(ScannerStatus.Error(result.message))
                                updateScreenState(ScreenState.FailureResult)
                                clearProcessingLock(ticketId)
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("QrScannerViewModel", "💥 Exception in flow for $ticketId", e)
                    updateStatus(ScannerStatus.Error("Validation failed: ${e.message}"))
                    updateScreenState(ScreenState.FailureResult)
                    clearProcessingLock(ticketId)
                }
            }
        } catch (e: Exception) {
            Log.e("QrScannerViewModel", "Error parsing QR data: ${e.message}", e)
            updateStatus(ScannerStatus.Error("Invalid QR data"))
            updateScreenState(ScreenState.FailureResult)
            clearProcessingLock(null)
        }
    }

    private fun clearProcessingLock(processedTicketId: String? = null) {
        synchronized(this) {
            val previousTicketId = currentlyProcessingTicketId
            currentlyProcessingTicketId = null

            // Track the last processed ticket for duplicate prevention
            if (processedTicketId != null) {
                lastProcessedTicketId = processedTicketId
                lastProcessedTime = System.currentTimeMillis()
            }

            Log.d("QrScannerViewModel", "🔓 UNLOCKED: Processing lock cleared for ticket: $previousTicketId")
        }
    }

    private fun scanMore() {
        Log.d("QrScannerViewModel", "🔄 SCAN MORE: User requested to scan more")
        clearProcessingLock() // Allow scanning new QRs
        // Reset the recent processing history to allow re-scanning same ticket if needed
        synchronized(this) {
            lastProcessedTicketId = null
            lastProcessedTime = 0
        }
        updateStatus(ScannerStatus.Waiting)
        updateScreenState(ScreenState.Scanner)
    }

    private fun showScannerScreen() {
        clearProcessingLock() // Ensure clean state when showing scanner
        updateScreenState(ScreenState.Scanner)
    }
    private fun clearStatus() {
        updateStatus(ScannerStatus.Waiting)
    }

    private fun changeValidationType(type: ValidationType) {
        viewModelState.update { it.copy(selectedValidationType = type) }
    }



    private fun clearProcessingLock() {
        synchronized(this) {
            val previousTicketId = currentlyProcessingTicketId
            currentlyProcessingTicketId = null
            Log.d("QrScannerViewModel", "🔓 UNLOCKED: Processing lock cleared for ticket: $previousTicketId")
        }
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
        val error: String? = null,
        val account: Account? = null,
        val assignedStationDetails: Station? = null,
        val stationError: String? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = QrScannerViewState(
            status = status,
            selectedValidationType = selectedValidationType,
            currentScreen = currentScreen,
            account = account,
            assignedStationDetails = assignedStationDetails
        )
    }

    data class QrScannerViewState(
        val status: ScannerStatus,
        val selectedValidationType: ValidationType,
        val currentScreen: ScreenState,
        val account: Account? = null,
        val assignedStationDetails: Station? = null
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

