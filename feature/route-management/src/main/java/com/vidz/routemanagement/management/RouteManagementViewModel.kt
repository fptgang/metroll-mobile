package com.vidz.routemanagement.management

import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.MetroLine
import com.vidz.domain.model.Station
import com.vidz.domain.model.P2PJourney
import com.vidz.domain.model.TicketType
import com.vidz.domain.model.CheckoutItem
import com.vidz.routemanagement.domain.usecase.GetMetroLinesUseCase
import com.vidz.domain.usecase.station.GetStationsUseCase
import com.vidz.domain.usecase.p2pjourney.GetP2PJourneyByStationsUseCase
import com.vidz.domain.usecase.cart.AddToCartUseCase
import com.vidz.domain.usecase.cart.GetCartItemsUseCase
import com.vidz.ticket.purchase.CartItem
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlinx.coroutines.Dispatchers

@HiltViewModel
class RouteManagementViewModel @Inject constructor(
    private val getMetroLinesUseCase: GetMetroLinesUseCase,
    private val getStationsUseCase: GetStationsUseCase,
    private val getP2PJourneyByStationsUseCase: GetP2PJourneyByStationsUseCase,
    private val addToCartUseCase: AddToCartUseCase,
    private val getCartItemsUseCase: GetCartItemsUseCase,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) : BaseViewModel<
        RouteManagementViewModel.RouteManagementViewEvent,
        RouteManagementViewModel.RouteManagementViewState,
        RouteManagementViewModel.RouteManagementViewModelState
        >(RouteManagementViewModelState()) {

    init {
        loadMetroLines()
        loadStations()
        observeCartItems()
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

    private fun loadStations() {
        viewModelScope.launch(ioDispatcher) {
            getStationsUseCase(page = 0, size = 100)
                .onEach { result ->
                    when (result) {
                        is Result.Init -> {
                            viewModelState.value = viewModelState.value.copy(isLoadingStations = true)
                        }
                        is Result.Success -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoadingStations = false,
                                stations = result.data.content,
                                error = null
                            )
                        }
                        is Result.ServerError -> {
                            viewModelState.value = viewModelState.value.copy(
                                isLoadingStations = false,
                                error = result.message
                            )
                        }
                    }
                }
                .launchIn(this)
        }
    }

    private fun observeCartItems() {
        viewModelScope.launch {
            getCartItemsUseCase()
                .onEach { checkoutItems ->
                    val cartItemCount = checkoutItems.sumOf { it.quantity }
                    viewModelState.value = viewModelState.value.copy(
                        cartItemCount = cartItemCount
                    )
                }
                .launchIn(this)
        }
    }

    private fun selectMetroLine(metroLine: MetroLine) {
        viewModelState.value = viewModelState.value.copy(selectedMetroLine = metroLine)
    }

    private fun refreshData() {
        viewModelState.value = viewModelState.value.copy(error = null)
        loadMetroLines()
        loadStations()
    }

    private fun onMapPointClick(station: Station) {
        viewModelState.value = viewModelState.value.copy(
            clickedStation = station,
            showBottomSheet = true,
            // Clear previous selections when starting new journey
            selectedStartStation = null,
            selectedEndStation = null,
            availableJourneys = emptyList(),
            selectedJourney = null,
            showAddToCartCard = false
        )
    }

    private fun showBottomSheet(show: Boolean) {
        viewModelState.value = viewModelState.value.copy(showBottomSheet = show)
        if (!show) {
            // Clear temporary data when closing bottom sheet
            viewModelState.value = viewModelState.value.copy(
                clickedStation = null,
                selectedStartStation = null,
                selectedEndStation = null,
                availableJourneys = emptyList(),
                selectedJourney = null,
                isLoadingJourneys = false
            )
        }
    }

    private fun selectStartStation(station: Station?) {
        viewModelState.value = viewModelState.value.copy(
            selectedStartStation = station,
            selectedEndStation = if (viewModelState.value.selectedEndStation?.id == station?.id) null else viewModelState.value.selectedEndStation
        )
        if (station != null) {
            searchJourneys()
        }
    }

    private fun selectEndStation(station: Station?) {
        val currentState = viewModelState.value
        val startStation = currentState.clickedStation
        
        if (startStation != null && station != null) {
            // Update state with selected stations and close bottom sheet
            viewModelState.value = currentState.copy(
                selectedStartStation = startStation,
                selectedEndStation = station,
                showBottomSheet = false, // Close bottom sheet
                isLoadingJourneys = true
            )
            
            // Fetch journeys for the selected stations
            viewModelScope.launch(ioDispatcher) {
                getP2PJourneyByStationsUseCase(
                    page = 0,
                    size = 50,
                    startStationId = startStation.code,
                    endStationId = station.code
                )
                    .onEach { result ->
                        when (result) {
                            is Result.Init -> {
                                viewModelState.value = viewModelState.value.copy(isLoadingJourneys = true)
                            }
                            is Result.Success -> {
                                val journeys = result.data.content
                                viewModelState.value = viewModelState.value.copy(
                                    isLoadingJourneys = false,
                                    availableJourneys = journeys,
                                    selectedJourney = journeys.firstOrNull(),
                                    showAddToCartCard = journeys.isNotEmpty(), // Show add to cart if journeys found
                                    error = null
                                )
                            }
                            is Result.ServerError -> {
                                viewModelState.value = viewModelState.value.copy(
                                    isLoadingJourneys = false,
                                    availableJourneys = emptyList(),
                                    error = result.message
                                )
                            }
                        }
                    }
                    .launchIn(this)
            }
        } else {
            viewModelState.value = currentState.copy(
                selectedEndStation = station,
                selectedStartStation = if (currentState.selectedStartStation?.id == station?.id) null else currentState.selectedStartStation
            )
            if (station != null) {
                searchJourneys()
            }
        }
    }

    private fun searchJourneys() {
        val startStation = viewModelState.value.selectedStartStation
        val endStation = viewModelState.value.selectedEndStation
        
        if (startStation != null && endStation != null) {
            viewModelScope.launch(ioDispatcher) {
                getP2PJourneyByStationsUseCase(
                    page = 0,
                    size = 50,
                    startStationId = startStation.code,
                    endStationId = endStation.code
                )
                    .onEach { result ->
                        when (result) {
                            is Result.Init -> {
                                viewModelState.value = viewModelState.value.copy(isLoadingJourneys = true)
                            }
                            is Result.Success -> {
                                val journeys = result.data.content
                                viewModelState.value = viewModelState.value.copy(
                                    isLoadingJourneys = false,
                                    availableJourneys = journeys,
                                    selectedJourney = journeys.firstOrNull(),
                                    showBottomSheet = false, // Close bottom sheet
                                    showAddToCartCard = journeys.isNotEmpty(), // Show add to cart if journeys found
                                    error = null
                                )
                            }
                            is Result.ServerError -> {
                                viewModelState.value = viewModelState.value.copy(
                                    isLoadingJourneys = false,
                                    availableJourneys = emptyList(),
                                    error = result.message
                                )
                            }
                        }
                    }
                    .launchIn(this)
            }
        } else {
            viewModelState.value = viewModelState.value.copy(
                availableJourneys = emptyList(),
                selectedJourney = null,
                isLoadingJourneys = false
            )
        }
    }

    private fun addToCart(journey: P2PJourney) {
        viewModelScope.launch(ioDispatcher) {
            try {
                val checkoutItem = CheckoutItem(
                    ticketType = TicketType.P2P,
                    p2pJourneyId = journey.id,
                    timedTicketPlanId = null,
                    quantity = 1
                )
                
                addToCartUseCase(checkoutItem)
                
                // Close bottom sheet and hide add to cart card after adding to cart
                viewModelState.value = viewModelState.value.copy(
                    showBottomSheet = false,
                )
            } catch (e: Exception) {
                viewModelState.value = viewModelState.value.copy(
                    error = e.message ?: "Failed to add to cart"
                )
            }
        }
    }

    private fun clearStationSelection() {
        viewModelState.value = viewModelState.value.copy(
            selectedStartStation = viewModelState.value.clickedStation,
            selectedEndStation = null,
            availableJourneys = emptyList(),
            selectedJourney = null,
            isLoadingJourneys = false
        )
    }

    private fun clearAllSelections() {
        viewModelState.value = viewModelState.value.copy(
            selectedStartStation = null,
            selectedEndStation = null,
            availableJourneys = emptyList(),
            selectedJourney = null,
            isLoadingJourneys = false,
            showAddToCartCard = false
        )
    }

    private fun hideAddToCartCard() {
        viewModelState.value = viewModelState.value.copy(showAddToCartCard = false)
    }

    data class RouteManagementViewState(
        val isLoading: Boolean = false,
        val isLoadingStations: Boolean = false,
        val metroLines: List<MetroLine> = emptyList(),
        val selectedMetroLine: MetroLine? = null,
        val stations: List<Station> = emptyList(),
        val error: String? = null,
        val cartItemCount: Int = 0,
        
        // Bottom Sheet State
        val showBottomSheet: Boolean = false,
        val clickedStation: Station? = null,
        val selectedStartStation: Station? = null,
        val selectedEndStation: Station? = null,
        val availableJourneys: List<P2PJourney> = emptyList(),
        val selectedJourney: P2PJourney? = null,
        val isLoadingJourneys: Boolean = false,
        
        // Add to Cart State
        val showAddToCartCard: Boolean = false
    ) : ViewState()
    
    data class RouteManagementViewModelState(
        val isLoading: Boolean = false,
        val isLoadingStations: Boolean = false,
        val metroLines: List<MetroLine> = emptyList(),
        val selectedMetroLine: MetroLine? = null,
        val stations: List<Station> = emptyList(),
        val error: String? = null,
        val cartItemCount: Int = 0,
        
        // Bottom Sheet State
        val showBottomSheet: Boolean = false,
        val clickedStation: Station? = null,
        val selectedStartStation: Station? = null,
        val selectedEndStation: Station? = null,
        val availableJourneys: List<P2PJourney> = emptyList(),
        val selectedJourney: P2PJourney? = null,
        val isLoadingJourneys: Boolean = false,
        
        // Add to Cart State
        val showAddToCartCard: Boolean = false
    ) : ViewModelState() {
        override fun toUiState(): ViewState {
            return RouteManagementViewState(
                isLoading = isLoading,
                isLoadingStations = isLoadingStations,
                metroLines = metroLines,
                selectedMetroLine = selectedMetroLine,
                stations = stations,
                error = error,
                cartItemCount = cartItemCount,
                showBottomSheet = showBottomSheet,
                clickedStation = clickedStation,
                selectedStartStation = selectedStartStation,
                selectedEndStation = selectedEndStation,
                availableJourneys = availableJourneys,
                selectedJourney = selectedJourney,
                isLoadingJourneys = isLoadingJourneys,
                showAddToCartCard = showAddToCartCard
            )
        }
    }

    sealed class RouteManagementViewEvent : ViewEvent {
        data class SelectMetroLine(val metroLine: MetroLine) : RouteManagementViewEvent()
        object RefreshData : RouteManagementViewEvent()
        data class OnMapPointClick(val station: Station) : RouteManagementViewEvent()
        data class ShowBottomSheet(val show: Boolean) : RouteManagementViewEvent()
        data class SelectStartStation(val station: Station?) : RouteManagementViewEvent()
        data class SelectEndStation(val station: Station?) : RouteManagementViewEvent()
        data class AddToCart(val journey: P2PJourney) : RouteManagementViewEvent()
        object ClearStationSelection : RouteManagementViewEvent()
        object ClearAllSelections : RouteManagementViewEvent()
        object HideAddToCartCard : RouteManagementViewEvent()
    }

    override fun onTriggerEvent(event: RouteManagementViewEvent) {
        when (event) {
            is RouteManagementViewEvent.SelectMetroLine -> selectMetroLine(event.metroLine)
            is RouteManagementViewEvent.RefreshData -> refreshData()
            is RouteManagementViewEvent.OnMapPointClick -> onMapPointClick(event.station)
            is RouteManagementViewEvent.ShowBottomSheet -> showBottomSheet(event.show)
            is RouteManagementViewEvent.SelectStartStation -> selectStartStation(event.station)
            is RouteManagementViewEvent.SelectEndStation -> selectEndStation(event.station)
            is RouteManagementViewEvent.AddToCart -> addToCart(event.journey)
            is RouteManagementViewEvent.ClearStationSelection -> clearStationSelection()
            is RouteManagementViewEvent.ClearAllSelections -> clearAllSelections()
            is RouteManagementViewEvent.HideAddToCartCard -> hideAddToCartCard()
        }
    }
} 
