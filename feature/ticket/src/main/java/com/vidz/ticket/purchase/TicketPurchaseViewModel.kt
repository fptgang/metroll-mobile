package com.vidz.ticket.purchase

import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.CheckoutItem
import com.vidz.domain.model.CheckoutRequest
import com.vidz.domain.model.Order
import com.vidz.domain.model.P2PJourney
import com.vidz.domain.model.Station
import com.vidz.domain.model.TicketType
import com.vidz.domain.model.TimedTicketPlan
import com.vidz.domain.model.Voucher
import com.vidz.domain.model.VoucherStatus
import com.vidz.domain.usecase.account.GetMyDiscountPercentageUseCase
import com.vidz.domain.usecase.auth.GetCurrentUserUseCase
import com.vidz.domain.usecase.cart.AddToCartUseCase
import com.vidz.domain.usecase.cart.ClearCartUseCase
import com.vidz.domain.usecase.cart.GetCartItemsUseCase
import com.vidz.domain.usecase.cart.RemoveFromCartUseCase
import com.vidz.domain.usecase.cart.UpdateCartItemQuantityUseCase
import com.vidz.domain.usecase.order.CheckoutUseCase
import com.vidz.domain.usecase.p2pjourney.GetP2PJourneyByIdUseCase
import com.vidz.domain.usecase.p2pjourney.GetP2PJourneysUseCase
import com.vidz.domain.usecase.p2pjourney.GetP2PJourneyByStationsUseCase
import com.vidz.domain.usecase.station.GetStationsUseCase
import com.vidz.domain.usecase.timedticketplan.GetTimedTicketPlanByIdUseCase
import com.vidz.domain.usecase.timedticketplan.GetTimedTicketPlansUseCase
import com.vidz.domain.usecase.voucher.GetMyVouchersUseCase
import dagger.hilt.android.lifecycle.HiltViewModel

import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CartItem(
    val id: String,
    val ticketType: TicketType,
    val name: String,
    val price: Double,
    val quantity: Int = 1,
    val description: String? = null
)

enum class P2PSortType(val displayName: String) {
    PRICE_ASC("Price: Low to High"),
    PRICE_DESC("Price: High to Low"),
    DURATION_ASC("Duration: Shortest"),
    DURATION_DESC("Duration: Longest"),
    DISTANCE_ASC("Distance: Shortest"),
    DISTANCE_DESC("Distance: Longest"),
    DEFAULT("Default")
}

@HiltViewModel
class TicketPurchaseViewModel @Inject constructor(
    private val getTimedTicketPlansUseCase: GetTimedTicketPlansUseCase,
    private val getP2PJourneysUseCase: GetP2PJourneysUseCase,
    private val getP2PJourneyByStationsUseCase: GetP2PJourneyByStationsUseCase,
    private val getStationsUseCase: GetStationsUseCase,
    private val getTimedTicketPlanByIdUseCase: GetTimedTicketPlanByIdUseCase,
    private val getP2PJourneyByIdUseCase: GetP2PJourneyByIdUseCase,
    private val checkoutUseCase: CheckoutUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val clearCartUseCase: ClearCartUseCase,
    private val removeFromCartUseCase: RemoveFromCartUseCase,
    private val updateCartItemQuantityUseCase: UpdateCartItemQuantityUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val getMyVouchersUseCase: GetMyVouchersUseCase,
    private val getMyDiscountPercentageUseCase: GetMyDiscountPercentageUseCase
) : BaseViewModel<TicketPurchaseViewModel.TicketPurchaseEvent, TicketPurchaseViewModel.TicketPurchaseUiState, TicketPurchaseViewModel.TicketPurchaseViewModelState>(
    initState = TicketPurchaseViewModelState()
) {

    init {
        loadInitialData()
        observeCartItems()
    }

    override fun onTriggerEvent(event: TicketPurchaseEvent) {
        when (event) {
            is TicketPurchaseEvent.LoadTimedTickets -> loadTimedTickets()
            is TicketPurchaseEvent.LoadP2PJourneys -> loadP2PJourneys(event.search)
            is TicketPurchaseEvent.SelectTicketType -> selectTicketType(event.ticketType)
            is TicketPurchaseEvent.AddToCart -> addToCart(event.item)
            is TicketPurchaseEvent.RemoveFromCart -> removeFromCart(event.item)
            is TicketPurchaseEvent.UpdateCartItemQuantity -> updateCartItemQuantity(
                event.item,
                event.quantity
            )

            is TicketPurchaseEvent.ClearCart -> clearCart()
            is TicketPurchaseEvent.Checkout -> performCheckout()
            is TicketPurchaseEvent.SortP2PBy -> sortP2PJourneys(event.sortType)
            is TicketPurchaseEvent.RefreshData -> refreshData()
            is TicketPurchaseEvent.ClearError -> clearError()
            is TicketPurchaseEvent.ShowCartSheet -> showCartSheet(event.show)
            is TicketPurchaseEvent.ProcessPaymentUrl -> processPaymentUrl(event.url)
            is TicketPurchaseEvent.OpenPayment -> openPayment()
            is TicketPurchaseEvent.ClosePayment -> closePayment()
            is TicketPurchaseEvent.LoadStations -> loadStations()
            is TicketPurchaseEvent.SelectFromStation -> selectFromStation(event.station)
            is TicketPurchaseEvent.SelectToStation -> selectToStation(event.station)
            is TicketPurchaseEvent.SearchJourneyByStations -> searchJourneyByStations()
            is TicketPurchaseEvent.ClearStationSelection -> clearStationSelection()
            is TicketPurchaseEvent.LoadVouchers -> loadVouchers()
            is TicketPurchaseEvent.SelectVoucher -> selectVoucher(event.voucher)
            is TicketPurchaseEvent.ShowVoucherSheet -> showVoucherSheet(event.show)
        }
    }

    private fun loadInitialData() {
        loadTimedTickets()
        loadP2PJourneys() // Load all P2P journeys initially
        loadStations()
        loadVouchers()
        loadDiscountPercentage()
    }

    private fun observeCartItems() {
        viewModelScope.launch {
            getCartItemsUseCase()
                .onEach { checkoutItems ->
                    // Create placeholder cart items first
                    val placeholderCartItems = checkoutItems.map { checkoutItem ->
                        CartItem(
                            id = checkoutItem.p2pJourneyId ?: checkoutItem.timedTicketPlanId ?: "",
                            ticketType = checkoutItem.ticketType,
                            name = if (checkoutItem.ticketType == TicketType.TIMED) "Loading Timed Ticket..." else "Loading P2P Journey...",
                            price = 0.0,
                            quantity = checkoutItem.quantity,
                            description = "Loading details..."
                        )
                    }
                    updateState { copy(cartItems = placeholderCartItems) }

                    // Fetch detailed information for each item
                    fetchCartItemDetails(checkoutItems)
                    
                    // Re-evaluate best voucher when cart changes
                    val currentVouchers = viewModelState.value.vouchers
                    if (currentVouchers.isNotEmpty()) {
                        autoSelectBestVoucher(currentVouchers)
                    }
                }
                .launchIn(this)
        }
    }

    private fun fetchCartItemDetails(checkoutItems: List<CheckoutItem>) {
        checkoutItems.forEach { checkoutItem ->
            when (checkoutItem.ticketType) {
                TicketType.TIMED -> {
                    checkoutItem.timedTicketPlanId?.let { id ->
                        viewModelScope.launch {
                            getTimedTicketPlanByIdUseCase(id)
                                .onEach { result ->
                                    when (result) {
                                        is Result.Success -> {
                                            val timedTicket = result.data
                                            updateCartItemWithDetails(
                                                id = id,
                                                cartItem = CartItem(
                                                    id = id,
                                                    ticketType = TicketType.TIMED,
                                                    name = timedTicket.name,
                                                    price = timedTicket.basePrice,
                                                    quantity = checkoutItem.quantity,
                                                    description = "${timedTicket.validDuration} days validity"
                                                )
                                            )
                                        }
                                        is Result.ServerError -> {
                                            updateCartItemWithDetails(
                                                id = id,
                                                cartItem = CartItem(
                                                    id = id,
                                                    ticketType = TicketType.TIMED,
                                                    name = "Timed Ticket",
                                                    price = 0.0,
                                                    quantity = checkoutItem.quantity,
                                                    description = "Failed to load details"
                                                )
                                            )
                                        }
                                        else -> {} // Init state, continue loading
                                    }
                                }
                                .launchIn(this)
                        }
                    }
                }

                TicketType.P2P -> {
                    checkoutItem.p2pJourneyId?.let { id ->
                        viewModelScope.launch {
                            getP2PJourneyByIdUseCase(id)
                                .onEach { result ->
                                    when (result) {
                                        is Result.Success -> {
                                            val p2pJourney = result.data
                                            updateCartItemWithDetails(
                                                id = id,
                                                cartItem = CartItem(
                                                    id = id,
                                                    ticketType = TicketType.P2P,
                                                    name = "${p2pJourney.startStationId} → ${p2pJourney.endStationId}",
                                                    price = p2pJourney.basePrice,
                                                    quantity = checkoutItem.quantity,
                                                    description = "${p2pJourney.distance} km • ${p2pJourney.travelTime} min"
                                                )
                                            )
                                        }
                                        is Result.ServerError -> {
                                            updateCartItemWithDetails(
                                                id = id,
                                                cartItem = CartItem(
                                                    id = id,
                                                    ticketType = TicketType.P2P,
                                                    name = "P2P Journey",
                                                    price = 0.0,
                                                    quantity = checkoutItem.quantity,
                                                    description = "Failed to load details"
                                                )
                                            )
                                        }
                                        else -> {} // Init state, continue loading
                                    }
                                }
                                .launchIn(this)
                        }
                    }
                }
            }
        }
    }

    private fun updateCartItemWithDetails(id: String, cartItem: CartItem) {
        updateState {
            copy(
                cartItems = cartItems.map { existing ->
                    if (existing.id == id) cartItem else existing
                }
            )
        }
    }

    private fun loadTimedTickets() {
        viewModelScope.launch {
            getTimedTicketPlansUseCase(page = 0, size = 20)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            updateState { copy(isLoadingTimed = true) }
                        }

                        is Result.Success -> {
                            updateState {
                                copy(
                                    isLoadingTimed = false,
                                    timedTickets = result.data.content,
                                    error = null
                                )
                            }
                        }

                        is Result.ServerError -> {
                            updateState {
                                copy(
                                    isLoadingTimed = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun loadP2PJourneys(search: String? = null) {
        viewModelScope.launch {
            getP2PJourneysUseCase(page = 0, size = 50, search = search)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            updateState { copy(isLoadingP2P = true) }
                        }

                        is Result.Success -> {
                            val sortedJourneys = sortJourneysByType(
                                result.data.content,
                                viewModelState.value.p2pSortType
                            )
                            updateState {
                                copy(
                                    isLoadingP2P = false,
                                    p2pJourneys = sortedJourneys,
                                    error = null
                                )
                            }
                        }

                        is Result.ServerError -> {
                            updateState {
                                copy(
                                    isLoadingP2P = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun selectTicketType(ticketType: TicketType) {
        updateState { copy(selectedTicketType = ticketType) }
    }

    private fun addToCart(item: CartItem) {
        viewModelScope.launch {
            val checkoutItem = CheckoutItem(
                ticketType = item.ticketType,
                p2pJourneyId = if (item.ticketType == TicketType.P2P) item.id else null,
                timedTicketPlanId = if (item.ticketType == TicketType.TIMED) item.id else null,
                quantity = item.quantity
            )
            addToCartUseCase(checkoutItem)
        }
    }

    private fun removeFromCart(item: CartItem) {
        viewModelScope.launch {
            val checkoutItem = CheckoutItem(
                ticketType = item.ticketType,
                p2pJourneyId = if (item.ticketType == TicketType.P2P) item.id else null,
                timedTicketPlanId = if (item.ticketType == TicketType.TIMED) item.id else null,
                quantity = item.quantity
            )
            removeFromCartUseCase(checkoutItem)
        }
    }

    private fun updateCartItemQuantity(item: CartItem, quantity: Int) {
        viewModelScope.launch {
            val checkoutItem = CheckoutItem(
                ticketType = item.ticketType,
                p2pJourneyId = if (item.ticketType == TicketType.P2P) item.id else null,
                timedTicketPlanId = if (item.ticketType == TicketType.TIMED) item.id else null,
                quantity = quantity
            )
            updateCartItemQuantityUseCase(checkoutItem, quantity)
        }
    }

    private fun clearCart() {
        viewModelScope.launch {
            clearCartUseCase()
        }
    }

    private fun sortP2PJourneys(sortType: P2PSortType) {
        val sortedJourneys = sortJourneysByType(viewModelState.value.p2pJourneys, sortType)
        updateState {
            copy(
                p2pJourneys = sortedJourneys,
                p2pSortType = sortType
            )
        }
    }

    private fun sortJourneysByType(
        journeys: List<P2PJourney>,
        sortType: P2PSortType
    ): List<P2PJourney> {
        return when (sortType) {
            P2PSortType.PRICE_ASC -> journeys.sortedBy { it.basePrice }
            P2PSortType.PRICE_DESC -> journeys.sortedByDescending { it.basePrice }
            P2PSortType.DURATION_ASC -> journeys.sortedBy { it.travelTime }
            P2PSortType.DURATION_DESC -> journeys.sortedByDescending { it.travelTime }
            P2PSortType.DISTANCE_ASC -> journeys.sortedBy { it.distance }
            P2PSortType.DISTANCE_DESC -> journeys.sortedByDescending { it.distance }
            P2PSortType.DEFAULT -> journeys
        }
    }

    private fun performCheckout() {
        viewModelScope.launch {
            getCartItemsUseCase()
                .onEach { checkoutItems ->
                    if (checkoutItems.isEmpty()) return@onEach
                    getCurrentUserUseCase().collect { user ->
                        when (user) {
                            is Result.Success -> {
                                val checkoutRequest = CheckoutRequest(
                                    items = checkoutItems,
                                    paymentMethod = "CASH",
                                    voucherId = viewModelState.value.selectedVoucher?.id,
//                                    discountPackage = if (viewModelState.value.userDiscountPercentage != null && viewModelState.value.userDiscountPercentage!! > 0f) "ACTIVE" else null,
                                    customerId = user.data?.uid
                                )

                                checkoutUseCase(checkoutRequest)
                                    .onEach { result ->
                                        when (result) {
                                            is Result.Init -> {
                                                updateState { copy(isCheckingOut = true) }
                                            }

                                            is Result.Success -> {
                                                // Clear cart after successful checkout
                                                clearCartUseCase()

                                                // Only set paymentUrl if payment method is PAYOS and paymentUrl exists
                                                val shouldShowPaymentWebView = result.data.paymentMethod == "PAYOS" &&
                                                                              !result.data.paymentUrl.isNullOrEmpty()

                                                // Debug logging
                                                println("Checkout successful - Payment Method: ${result.data.paymentMethod}, PaymentUrl: ${result.data.paymentUrl}, ShouldShow: $shouldShowPaymentWebView")

                                                updateState {
                                                    copy(
                                                        isCheckingOut = false,
                                                        checkoutResult = result.data,
                                                        error = null,
                                                        paymentUrl = if (shouldShowPaymentWebView) result.data.paymentUrl else null,
                                                        showPaymentWebView = shouldShowPaymentWebView
                                                    )
                                                }
                                            }

                                            is Result.ServerError -> {
                                                updateState {
                                                    copy(
                                                        isCheckingOut = false,
                                                        error = result.message
                                                    )
                                                }
                                            }
                                        }
                                    }
                                    .launchIn(this)
                            }

                            else -> null // Handle error case if needed
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun refreshData() {
        updateState { copy(error = null) }
        loadInitialData()
    }

    private fun clearError() {
        updateState { copy(error = null) }
    }

    private fun showCartSheet(show: Boolean) {
        updateState { copy(showCartSheet = show) }
    }

    private fun processPaymentUrl(url: String) {
        updateState { copy(paymentUrl = url) }
    }

    private fun openPayment() {
        updateState { copy(showPaymentWebView = true) }
    }

    private fun closePayment() {
        updateState { copy(showPaymentWebView = false, paymentUrl = null) }
    }

    private fun loadStations() {
        viewModelScope.launch {
            getStationsUseCase(page = 0, size = 100)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            updateState { copy(isLoadingStations = true) }
                        }
                        is Result.Success -> {
                            println("ViewModel: Stations loaded successfully - ${result.data.content.size} stations")
                            updateState {
                                copy(
                                    isLoadingStations = false,
                                    stations = result.data.content,
                                    error = null
                                )
                            }
                        }
                        is Result.ServerError -> {
                            println( "ViewModel: Error loading stations - ${result.message}")
                            updateState {
                                copy(
                                    isLoadingStations = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun selectFromStation(station: Station?) {
        println("ViewModel: selectFromStation called with: ${station?.name} (${station?.code})")
        updateState {
            copy(
                selectedFromStation = station,
                selectedToStation = if (selectedToStation?.id == station?.id) null else selectedToStation
            )
        }

        // Always try to search when any station is selected
        if (station != null) {
            println("ViewModel: From station selected, triggering searchJourneyByStations")
            searchJourneyByStations()
        } else {
            println("ViewModel: From station cleared, loading all P2P journeys")
            loadP2PJourneys()
        }
    }

    private fun selectToStation(station: Station?) {
        println("ViewModel: selectToStation called with: ${station?.name} (${station?.code})")
        updateState {
            copy(
                selectedToStation = station,
                selectedFromStation = if (selectedFromStation?.id == station?.id) null else selectedFromStation
            )
        }

        // Always try to search when any station is selected
        if (station != null) {
            println("ViewModel: To station selected, triggering searchJourneyByStations")
            searchJourneyByStations()
        } else {
            println("ViewModel: To station cleared, loading all P2P journeys")
            loadP2PJourneys()
        }
    }

    private fun searchJourneyByStations() {
        val fromStation = viewModelState.value.selectedFromStation
        val toStation = viewModelState.value.selectedToStation
        
        println("ViewModel: searchJourneyByStations called. From: ${fromStation?.name} (${fromStation?.code}), To: ${toStation?.name} (${toStation?.code})")

        // If stations are selected, search by stations using codes
        if (fromStation != null || toStation != null) {
            val fromCode = fromStation?.code.orEmpty()
            val toCode = toStation?.code.orEmpty()
            println("ViewModel: Searching P2P journeys with fromCode='$fromCode', toCode='$toCode'")
            
            viewModelScope.launch {
                getP2PJourneyByStationsUseCase(page = 0, size = 50, fromCode, toCode)
                    .onEach { result ->
                        when (result) {
                            is Result.Init -> {
                                println("ViewModel: P2P journey search init state")
                                updateState { copy(isLoadingP2P = true) }
                            }
                            is Result.Success -> {
                                println("ViewModel: P2P journey search success - ${result.data.content.size} journeys found")
                                val sortedJourneys = sortJourneysByType(
                                    result.data.content,
                                    viewModelState.value.p2pSortType
                                )
                                updateState {
                                    copy(
                                        isLoadingP2P = false,
                                        p2pJourneys = sortedJourneys,
                                        error = null
                                    )
                                }
                            }
                            is Result.ServerError -> {
                                println("ViewModel: P2P journey search error - ${result.message}")
                                updateState {
                                    copy(
                                        isLoadingP2P = false,
                                        p2pJourneys = emptyList(),
                                        error = result.message
                                    )
                                }
                            }
                        }
                    }
                    .launchIn(this)
            }
        } else {
            println("ViewModel: No stations selected, loading all P2P journeys")
            loadP2PJourneys()
        }
    }

    private fun clearStationSelection() {
        updateState {
            copy(
                selectedFromStation = null,
                selectedToStation = null
            )
        }
        // Reload all P2P journeys when clearing stations
        loadP2PJourneys()
    }

    private fun loadVouchers() {
        viewModelScope.launch {
            getMyVouchersUseCase()
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            updateState { copy(isLoadingVouchers = true) }
                        }
                        is Result.Success -> {
                            val vouchers = result.data
                            updateState {
                                copy(
                                    isLoadingVouchers = false,
                                    vouchers = vouchers,
                                    error = null
                                )
                            }
                            // Auto-select the best voucher
                            autoSelectBestVoucher(vouchers)
                        }
                        is Result.ServerError -> {
                            updateState {
                                copy(
                                    isLoadingVouchers = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun autoSelectBestVoucher(vouchers: List<Voucher>) {
        val currentSubtotal = viewModelState.value.cartItems.sumOf { it.price * it.quantity }
        
        // Select the first usable voucher (highest discount among usable ones)
        val bestVoucher = vouchers.firstOrNull { voucher ->
            voucher.status == VoucherStatus.VALID && currentSubtotal >= voucher.minTransactionAmount
        }?.let { voucher ->
            // Among usable vouchers, select the one with highest discount
            vouchers.filter { v ->
                v.status == VoucherStatus.VALID && currentSubtotal >= v.minTransactionAmount
            }.maxByOrNull { it.discountAmount }
        }
        
        updateState { copy(selectedVoucher = bestVoucher) }
    }

    private fun selectVoucher(voucher: Voucher?) {
        updateState { copy(selectedVoucher = voucher) }
    }

    private fun showVoucherSheet(show: Boolean) {
        updateState { copy(showVoucherSheet = show) }
    }

    private fun loadDiscountPercentage() {
        viewModelScope.launch {
            getMyDiscountPercentageUseCase()
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            updateState { copy(isLoadingDiscountPercentage = true) }
                        }
                        is Result.Success -> {
                            updateState {
                                copy(
                                    isLoadingDiscountPercentage = false,
                                    userDiscountPercentage = result.data,
                                    error = null
                                )
                            }
                        }
                        is Result.ServerError -> {
                            updateState {
                                copy(
                                    isLoadingDiscountPercentage = false,
                                    error = result.message
                                )
                            }
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun updateState(update: TicketPurchaseViewModelState.() -> TicketPurchaseViewModelState) {
        viewModelState.value = viewModelState.value.update()
    }

    sealed interface TicketPurchaseEvent : ViewEvent {
        object LoadTimedTickets : TicketPurchaseEvent
        data class LoadP2PJourneys(val search: String? = null) : TicketPurchaseEvent
        data class SelectTicketType(val ticketType: TicketType) : TicketPurchaseEvent
        data class AddToCart(val item: CartItem) : TicketPurchaseEvent
        data class RemoveFromCart(val item: CartItem) : TicketPurchaseEvent
        data class UpdateCartItemQuantity(val item: CartItem, val quantity: Int) :
            TicketPurchaseEvent

        object ClearCart : TicketPurchaseEvent
        object Checkout : TicketPurchaseEvent
        data class SortP2PBy(val sortType: P2PSortType) : TicketPurchaseEvent
        object RefreshData : TicketPurchaseEvent
        object ClearError : TicketPurchaseEvent
        data class ShowCartSheet(val show: Boolean) : TicketPurchaseEvent
        data class ProcessPaymentUrl(val url: String) : TicketPurchaseEvent
        object OpenPayment : TicketPurchaseEvent
        object ClosePayment : TicketPurchaseEvent
        object LoadStations : TicketPurchaseEvent
        data class SelectFromStation(val station: Station?) : TicketPurchaseEvent
        data class SelectToStation(val station: Station?) : TicketPurchaseEvent
        object SearchJourneyByStations : TicketPurchaseEvent
        object ClearStationSelection : TicketPurchaseEvent
        object LoadVouchers : TicketPurchaseEvent
        data class SelectVoucher(val voucher: Voucher?) : TicketPurchaseEvent
        data class ShowVoucherSheet(val show: Boolean) : TicketPurchaseEvent
    }

    data class TicketPurchaseViewModelState(
        val isLoadingTimed: Boolean = false,
        val isLoadingP2P: Boolean = false,
        val isCheckingOut: Boolean = false,
        val selectedTicketType: TicketType = TicketType.TIMED,
        val timedTickets: List<TimedTicketPlan> = emptyList(),
        val p2pJourneys: List<P2PJourney> = emptyList(),
        val cartItems: List<CartItem> = emptyList(),
        val checkoutResult: Order? = null,
        val error: String? = null,
        val p2pSortType: P2PSortType = P2PSortType.DEFAULT,
        val showCartSheet: Boolean = false,
        val paymentUrl: String? = null,
        val showPaymentWebView: Boolean = false,
        val isLoadingStations: Boolean = false,
        val stations: List<Station> = emptyList(),
        val selectedFromStation: Station? = null,
        val selectedToStation: Station? = null,
        val isLoadingVouchers: Boolean = false,
        val vouchers: List<Voucher> = emptyList(),
        val selectedVoucher: Voucher? = null,
        val showVoucherSheet: Boolean = false,
        val isLoadingDiscountPercentage: Boolean = false,
        val userDiscountPercentage: Float? = null
    ) : ViewModelState() {
        override fun toUiState(): ViewState {
            val subtotal = cartItems.sumOf { it.price * it.quantity }
            
            // Calculate voucher discount
            val voucherDiscount = selectedVoucher?.let { voucher ->
                if (voucher.status == VoucherStatus.VALID && subtotal >= voucher.minTransactionAmount) {
                    voucher.discountAmount
                } else {
                    0.0
                }
            } ?: 0.0
            
            // Calculate discount package discount
            val discountPackageDiscount = userDiscountPercentage?.let { percentage ->
                if (percentage > 0f && percentage < 1f) {
                    subtotal * percentage
                } else {
                    0.0
                }
            } ?: 0.0
            
            // Apply both discounts (voucher first, then discount package)
            val totalAfterVoucher = subtotal - voucherDiscount
            val total = totalAfterVoucher - discountPackageDiscount
            
            val sortedVouchers = vouchers.sortedWith(compareByDescending<Voucher> { voucher ->
                voucher.status == VoucherStatus.VALID && subtotal >= voucher.minTransactionAmount
            }.thenByDescending { it.discountAmount })
            
            return TicketPurchaseUiState(
                isLoadingTimed = isLoadingTimed,
                isLoadingP2P = isLoadingP2P,
                isCheckingOut = isCheckingOut,
                selectedTicketType = selectedTicketType,
                timedTickets = timedTickets,
                p2pJourneys = p2pJourneys,
                cartItems = cartItems,
                checkoutResult = checkoutResult,
                error = error,
                p2pSortType = p2pSortType,
                showCartSheet = showCartSheet,
                paymentUrl = paymentUrl,
                showPaymentWebView = showPaymentWebView,
                isLoadingStations = isLoadingStations,
                stations = stations,
                selectedFromStation = selectedFromStation,
                selectedToStation = selectedToStation,
                isLoadingVouchers = isLoadingVouchers,
                vouchers = sortedVouchers,
                selectedVoucher = selectedVoucher,
                showVoucherSheet = showVoucherSheet,
                isLoadingDiscountPercentage = isLoadingDiscountPercentage,
                userDiscountPercentage = userDiscountPercentage,
                subtotal = subtotal,
                total = total,
                voucherDiscount = voucherDiscount,
                discountPackageDiscount = discountPackageDiscount,
                cartItemCount = cartItems.sumOf { it.quantity }
            )
        }
    }

    data class TicketPurchaseUiState(
        val isLoadingTimed: Boolean,
        val isLoadingP2P: Boolean,
        val isCheckingOut: Boolean,
        val selectedTicketType: TicketType,
        val timedTickets: List<TimedTicketPlan>,
        val p2pJourneys: List<P2PJourney>,
        val cartItems: List<CartItem>,
        val checkoutResult: Order?,
        val error: String?,
        val p2pSortType: P2PSortType,
        val showCartSheet: Boolean,
        val paymentUrl: String?,
        val showPaymentWebView: Boolean,
        val isLoadingStations: Boolean,
        val stations: List<Station>,
        val selectedFromStation: Station?,
        val selectedToStation: Station?,
        val isLoadingVouchers: Boolean,
        val vouchers: List<Voucher>,
        val selectedVoucher: Voucher?,
        val showVoucherSheet: Boolean,
        val isLoadingDiscountPercentage: Boolean,
        val userDiscountPercentage: Float?,
        val subtotal: Double,
        val total: Double,
        val voucherDiscount: Double,
        val discountPackageDiscount: Double,
        val cartItemCount: Int
    ) : ViewState()
} 
