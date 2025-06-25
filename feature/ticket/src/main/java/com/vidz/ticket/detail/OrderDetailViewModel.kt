package com.vidz.ticket.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.Order
import com.vidz.domain.model.OrderDetail
import com.vidz.domain.usecase.order.GetOrderByIdUseCase
import com.vidz.domain.usecase.ticket.GetQRByTicketIdUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderDetailViewModel @Inject constructor(
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val getQRByTicketIdUseCase: GetQRByTicketIdUseCase,
    savedStateHandle: SavedStateHandle
) : BaseViewModel<OrderDetailViewModel.OrderDetailEvent, OrderDetailViewModel.OrderDetailUiState, OrderDetailViewModel.OrderDetailViewModelState>(
    initState = OrderDetailViewModelState()
) {

    init {
        // Get order ID from navigation arguments
        savedStateHandle.get<String>("orderId")?.let { orderId ->
            loadOrder(orderId)
        }
    }

    override fun onTriggerEvent(event: OrderDetailEvent) {
        when (event) {
            is OrderDetailEvent.LoadOrder -> loadOrder(event.orderId)
            is OrderDetailEvent.LoadQRCode -> loadQRCode(event.ticketId)
            is OrderDetailEvent.CloseQRCode -> closeQRCode()
            is OrderDetailEvent.ClearError -> clearError()
        }
    }

    private fun loadOrder(orderId: String) {
        viewModelScope.launch {
            getOrderByIdUseCase(orderId)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            updateState { copy(isLoadingOrder = true, orderError = null) }
                        }
                        is Result.Success -> {
                            updateState {
                                copy(
                                    isLoadingOrder = false,
                                    order = result.data,
                                    orderError = null
                                )
                            }
                        }
                        is Result.ServerError -> {
                            updateState {
                                copy(
                                    isLoadingOrder = false,
                                    orderError = result.message
                                )
                            }
                        }
                    }
                }
                .launchIn(this)
        }
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
                                    showQRDialog = true,
                                    qrError = null
                                )
                            }
                        }
                        is Result.ServerError -> {
                            updateState {
                                copy(
                                    isLoadingQR = false,
                                    qrError = result.message,
                                    showQRDialog = false
                                )
                            }
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun closeQRCode() {
        updateState { 
            copy(
                showQRDialog = false,
                qrCodeData = null
            )
        }
    }

    private fun clearError() {
        updateState { copy(qrError = null, orderError = null) }
    }

    private fun updateState(update: OrderDetailViewModelState.() -> OrderDetailViewModelState) {
        viewModelState.value = viewModelState.value.update()
    }

    sealed interface OrderDetailEvent : ViewEvent {
        data class LoadOrder(val orderId: String) : OrderDetailEvent
        data class LoadQRCode(val ticketId: String) : OrderDetailEvent
        object CloseQRCode : OrderDetailEvent
        object ClearError : OrderDetailEvent
    }

    data class OrderDetailViewModelState(
        val order: Order? = null,
        val isLoadingOrder: Boolean = false,
        val orderError: String? = null,
        val isLoadingQR: Boolean = false,
        val qrCodeData: String? = null,
        val showQRDialog: Boolean = false,
        val qrError: String? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState = OrderDetailUiState(
            order = order,
            isLoadingOrder = isLoadingOrder,
            orderError = orderError,
            isLoadingQR = isLoadingQR,
            qrCodeData = qrCodeData,
            showQRDialog = showQRDialog,
            qrError = qrError
        )
    }

    data class OrderDetailUiState(
        val order: Order?,
        val isLoadingOrder: Boolean,
        val orderError: String?,
        val isLoadingQR: Boolean,
        val qrCodeData: String?,
        val showQRDialog: Boolean,
        val qrError: String?
    ) : ViewState()
} 