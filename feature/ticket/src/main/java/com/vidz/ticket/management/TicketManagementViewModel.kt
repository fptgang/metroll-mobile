package com.vidz.ticket.management

import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.Order
import com.vidz.domain.usecase.order.GetMyOrdersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TicketManagementViewModel @Inject constructor(
    private val getMyOrdersUseCase: GetMyOrdersUseCase
) : BaseViewModel<TicketManagementViewModel.TicketManagementEvent, TicketManagementViewModel.TicketManagementUiState, TicketManagementViewModel.TicketManagementViewModelState>(
    initState = TicketManagementViewModelState()
) {

    init {
        loadMyOrders()
    }

    override fun onTriggerEvent(event: TicketManagementEvent) {
        when (event) {
            is TicketManagementEvent.LoadMyOrders -> loadMyOrders(event.page, event.size)
            is TicketManagementEvent.RefreshOrders -> refreshOrders()
            is TicketManagementEvent.SearchOrders -> searchOrders(event.query)
            is TicketManagementEvent.ClearError -> clearError()
            is TicketManagementEvent.LoadMoreOrders -> loadMoreOrders()
        }
    }

    private fun loadMyOrders(page: Int = 0, size: Int = 20) {
        viewModelScope.launch {
            getMyOrdersUseCase(page = page, size = size, search = viewModelState.value.searchQuery)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            updateState { copy(isLoading = true, error = null) }
                        }
                        is Result.Success -> {
                            val currentOrders = if (page == 0) emptyList() else viewModelState.value.orders
                            updateState { 
                                copy(
                                    isLoading = false,
                                    orders = currentOrders + result.data.content,
                                    currentPage = page,
                                    hasNextPage = !result.data.last,
                                    error = null
                                )
                            }
                        }
                        is Result.ServerError -> {
                            updateState { 
                                copy(
                                    isLoading = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun refreshOrders() {
        updateState { 
            copy(
                orders = emptyList(),
                currentPage = 0,
                hasNextPage = true,
                isRefreshing = true
            )
        }
        loadMyOrders()
        updateState { copy(isRefreshing = false) }
    }

    private fun searchOrders(query: String) {
        updateState { 
            copy(
                searchQuery = query,
                orders = emptyList(),
                currentPage = 0,
                hasNextPage = true
            )
        }
        loadMyOrders()
    }

    private fun loadMoreOrders() {
        if (viewModelState.value.hasNextPage && !viewModelState.value.isLoading) {
            loadMyOrders(page = viewModelState.value.currentPage + 1)
        }
    }

    private fun clearError() {
        updateState { copy(error = null) }
    }

    private fun updateState(update: TicketManagementViewModelState.() -> TicketManagementViewModelState) {
        viewModelState.value = viewModelState.value.update()
    }

    sealed interface TicketManagementEvent : ViewEvent {
        data class LoadMyOrders(val page: Int = 0, val size: Int = 20) : TicketManagementEvent
        object RefreshOrders : TicketManagementEvent
        data class SearchOrders(val query: String) : TicketManagementEvent
        object ClearError : TicketManagementEvent
        object LoadMoreOrders : TicketManagementEvent
    }

    data class TicketManagementViewModelState(
        val isLoading: Boolean = false,
        val isRefreshing: Boolean = false,
        val orders: List<Order> = emptyList(),
        val searchQuery: String = "",
        val currentPage: Int = 0,
        val hasNextPage: Boolean = true,
        val error: String? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = TicketManagementUiState(
            isLoading = isLoading,
            isRefreshing = isRefreshing,
            orders = orders,
            searchQuery = searchQuery,
            currentPage = currentPage,
            hasNextPage = hasNextPage,
            error = error
        )
    }

    data class TicketManagementUiState(
        val isLoading: Boolean,
        val isRefreshing: Boolean,
        val orders: List<Order>,
        val searchQuery: String,
        val currentPage: Int,
        val hasNextPage: Boolean,
        val error: String?
    ) : ViewState()
} 