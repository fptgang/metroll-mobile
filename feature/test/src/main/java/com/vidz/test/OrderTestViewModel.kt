package com.vidz.test

import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.CheckoutItem
import com.vidz.domain.model.CheckoutRequest
import com.vidz.domain.model.TicketType
import com.vidz.domain.usecase.order.CheckoutUseCase
import com.vidz.domain.usecase.order.GetAllOrdersUseCase
import com.vidz.domain.usecase.order.GetMyOrdersUseCase
import com.vidz.domain.usecase.order.GetOrderByIdUseCase
import com.vidz.domain.usecase.payment.GetPaymentStatusUseCase
import com.vidz.domain.usecase.payment.HandlePaymentCancelUseCase
import com.vidz.domain.usecase.payment.HandlePaymentSuccessUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderTestViewModel @Inject constructor(
    private val checkoutUseCase: CheckoutUseCase,
    private val getAllOrdersUseCase: GetAllOrdersUseCase,
    private val getMyOrdersUseCase: GetMyOrdersUseCase,
    private val getOrderByIdUseCase: GetOrderByIdUseCase,
    private val getPaymentStatusUseCase: GetPaymentStatusUseCase,
    private val handlePaymentSuccessUseCase: HandlePaymentSuccessUseCase,
    private val handlePaymentCancelUseCase: HandlePaymentCancelUseCase
) : BaseViewModel<OrderTestViewModel.OrderTestEvent,
        OrderTestViewModel.OrderTestViewState,
        OrderTestViewModel.OrderTestViewModelState>(
    initState = OrderTestViewModelState()
) {

    override fun onTriggerEvent(event: OrderTestEvent) {
        when (event) {
            is OrderTestEvent.TestCheckout -> testCheckout()
            is OrderTestEvent.TestGetAllOrders -> testGetAllOrders()
            is OrderTestEvent.TestGetMyOrders -> testGetMyOrders()
            is OrderTestEvent.TestGetOrderById -> testGetOrderById(event.orderId)
            is OrderTestEvent.TestGetPaymentStatus -> testGetPaymentStatus(event.orderId)
            is OrderTestEvent.TestPaymentSuccess -> testPaymentSuccess(event.orderId)
            is OrderTestEvent.TestPaymentCancel -> testPaymentCancel(event.orderId)
            is OrderTestEvent.ClearResults -> clearResults()
            is OrderTestEvent.UpdateOrderId -> updateOrderId(event.orderId)
        }
    }

    private fun testCheckout() {
        viewModelScope.launch {
            val checkoutRequest = CheckoutRequest(
                items = listOf(
                    CheckoutItem(
                        ticketType = TicketType.P2P,
                        p2pJourneyId = "test-p2p-journey-1",
                        quantity = 2
                    )
                ),
                paymentMethod = "CREDIT_CARD",
                customerId = "test-customer-123"
            )

            checkoutUseCase(checkoutRequest)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = true,
                                result = "Loading checkout..."
                            )
                        }
                        is Result.Success -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                result = "✅ Checkout Success: Order ${result.data.id} created"
                            )
                        }
                        is Result.ServerError -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                result = "❌ Checkout Error: ${result.message}"
                            )
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun testGetAllOrders() {
        viewModelScope.launch {
            getAllOrdersUseCase(page = 0, size = 10)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = true,
                                result = "Loading all orders..."
                            )
                        }
                        is Result.Success -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                result = "✅ Get All Orders Success: Found ${result.data.content.size} orders"
                            )
                        }
                        is Result.ServerError -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                result = "❌ Get All Orders Error: ${result.message}"
                            )
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun testGetMyOrders() {
        viewModelScope.launch {
            getMyOrdersUseCase(page = 0, size = 10)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = true,
                                result = "Loading my orders..."
                            )
                        }
                        is Result.Success -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                result = "✅ Get My Orders Success: Found ${result.data.content.size} orders"
                            )
                        }
                        is Result.ServerError -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                result = "❌ Get My Orders Error: ${result.message}"
                            )
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun testGetOrderById(orderId: String) {
        viewModelScope.launch {
            getOrderByIdUseCase(orderId)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = true,
                                result = "Loading order by ID..."
                            )
                        }
                        is Result.Success -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                result = "✅ Get Order By ID Success: Order ${result.data.id} found"
                            )
                        }
                        is Result.ServerError -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                result = "❌ Get Order By ID Error: ${result.message}"
                            )
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun testGetPaymentStatus(orderId: String) {
        viewModelScope.launch {
            getPaymentStatusUseCase(orderId)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = true,
                                result = "Loading payment status..."
                            )
                        }
                        is Result.Success -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                result = "✅ Payment Status Success: ${result.data}"
                            )
                        }
                        is Result.ServerError -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                result = "❌ Payment Status Error: ${result.message}"
                            )
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun testPaymentSuccess(orderId: String) {
        viewModelScope.launch {
            handlePaymentSuccessUseCase(orderId)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = true,
                                result = "Processing payment success..."
                            )
                        }
                        is Result.Success -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                result = "✅ Payment Success: ${result.data}"
                            )
                        }
                        is Result.ServerError -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                result = "❌ Payment Success Error: ${result.message}"
                            )
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun testPaymentCancel(orderId: String) {
        viewModelScope.launch {
            handlePaymentCancelUseCase(orderId)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = true,
                                result = "Processing payment cancel..."
                            )
                        }
                        is Result.Success -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                result = "✅ Payment Cancel: ${result.data}"
                            )
                        }
                        is Result.ServerError -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoading = false,
                                result = "❌ Payment Cancel Error: ${result.message}"
                            )
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun clearResults() {
        viewModelState.value = viewModelState.value.copy(result = "")
    }

    private fun updateOrderId(orderId: String) {
        viewModelState.value = viewModelState.value.copy(testOrderId = orderId)
    }

    data class OrderTestViewModelState(
        val isLoading: Boolean = false,
        val testOrderId: String = "",
        val result: String = ""
    ) : ViewModelState() {
        override fun toUiState(): ViewState = OrderTestViewState(
            isLoading = isLoading,
            testOrderId = testOrderId,
            result = result
        )
    }

    data class OrderTestViewState(
        val isLoading: Boolean,
        val testOrderId: String,
        val result: String
    ) : ViewState()

    sealed class OrderTestEvent : ViewEvent {
        object TestCheckout : OrderTestEvent()
        object TestGetAllOrders : OrderTestEvent()
        object TestGetMyOrders : OrderTestEvent()
        data class TestGetOrderById(val orderId: String) : OrderTestEvent()
        data class TestGetPaymentStatus(val orderId: String) : OrderTestEvent()
        data class TestPaymentSuccess(val orderId: String) : OrderTestEvent()
        data class TestPaymentCancel(val orderId: String) : OrderTestEvent()
        object ClearResults : OrderTestEvent()
        data class UpdateOrderId(val orderId: String) : OrderTestEvent()
    }
} 