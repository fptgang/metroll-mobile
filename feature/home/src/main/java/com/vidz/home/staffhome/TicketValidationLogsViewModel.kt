package com.vidz.home.staffhome

import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.Account
import com.vidz.domain.model.TicketValidation
import com.vidz.domain.usecase.account.ObserveLocalAccountInfoUseCase
import com.vidz.domain.usecase.ticketvalidation.GetTicketValidationsByStationIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketValidationLogsViewModel @Inject constructor(
    private val getTicketValidationsByStationIdUseCase: GetTicketValidationsByStationIdUseCase,
    private val observeLocalAccountInfoUseCase: ObserveLocalAccountInfoUseCase
) : BaseViewModel<TicketValidationLogsViewModel.TicketValidationLogsEvent, TicketValidationLogsViewModel.TicketValidationLogsViewState, TicketValidationLogsViewModel.TicketValidationLogsViewModelState>(
    initState = TicketValidationLogsViewModelState()
) {

    init {
        observeLocalAccountInfo()
    }

    private fun observeLocalAccountInfo() {
        viewModelScope.launch {
            observeLocalAccountInfoUseCase().collect { account ->
                android.util.Log.d("TicketValidationLogsVM", "Account received: $account")
                android.util.Log.d("TicketValidationLogsVM", "Account assignedStation: '${account?.assignedStation}'")
                
                val oldStationId = viewModelState.value.assignedStationId
                val newStationId = account?.assignedStation ?: ""
                
                android.util.Log.d("TicketValidationLogsVM", "Old stationId: '$oldStationId', New stationId: '$newStationId'")
                
                viewModelState.value = viewModelState.value.copy(
                    localAccount = account,
                    staffName = account?.fullName ?: "Staff Member",
                    assignedStationId = newStationId
                )
                
                // Load ticket validations if we have a station assigned and it changed
                if (newStationId.isNotBlank() && newStationId != oldStationId) {
                    android.util.Log.d("TicketValidationLogsVM", "Loading validations for station: '$newStationId'")
                    loadTicketValidations(newStationId)
                } else if (newStationId.isBlank()) {
                    android.util.Log.w("TicketValidationLogsVM", "No station assigned to account")
                    // Show message if no station is assigned
                    viewModelState.value = viewModelState.value.copy(
                        error = "No station assigned to your account",
                        snackbarMessage = "No station assigned to your account"
                    )
                }
            }
        }
    }

    private fun loadTicketValidations(stationId: String) {
        android.util.Log.d("TicketValidationLogsVM", "loadTicketValidations called with stationId: $stationId")
        
        viewModelScope.launch {
            // Set loading state
            viewModelState.value = viewModelState.value.copy(
                isLoading = true,
                error = null,
                snackbarMessage = "Loading validation logs for station: $stationId"
            )
            
            try {
                android.util.Log.d("TicketValidationLogsVM", "About to call use case")
                getTicketValidationsByStationIdUseCase(stationId).collect { result ->
                    android.util.Log.d("TicketValidationLogsVM", "Received result from use case: $result")
                    when (result) {
                        is Result.Init -> {
                            android.util.Log.d("TicketValidationLogsVM", "Result.Init received")
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = true
                            )
                        }
                        is Result.Success -> {
                            android.util.Log.d("TicketValidationLogsVM", "Result.Success received with ${result.data.size} items")
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                ticketValidations = result.data.sortedByDescending { it.validationTime },
                                error = null,
                                snackbarMessage = "Loaded ${result.data.size} validation logs"
                            )
                        }
                        is Result.ServerError -> {
                            android.util.Log.e("TicketValidationLogsVM", "Result.ServerError received: ${result.message}")
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                error = result.message,
                                snackbarMessage = "Error loading logs: ${result.message}"
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("TicketValidationLogsVM", "Exception in loadTicketValidations", e)
                viewModelState.value = viewModelState.value.copy(
                    isLoading = false,
                    error = "Failed to load validation logs: ${e.message}",
                    snackbarMessage = "Failed to load validation logs: ${e.message}"
                )
            }
        }
    }

    private fun refreshLogs() {
        val stationId = viewModelState.value.assignedStationId
        if (stationId.isNotBlank()) {
            loadTicketValidations(stationId)
        }
    }

    override fun onTriggerEvent(event: TicketValidationLogsEvent) {
        when (event) {
            is TicketValidationLogsEvent.RefreshLogs -> {
                refreshLogs()
            }
            is TicketValidationLogsEvent.DismissSnackbar -> {
                viewModelState.value = viewModelState.value.copy(
                    snackbarMessage = null
                )
            }
            is TicketValidationLogsEvent.ForceLoadWithTestStation -> {
                // For testing purposes - force load with a test station ID
                android.util.Log.d("TicketValidationLogsVM", "Force loading with test station ID")
                loadTicketValidations(viewModelState.value.localAccount?.assignedStation?:"") // Use a hardcoded test station ID
            }
        }
    }

    data class TicketValidationLogsViewModelState(
        val localAccount: Account? = null,
        val staffName: String = "Staff Member",
        val assignedStationId: String = "",
        val ticketValidations: List<TicketValidation> = emptyList(),
        val isLoading: Boolean = false,
        val error: String? = null,
        val snackbarMessage: String? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = TicketValidationLogsViewState(
            localAccount = localAccount,
            staffName = staffName,
            assignedStationId = assignedStationId,
            ticketValidations = ticketValidations,
            isLoading = isLoading,
            error = error,
            snackbarMessage = snackbarMessage
        )
    }

    data class TicketValidationLogsViewState(
        val localAccount: Account?,
        val staffName: String,
        val assignedStationId: String,
        val ticketValidations: List<TicketValidation>,
        val isLoading: Boolean,
        val error: String?,
        val snackbarMessage: String?
    ) : ViewState()

    sealed class TicketValidationLogsEvent : ViewEvent {
        object RefreshLogs : TicketValidationLogsEvent()
        object DismissSnackbar : TicketValidationLogsEvent()
        object ForceLoadWithTestStation : TicketValidationLogsEvent()
    }
} 