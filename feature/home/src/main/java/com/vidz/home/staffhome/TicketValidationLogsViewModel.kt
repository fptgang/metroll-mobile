package com.vidz.home.staffhome

import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.Account
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.TicketValidation
import com.vidz.domain.model.ValidationType
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
                    loadTicketValidations(newStationId, resetPagination = true)
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

    private fun loadTicketValidations(
        stationCode: String, 
        resetPagination: Boolean = false,
        loadMore: Boolean = false
    ) {
        android.util.Log.d("TicketValidationLogsVM", "loadTicketValidations called with stationCode: $stationCode")
        
        viewModelScope.launch {
            val currentState = viewModelState.value
            
            // Reset pagination if needed
            val currentPage = if (resetPagination) 0 else currentState.currentPage
            val nextPage = if (loadMore) currentPage + 1 else currentPage
            
            // Don't load if already loading or if we've reached the end
            if (currentState.isLoading || (loadMore && !currentState.hasMorePages)) {
                return@launch
            }
            
            // Set loading state
            viewModelState.value = viewModelState.value.copy(
                isLoading = true,
                error = null,
                snackbarMessage = if (loadMore) "Loading more logs..." else "Loading validation logs for station: $stationCode"
            )
            
            try {
                android.util.Log.d("TicketValidationLogsVM", "About to call use case with page: $nextPage")
                
                getTicketValidationsByStationIdUseCase(
                    stationCode = stationCode,
                    page = nextPage,
                    size = currentState.pageSize,
                    search = currentState.searchQuery.takeIf { it.isNotBlank() },
                    validationType = currentState.selectedValidationType?.name,
                    startDate = currentState.startDate,
                    endDate = currentState.endDate
                ).collect { result ->
                    android.util.Log.d("TicketValidationLogsVM", "Received result from use case: $result")
                    when (result) {
                        is Result.Init -> {
                            android.util.Log.d("TicketValidationLogsVM", "Result.Init received")
                            // Keep current loading state
                        }
                        is Result.Success -> {
                            android.util.Log.d("TicketValidationLogsVM", "Result.Success received with ${result.data.content.size} items")
                            
                            val newItems = result.data.content.sortedByDescending { it.validationTime }
                            val allItems = if (loadMore) {
                                currentState.ticketValidations + newItems
                            } else {
                                newItems
                            }
                            
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                ticketValidations = allItems,
                                currentPage = nextPage,
                                totalPages = result.data.totalPages,
                                totalElements = result.data.totalElements,
                                hasMorePages = nextPage < result.data.totalPages - 1,
                                error = null,
                                snackbarMessage = if (loadMore) {
                                    "Loaded ${newItems.size} more logs"
                                } else {
                                    "Loaded ${result.data.content.size} validation logs"
                                }
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
        val stationCode = viewModelState.value.assignedStationId
        if (stationCode.isNotBlank()) {
            loadTicketValidations(stationCode, resetPagination = true)
        }
    }

    private fun loadMoreLogs() {
        val stationCode = viewModelState.value.assignedStationId
        if (stationCode.isNotBlank()) {
            loadTicketValidations(stationCode, loadMore = true)
        }
    }

    private fun applyFilters() {
        val stationCode = viewModelState.value.assignedStationId
        if (stationCode.isNotBlank()) {
            loadTicketValidations(stationCode, resetPagination = true)
        }
    }

    private fun clearFilters() {
        viewModelState.value = viewModelState.value.copy(
            searchQuery = "",
            selectedValidationType = null,
            startDate = null,
            endDate = null
        )
        applyFilters()
    }

    override fun onTriggerEvent(event: TicketValidationLogsEvent) {
        when (event) {
            is TicketValidationLogsEvent.RefreshLogs -> {
                refreshLogs()
            }
            is TicketValidationLogsEvent.LoadMoreLogs -> {
                loadMoreLogs()
            }
            is TicketValidationLogsEvent.DismissSnackbar -> {
                viewModelState.value = viewModelState.value.copy(
                    snackbarMessage = null
                )
            }
            is TicketValidationLogsEvent.ForceLoadWithTestStation -> {
                // For testing purposes - force load with a test station ID
                android.util.Log.d("TicketValidationLogsVM", "Force loading with test station ID")
                loadTicketValidations(viewModelState.value.localAccount?.assignedStation ?: "", resetPagination = true)
            }
            is TicketValidationLogsEvent.UpdateSearchQuery -> {
                viewModelState.value = viewModelState.value.copy(
                    searchQuery = event.query
                )
            }
            is TicketValidationLogsEvent.ApplySearch -> {
                applyFilters()
            }
            is TicketValidationLogsEvent.SelectValidationType -> {
                viewModelState.value = viewModelState.value.copy(
                    selectedValidationType = event.validationType
                )
                applyFilters()
            }
            is TicketValidationLogsEvent.UpdateDateRange -> {
                viewModelState.value = viewModelState.value.copy(
                    startDate = event.startDate,
                    endDate = event.endDate
                )
                applyFilters()
            }
            is TicketValidationLogsEvent.ClearFilters -> {
                clearFilters()
            }
            is TicketValidationLogsEvent.ToggleFilters -> {
                viewModelState.value = viewModelState.value.copy(
                    showFilters = !viewModelState.value.showFilters
                )
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
        val snackbarMessage: String? = null,
        // Pagination
        val currentPage: Int = 0,
        val pageSize: Int = 20,
        val totalPages: Int = 0,
        val totalElements: Long = 0,
        val hasMorePages: Boolean = false,
        // Filtering
        val searchQuery: String = "",
        val selectedValidationType: ValidationType? = null,
        val startDate: String? = null,
        val endDate: String? = null,
        val showFilters: Boolean = false
    ) : ViewModelState() {
        override fun toUiState(): ViewState = TicketValidationLogsViewState(
            localAccount = localAccount,
            staffName = staffName,
            assignedStationId = assignedStationId,
            ticketValidations = ticketValidations,
            isLoading = isLoading,
            error = error,
            snackbarMessage = snackbarMessage,
            currentPage = currentPage,
            pageSize = pageSize,
            totalPages = totalPages,
            totalElements = totalElements,
            hasMorePages = hasMorePages,
            searchQuery = searchQuery,
            selectedValidationType = selectedValidationType,
            startDate = startDate,
            endDate = endDate,
            showFilters = showFilters
        )
    }

    data class TicketValidationLogsViewState(
        val localAccount: Account?,
        val staffName: String,
        val assignedStationId: String,
        val ticketValidations: List<TicketValidation>,
        val isLoading: Boolean,
        val error: String?,
        val snackbarMessage: String?,
        // Pagination
        val currentPage: Int,
        val pageSize: Int,
        val totalPages: Int,
        val totalElements: Long,
        val hasMorePages: Boolean,
        // Filtering
        val searchQuery: String,
        val selectedValidationType: ValidationType?,
        val startDate: String?,
        val endDate: String?,
        val showFilters: Boolean
    ) : ViewState()

    sealed class TicketValidationLogsEvent : ViewEvent {
        object RefreshLogs : TicketValidationLogsEvent()
        object LoadMoreLogs : TicketValidationLogsEvent()
        object DismissSnackbar : TicketValidationLogsEvent()
        object ForceLoadWithTestStation : TicketValidationLogsEvent()
        
        // Search and Filter Events
        data class UpdateSearchQuery(val query: String) : TicketValidationLogsEvent()
        object ApplySearch : TicketValidationLogsEvent()
        data class SelectValidationType(val validationType: ValidationType?) : TicketValidationLogsEvent()
        data class UpdateDateRange(val startDate: String?, val endDate: String?) : TicketValidationLogsEvent()
        object ClearFilters : TicketValidationLogsEvent()
        object ToggleFilters : TicketValidationLogsEvent()
    }
} 