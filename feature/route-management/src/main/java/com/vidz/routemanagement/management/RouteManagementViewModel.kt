package com.vidz.routemanagement.management

import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.MetroLine
import com.vidz.routemanagement.domain.usecase.GetMetroLinesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RouteManagementViewModel @Inject constructor(
    private val getMetroLinesUseCase: GetMetroLinesUseCase,
    private val ioDispatcher: CoroutineDispatcher
) : BaseViewModel<RouteManagementViewModel.RouteManagementViewEvent,
        RouteManagementViewModel.RouteManagementViewState,
        RouteManagementViewModel.RouteManagementViewModelState>(
    initState = RouteManagementViewModelState()
) {

    init {
        loadMetroLines()
    }

    override fun onTriggerEvent(event: RouteManagementViewEvent) {
        when (event) {
            is RouteManagementViewEvent.LoadMetroLines -> loadMetroLines()
            is RouteManagementViewEvent.SelectMetroLine -> selectMetroLine(event.metroLine)
            is RouteManagementViewEvent.RefreshData -> loadMetroLines()
        }
    }

    private fun loadMetroLines() {
        viewModelScope.launch(ioDispatcher) {
            viewModelState.value = viewModelState.value.copy(isLoading = true, error = null)
            
            getMetroLinesUseCase()
                .catch { exception ->
                    viewModelState.value = viewModelState.value.copy(
                        isLoading = false,
                        error = exception.message ?: "Unknown error occurred"
                    )
                }
                .collect { result ->
                    when (result) {
                        is Result.Init -> {
                            viewModelState.value = viewModelState.value.copy(isLoading = true)
                        }
                        is Result.Success -> {
                            val metroLines = result.data.content
                            val selectedLine = metroLines.firstOrNull() ?: viewModelState.value.selectedMetroLine
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                metroLines = metroLines,
                                selectedMetroLine = selectedLine,
                                error = null
                            )
                        }
                        is Result.ServerError -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                error = result.message
                            )
                        }
                    }
                }
        }
    }

    private fun selectMetroLine(metroLine: MetroLine) {
        viewModelState.value = viewModelState.value.copy(selectedMetroLine = metroLine)
    }

    data class RouteManagementViewModelState(
        val isLoading: Boolean = false,
        val error: String? = null,
        val metroLines: List<MetroLine> = emptyList(),
        val selectedMetroLine: MetroLine? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = RouteManagementViewState(
            isLoading = isLoading,
            error = error,
            metroLines = metroLines,
            selectedMetroLine = selectedMetroLine
        )
    }

    data class RouteManagementViewState(
        val isLoading: Boolean,
        val error: String?,
        val metroLines: List<MetroLine>,
        val selectedMetroLine: MetroLine?
    ) : ViewState()

    sealed class RouteManagementViewEvent : ViewEvent {
        object LoadMetroLines : RouteManagementViewEvent()
        object RefreshData : RouteManagementViewEvent()
        data class SelectMetroLine(val metroLine: MetroLine) : RouteManagementViewEvent()
    }
} 
