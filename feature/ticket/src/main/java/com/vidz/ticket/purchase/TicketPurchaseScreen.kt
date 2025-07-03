package com.vidz.ticket.purchase

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AvTimer
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.DragHandle
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Train
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.base.components.MetrollButton
import com.vidz.domain.model.P2PJourney
import com.vidz.domain.model.Station
import com.vidz.domain.model.TicketType
import com.vidz.domain.model.TimedTicketPlan

@Composable
fun TicketPurchaseScreenRoot(
    navController: NavController,
    onShowSnackbar: (String) -> Unit,
    viewModel: TicketPurchaseViewModel = hiltViewModel()
) {
    TicketPurchaseScreen(
        navController = navController,
        onShowSnackbar = onShowSnackbar,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketPurchaseScreen(
    navController: NavController,
    onShowSnackbar: (String) -> Unit,
    viewModel: TicketPurchaseViewModel
) {
    //region Define Var
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val bottomSheetState = rememberModalBottomSheetState()
    var showSortMenu by remember { mutableStateOf(false) }
    //endregion

    //region Event Handler
    val onTabSelected = { ticketType: TicketType ->
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.SelectTicketType(ticketType))
    }
    
    val onTimedTicketAddToCart = { ticket: TimedTicketPlan ->
        val cartItem = CartItem(
            id = ticket.id,
            ticketType = TicketType.TIMED,
            name = ticket.name,
            price = ticket.basePrice,
            quantity = 1,
            description = "${ticket.validDuration} days"
        )
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.AddToCart(cartItem))
        onShowSnackbar("${ticket.name} added to cart")
    }
    
    val onP2PJourneyAddToCart = { journey: P2PJourney ->
        val cartItem = CartItem(
            id = journey.id,
            ticketType = TicketType.P2P,
            name = "P2P Journey",
            price = journey.basePrice,
            quantity = 1,
            description = "${journey.distance} km • ${journey.travelTime} min"
        )
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.AddToCart(cartItem))
        onShowSnackbar("Journey added to cart")
    }
    
    val onCartItemQuantityChange = { item: CartItem, quantity: Int ->
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.UpdateCartItemQuantity(item, quantity))
    }
    
    val onCartItemRemove = { item: CartItem ->
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.RemoveFromCart(item))
    }
    
    val onCheckout = {
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.Checkout)
    }
    
    val onShowCart = {
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ShowCartSheet(true))
    }
    
    val onSortChange = { sortType: P2PSortType ->
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.SortP2PBy(sortType))
        showSortMenu = false
    }
    //endregion

    //region UI
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text(
                        "Purchase Tickets",
                        fontWeight = FontWeight.SemiBold
                    )
                },
                actions = {
                    IconButton(onClick = {
                        navController.navigate(com.vidz.base.navigation.DestinationRoutes.TICKET_CART_SCREEN_ROUTE)
                    }) {
                        if (uiState.cartItemCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.error
                                    ) {
                                        Text(
                                            text = uiState.cartItemCount.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            color = MaterialTheme.colorScheme.onError
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart,
                                    contentDescription = "Cart",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.ShoppingCart,
                                contentDescription = "Cart",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (uiState.cartItemCount > 0) {
                ExtendedFloatingActionButton(
                    onClick = onShowCart,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    icon = {
                        Icon(
                            imageVector = Icons.Default.ShoppingCart,
                            contentDescription = "Cart"
                        )
                    },
                    text = {
                        Text(
                            "View Cart • ₫${
                                String.format(
                                    "%,.0f",
                                    uiState.cartTotal
                                )
                            }"
                        )
                    }
                )
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row
            Column(modifier = Modifier.fillMaxWidth()) {
                ScrollableTabRow(
                    selectedTabIndex = if (uiState.selectedTicketType == TicketType.TIMED) 0 else 1,
                    modifier = Modifier.fillMaxWidth(),
                    edgePadding = 16.dp,
                    indicator = { tabPositions ->
                        TabRowDefaults.Indicator(
                            modifier = Modifier
                                .tabIndicatorOffset(tabPositions[if (uiState.selectedTicketType == TicketType.TIMED) 0 else 1])
                                .height(3.dp)
                                .clip(RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp)),
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                ) {
                    Tab(
                        selected = uiState.selectedTicketType == TicketType.TIMED,
                        onClick = { onTabSelected(TicketType.TIMED) },
                        text = { Text("Timed Tickets") }
                    )
                    Tab(
                        selected = uiState.selectedTicketType == TicketType.P2P,
                        onClick = { onTabSelected(TicketType.P2P) },
                        text = { Text("Point-to-Point") }
                    )
                }
                Divider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
            }

            // Content
            when (uiState.selectedTicketType) {
                TicketType.TIMED -> {
                    TimedTicketsContent(
                        tickets = uiState.timedTickets,
                        isLoading = uiState.isLoadingTimed,
                        onAddToCart = onTimedTicketAddToCart
                    )
                }
                TicketType.P2P -> {
                    P2PJourneysContent(
                        journeys = uiState.p2pJourneys,
                        isLoading = uiState.isLoadingP2P,
                        currentSort = uiState.p2pSortType,
                        stations = uiState.stations,
                        isLoadingStations = uiState.isLoadingStations,
                        selectedFromStation = uiState.selectedFromStation,
                        selectedToStation = uiState.selectedToStation,
                        onAddToCart = onP2PJourneyAddToCart,
                        onSortChange = onSortChange,
                        onFromStationSelect = { station ->
                            viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.SelectFromStation(station))
                        },
                        onToStationSelect = { station ->
                            viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.SelectToStation(station))
                        },
                        onClearStations = {
                            viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ClearStationSelection)
                        }
                    )
                }
            }
        }
    }

    //region Dialog and Sheet
    if (uiState.showCartSheet) {
        ModalBottomSheet(
            onDismissRequest = { 
                viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ShowCartSheet(false))
            },
            sheetState = bottomSheetState
        ) {
            CartBottomSheet(
                cartItems = uiState.cartItems,
                cartTotal = uiState.cartTotal,
                isCheckingOut = uiState.isCheckingOut,
                onQuantityChange = onCartItemQuantityChange,
                onRemoveItem = onCartItemRemove,
                onCheckout = onCheckout
            )
        }
    }

    // WebView for payment
    uiState.paymentUrl?.let { url ->
        PaymentWebView(
            url = url,
            onPaymentComplete = {
                viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ProcessPaymentUrl(""))
                onShowSnackbar("Payment completed successfully!")
                navController.popBackStack()
            },
            onPaymentFailed = {
                viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ProcessPaymentUrl(""))
                onShowSnackbar("Payment failed. Please try again.")
            }
        )
    }
    //endregion
}

@Composable
private fun TimedTicketsContent(
    tickets: List<TimedTicketPlan>,
    isLoading: Boolean,
    onAddToCart: (TimedTicketPlan) -> Unit
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Available Timed Tickets",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 16.dp)
            )
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                items(tickets) { ticket ->
                    TimedTicketCard(
                        ticket = ticket,
                        onAddToCart = { onAddToCart(ticket) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun P2PJourneysContent(
    journeys: List<P2PJourney>,
    isLoading: Boolean,
    currentSort: P2PSortType,
    stations: List<Station>,
    isLoadingStations: Boolean,
    selectedFromStation: Station?,
    selectedToStation: Station?,
    onAddToCart: (P2PJourney) -> Unit,
    onSortChange: (P2PSortType) -> Unit,
    onFromStationSelect: (Station?) -> Unit,
    onToStationSelect: (Station?) -> Unit,
    onClearStations: () -> Unit
) {
    var showFromDropdown by remember { mutableStateOf(false) }
    var showToDropdown by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Station Selection Section
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Select Journey Route",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                if (selectedFromStation != null || selectedToStation != null) {
                    IconButton(
                        onClick = onClearStations,
                        modifier = Modifier.size(32.dp)
                    ) {
                        Icon(
                            Icons.Default.Clear,
                            contentDescription = "Clear stations",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.Top
            ) {
                // From Station Dropdown
                ExposedDropdownMenuBox(
                    expanded = showFromDropdown,
                    onExpandedChange = { showFromDropdown = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedFromStation?.name ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("From Station") },
                        placeholder = { Text("From Station") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showFromDropdown)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                    
                    ExposedDropdownMenu(
                        expanded = showFromDropdown,
                        onDismissRequest = { showFromDropdown = false }
                    ) {
                        if (isLoadingStations) {
                            DropdownMenuItem(
                                text = { Text("Loading stations...") },
                                onClick = { }
                            )
                        } else if (stations.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No stations available") },
                                onClick = { }
                            )
                        } else {
                            stations.forEach { station ->
                                DropdownMenuItem(
                                    text = { 
                                        Column {
                                            Text(
                                                text = station.name,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                            Text(
                                                text = station.code,
                                                style = MaterialTheme.typography.bodySmall,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    },
                                    onClick = {
                                        onFromStationSelect(station)
                                        showFromDropdown = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                // To Station Dropdown
                ExposedDropdownMenuBox(
                    expanded = showToDropdown,
                    onExpandedChange = { showToDropdown = it },
                    modifier = Modifier.weight(1f)
                ) {
                    OutlinedTextField(
                        value = selectedToStation?.name ?: "",
                        onValueChange = { },
                        readOnly = true,
                        label = { Text("To Station") },
                        placeholder = { Text("To Station") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showToDropdown)
                        },
                        modifier = Modifier
                            .menuAnchor()
                            .fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(16.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                        )
                    )
                    
                    ExposedDropdownMenu(
                        expanded = showToDropdown,
                        onDismissRequest = { showToDropdown = false }
                    ) {
                        if (isLoadingStations) {
                            DropdownMenuItem(
                                text = { Text("Loading stations...") },
                                onClick = { }
                            )
                        } else if (stations.isEmpty()) {
                            DropdownMenuItem(
                                text = { Text("No stations available") },
                                onClick = { }
                            )
                        } else {
                            // Filter out the selected from station
                            val availableToStations = stations.filter { it.id != selectedFromStation?.id }
                            
                            if (availableToStations.isEmpty()) {
                                DropdownMenuItem(
                                    text = { Text("Please select departure station first") },
                                    onClick = { }
                                )
                            } else {
                                availableToStations.forEach { station ->
                                    DropdownMenuItem(
                                        text = { 
                                            Column {
                                                Text(
                                                    text = station.name,
                                                    style = MaterialTheme.typography.bodyMedium
                                                )
                                                Text(
                                                    text = station.code,
                                                    style = MaterialTheme.typography.bodySmall,
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        onClick = {
                                            onToStationSelect(station)
                                            showToDropdown = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Sort Options
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${journeys.size} routes found",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Box {
                    OutlinedButton(
                        onClick = { showSortMenu = true }
                    ) {
                        Icon(
                            Icons.Default.FilterList, 
                            contentDescription = "Sort",
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(currentSort.displayName)
                    }
                    
                    DropdownMenu(
                        expanded = showSortMenu,
                        onDismissRequest = { showSortMenu = false }
                    ) {
                        P2PSortType.values().forEach { sortType ->
                            DropdownMenuItem(
                                text = { Text(sortType.displayName) },
                                onClick = { 
                                    onSortChange(sortType)
                                    showSortMenu = false 
                                }
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(journeys) { journey ->
                    P2PJourneyCard(
                        journey = journey,
                        stations = stations,
                        onAddToCart = { onAddToCart(journey) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TimedTicketCard(
    ticket: TimedTicketPlan,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Surface(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.AvTimer,
                    contentDescription = "Timed Ticket",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(12.dp)
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = ticket.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "${ticket.validDuration} days validity",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "₫${String.format("%,.0f", ticket.basePrice)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "per pass",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }

        MetrollButton(
            text = "Add to Cart",
            onClick = onAddToCart,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 16.dp)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun P2PJourneyCard(
    journey: P2PJourney,
    stations: List<Station>,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val fromStation = stations.find { it.id == journey.startStationId }
            val toStation = stations.find { it.id == journey.endStationId }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Surface(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = Icons.Default.Train,
                        contentDescription = "Journey",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(12.dp)
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${fromStation?.name ?: "..."} → ${toStation?.name ?: "..."}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    Text(
                        text = "${journey.distance} km • ${journey.travelTime} min",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "₫${String.format("%,.0f", journey.basePrice)}",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )

                MetrollButton(
                    text = "Add to Cart",
                    onClick = onAddToCart
                )
            }
        }
    }
}

@Composable
private fun CartBottomSheet(
    cartItems: List<CartItem>,
    cartTotal: Double,
    isCheckingOut: Boolean,
    onQuantityChange: (CartItem, Int) -> Unit,
    onRemoveItem: (CartItem) -> Unit,
    onCheckout: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.DragHandle,
                contentDescription = "Drag handle",
                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                modifier = Modifier
                    .background(
                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                        CircleShape
                    )
                    .padding(horizontal = 16.dp, vertical = 2.dp)
            )
        }

        Text(
            text = "Shopping Cart",
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )
        
        if (cartItems.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingCart,
                        contentDescription = "Empty Cart",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your cart is empty",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.weight(1f, false),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(cartItems) { item ->
                    CartItemCard(
                        item = item,
                        onQuantityChange = { quantity -> onQuantityChange(item, quantity) },
                        onRemove = { onRemoveItem(item) }
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Total and Checkout
            Card(
                modifier = Modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainerHighest
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Total",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "₫${String.format("%,.0f", cartTotal)}",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    MetrollButton(
                        text = if (isCheckingOut) "Processing..." else "Proceed to Checkout",
                        onClick = onCheckout,
                        enabled = !isCheckingOut,
                        isLoading = isCheckingOut,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CartItemCard(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                item.description?.let { desc ->
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                Text(
                    text = "₫${String.format("%,.0f", item.price)} each",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Column(horizontalAlignment = Alignment.End) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    IconButton(
                        onClick = { onQuantityChange(item.quantity - 1) },
                        enabled = item.quantity > 1,
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f)
                        )
                    ) {
                        Icon(
                            Icons.Default.Remove,
                            contentDescription = "Decrease",
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Text(
                        text = item.quantity.toString(),
                        style = MaterialTheme.typography.titleMedium,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.width(32.dp)
                    )

                    IconButton(
                        onClick = { onQuantityChange(item.quantity + 1) },
                        modifier = Modifier.size(32.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                            contentColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Increase",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                TextButton(onClick = onRemove) {
                    Text(
                        "Remove",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@SuppressLint("SetJavaScriptEnabled")
@Composable
internal fun PaymentWebView(
    url: String,
    onPaymentComplete: () -> Unit,
    onPaymentFailed: () -> Unit
) {
    val context = LocalContext.current
    var showDialog by remember { mutableStateOf(true) }
    
    // Debug logging
    println("PaymentWebView triggered with URL: $url")
    
    if (showDialog) {
        AlertDialog(
            onDismissRequest = { 
                showDialog = false
                onPaymentFailed()
            },
            title = { Text("Payment - PayOS") },
            text = {
                Column {
                    Text(
                        text = "Please complete your payment in the web browser below:",
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    AndroidView(
                        factory = { context ->
                            WebView(context).apply {
                                webViewClient = object : WebViewClient() {
                                    override fun onPageFinished(view: WebView?, url: String?) {
                                        super.onPageFinished(view, url)
                                        println("WebView page finished: $url")
                                        // Check if payment is complete based on URL patterns for PayOS
                                        url?.let { currentUrl ->
                                            when {
                                                currentUrl.contains("success") || 
                                                currentUrl.contains("complete") ||
                                                currentUrl.contains("payment_status=success") ||
                                                currentUrl.contains("vnp_ResponseCode=00") ||
                                                currentUrl.contains("status=success") -> {
                                                    println("Payment success detected")
                                                    showDialog = false
                                                    onPaymentComplete()
                                                }
                                                currentUrl.contains("failed") || 
                                                currentUrl.contains("error") ||
                                                currentUrl.contains("payment_status=failed") ||
                                                currentUrl.contains("vnp_ResponseCode") ||
                                                currentUrl.contains("status=failed") ||
                                                currentUrl.contains("cancel") -> {
                                                    println("Payment failure detected")
                                                    showDialog = false
                                                    onPaymentFailed()
                                                }
                                            }
                                        }
                                    }
                                    
                                    override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                        // Handle deep links or redirects
                                        request?.url?.toString()?.let { currentUrl ->
                                            println("WebView URL loading: $currentUrl")
                                            when {
                                                currentUrl.startsWith("metroll://") -> {
                                                    // Handle app deep link for payment completion
                                                    if (currentUrl.contains("success") || currentUrl.contains("vnp_ResponseCode=00")) {
                                                        showDialog = false
                                                        onPaymentComplete()
                                                    } else {
                                                        showDialog = false
                                                        onPaymentFailed()
                                                    }
                                                    return true
                                                }
                                            }
                                        }
                                        return false
                                    }
                                }
                                settings.javaScriptEnabled = true
                                settings.domStorageEnabled = true
                                settings.loadWithOverviewMode = true
                                settings.useWideViewPort = true
                                settings.setSupportZoom(true)
                                settings.builtInZoomControls = true
                                settings.displayZoomControls = false
                                println("Loading URL in WebView: $url")
                                loadUrl(url)
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(400.dp)
                    )
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { 
                        showDialog = false
                        onPaymentComplete() 
                    }
                ) {
                    Text("Close")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDialog = false
                        onPaymentFailed()
                    }
                ) {
                    Text("Cancel Payment")
                }
            }
        )
    }
} 
