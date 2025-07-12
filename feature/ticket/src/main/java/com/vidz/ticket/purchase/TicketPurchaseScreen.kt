package com.vidz.ticket.purchase

import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.base.components.MetrollActionCard
import com.vidz.base.components.MetrollButton
import com.vidz.base.components.MetrollTextField
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
        onShowSnackbar("${ticket.name} đã được thêm vào giỏ hàng")
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
        onShowSnackbar("Chuyến đi đã được thêm vào giỏ hàng")
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
    
    val onNavigateToCart = {
        navController.navigate(com.vidz.base.navigation.DestinationRoutes.TICKET_CART_SCREEN_ROUTE)
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
                title = { Text("Mua Vé") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Quay lại")
                    }
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
                                            style = MaterialTheme.typography.labelSmall
                                        )
                                    }
                                }
                            ) {
                                Icon(
                                    Icons.Default.ShoppingCart, 
                                    contentDescription = "Giỏ hàng",
                                    tint = MaterialTheme.colorScheme.primary
                                )
                            }
                        } else {
                            Icon(
                                Icons.Default.ShoppingCart, 
                                contentDescription = "Giỏ hàng",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        floatingActionButton = {
            if (uiState.cartItemCount > 0) {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToCart,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    BadgedBox(
                        badge = {
                            Badge {
                                Text(uiState.cartItemCount.toString())
                            }
                        }
                    ) {
                        Icon(Icons.Default.ShoppingCart, contentDescription = "Giỏ hàng")
                    }
                    Spacer(Modifier.width(8.dp))
                    Text("Giỏ hàng • ${String.format("%,.0f", uiState.subtotal)}₫")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Tab Row - Only show for customers, staff only see P2P
            if (uiState.isCustomer) {
                TabRow(
                    selectedTabIndex = if (uiState.selectedTicketType == TicketType.TIMED) 0 else 1,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Tab(
                        selected = uiState.selectedTicketType == TicketType.TIMED,
                        onClick = { onTabSelected(TicketType.TIMED) },
                        text = { Text("Vé Thời Hạn") }
                    )
                    Tab(
                        selected = uiState.selectedTicketType == TicketType.P2P,
                        onClick = { onTabSelected(TicketType.P2P) },
                        text = { Text("Vé Theo Trạm") }
                    )
                }
            } else {
                // For staff, show a simple title instead of tabs
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                ) {
                    Text(
                        text = "Vé Theo Trạm",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            // Content
            if (uiState.isStaff) {
                // Staff users only see P2P content
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
                        println("TicketPurchaseScreen: From station selected: ${station?.name}")
                        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.SelectFromStation(station))
                    },
                    onToStationSelect = { station ->
                        println("TicketPurchaseScreen: To station selected: ${station?.name}")
                        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.SelectToStation(station))
                    },
                    onClearStations = {
                        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ClearStationSelection)
                    },
                    isStaff = uiState.isStaff,
                    staffAssignedStation = uiState.staffAssignedStation
                )
            } else {
                // Customer users see content based on selected tab
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
                                println("TicketPurchaseScreen: From station selected: ${station?.name}")
                                viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.SelectFromStation(station))
                            },
                            onToStationSelect = { station ->
                                println("TicketPurchaseScreen: To station selected: ${station?.name}")
                                viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.SelectToStation(station))
                            },
                            onClearStations = {
                                viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ClearStationSelection)
                            },
                            isStaff = uiState.isStaff,
                            staffAssignedStation = uiState.staffAssignedStation
                        )
                    }
                }
            }
        }
    }

    //region Dialog and Sheet
    // WebView for payment
    uiState.paymentUrl?.let { url ->
        PaymentWebView(
            url = url,
            onPaymentComplete = {
                viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ProcessPaymentUrl(""))
                onShowSnackbar("Thanh toán thành công!")
                navController.popBackStack()
            },
            onPaymentFailed = {
                viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ProcessPaymentUrl(""))
                onShowSnackbar("Thanh toán thất bại. Vui lòng thử lại.")
            }
        )
    }
    //endregion
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
                text = "Vé Thời Hạn Khả Dụng",
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
    onClearStations: () -> Unit,
    isStaff: Boolean = false,
    staffAssignedStation: String? = null
) {
    var showFromDropdown by remember { mutableStateOf(false) }
    var showToDropdown by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Station Selection Section - Always visible
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Chọn Tuyến Đường",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                        
                        // Show staff-specific message if they have limited stations
                        if (isStaff && !staffAssignedStation.isNullOrBlank()) {
                            Text(
                                text = "Bạn chỉ có thể chọn trạm: $staffAssignedStation",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(top = 4.dp)
                            )
                        }
                    }
                    
                    if (selectedFromStation != null || selectedToStation != null) {
                        IconButton(
                            onClick = onClearStations,
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Xóa trạm",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(12.dp))
                
                if (isLoadingStations) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp))
                            Text(
                                text = "Đang tải trạm...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
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
                                label = { Text("Từ") },
                                placeholder = { Text("Chọn một") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showFromDropdown)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            
                            ExposedDropdownMenu(
                                expanded = showFromDropdown,
                                onDismissRequest = { showFromDropdown = false }
                            ) {
                                if (stations.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { 
                                            Text(
                                                "Không có trạm khả dụng",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            ) 
                                        },
                                        onClick = { },
                                        enabled = false
                                    )
                                } else {
                                    stations.forEach { station ->
                                        DropdownMenuItem(
                                            text = { 
                                                Column {
                                                    Text(
                                                        text = station.name,
                                                        style = MaterialTheme.typography.bodyMedium,
                                                        fontWeight = FontWeight.Medium
                                                    )
                                                    Text(
                                                        text = "Mã: ${station.code}",
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
                                label = { Text("Đến") },
                                placeholder = { Text("Chọn một") },
                                trailingIcon = {
                                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = showToDropdown)
                                },
                                modifier = Modifier
                                    .menuAnchor()
                                    .fillMaxWidth(),
                                singleLine = true,
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline
                                )
                            )
                            
                            ExposedDropdownMenu(
                                expanded = showToDropdown,
                                onDismissRequest = { showToDropdown = false }
                            ) {
                                if (stations.isEmpty()) {
                                    DropdownMenuItem(
                                        text = { 
                                            Text(
                                                "Không có trạm khả dụng",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            ) 
                                        },
                                        onClick = { },
                                        enabled = false
                                    )
                                } else {
                                    // Filter out the selected from station
                                    val availableToStations = stations.filter { it.id != selectedFromStation?.id }
                                    
                                    if (availableToStations.isEmpty() && selectedFromStation != null) {
                                        DropdownMenuItem(
                                            text = { 
                                                Text(
                                                    "Không có trạm khác khả dụng",
                                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                                ) 
                                            },
                                            onClick = { },
                                            enabled = false
                                        )
                                    } else {
                                        (if (selectedFromStation != null) availableToStations else stations).forEach { station ->
                                            DropdownMenuItem(
                                                text = { 
                                                    Column {
                                                        Text(
                                                            text = station.name,
                                                            style = MaterialTheme.typography.bodyMedium,
                                                            fontWeight = FontWeight.Medium
                                                        )
                                                        Text(
                                                            text = "Mã: ${station.code}",
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
                }
                
                // Station Selection Info
                if (selectedFromStation != null || selectedToStation != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (selectedFromStation != null && selectedToStation != null) {
                                "Tuyến: ${selectedFromStation.name} → ${selectedToStation.name}"
                            } else if (selectedFromStation != null) {
                                "Từ: ${selectedFromStation.name}"
                            } else {
                                "Đến: ${selectedToStation?.name}"
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // Journey Results Section
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CircularProgressIndicator()
                    Text(
                        text = "Đang tìm kiếm tuyến đường...",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            // Sort Options and Results Count
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tìm thấy ${journeys.size} tuyến đường",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontWeight = FontWeight.Medium
                )
                
                Box {
                    OutlinedButton(
                        onClick = { showSortMenu = true }
                    ) {
                        Icon(
                            Icons.Default.FilterList, 
                            contentDescription = "Sắp xếp",
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
            
            // Journey List
            if (journeys.isEmpty()) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = if (selectedFromStation != null || selectedToStation != null) {
                                    "Không tìm thấy tuyến đường cho các trạm đã chọn"
                                } else if (isStaff && !staffAssignedStation.isNullOrBlank()) {
                                    "Chọn trạm để tìm kiếm tuyến đường từ trạm được gán của bạn"
                                } else {
                                    "Chọn trạm để tìm kiếm tuyến đường"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = if (selectedFromStation != null || selectedToStation != null) {
                                    "Hãy thử chọn các trạm khác hoặc xóa lựa chọn của bạn"
                                } else if (isStaff && !staffAssignedStation.isNullOrBlank()) {
                                    "Bạn chỉ có thể chọn trạm: $staffAssignedStation"
                                } else {
                                    "Chọn trạm khởi hành và đích đến ở trên"
                                },
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
            } else {
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
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = ticket.name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "${ticket.validDuration} ngày",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${String.format("%,.0f", ticket.basePrice)}₫",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                IconButton(
                    onClick = onAddToCart,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm vào giỏ hàng",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
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
            .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(8.dp))
            .fillMaxWidth()
            .animateContentSize(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    val fromStation = stations.find { it.code == journey.startStationId }
                    val toStation = stations.find { it.code == journey.endStationId }
                    
                    Text(
                        text = "${fromStation?.name ?: journey.startStationId} → ${toStation?.name ?: journey.endStationId}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.fillMaxWidth(0.9f)
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "From: ${fromStation?.name ?: journey.startStationId} (${fromStation?.code ?: ""})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = "To: ${toStation?.name ?: journey.endStationId} (${toStation?.code ?: ""})",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
                
                Text(
                    text = "${String.format("%,.0f", journey.basePrice)}₫",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "${journey.distance} km",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "${journey.travelTime} min",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                
                IconButton(
                    onClick = onAddToCart,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add to Cart",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
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
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleSmall,
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
                    text = "${String.format("%,.0f", item.price)}₫ each",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(
                    onClick = { onQuantityChange(item.quantity - 1) },
                    enabled = item.quantity > 1
                ) {
                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                }
                
                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.width(32.dp)
                )
                
                IconButton(
                    onClick = { onQuantityChange(item.quantity + 1) }
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Increase")
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
                                                currentUrl.contains("status=success") -> {
                                                    println("Payment success detected")
                                                    showDialog = false
                                                    onPaymentComplete()
                                                }
                                                currentUrl.contains("failed") || 
                                                currentUrl.contains("error") ||
                                                currentUrl.contains("payment_status=failed") ||
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
                                                    if (currentUrl.contains("success")) {
                                                        showDialog = false
                                                        onPaymentComplete()
                                                    } else {
                                                        showDialog = false
                                                        onPaymentFailed()
                                                    }
                                                    return true
                                                }

                                                else -> {}
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
                    Text("Payment Complete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDialog = false
                        onPaymentFailed()
                    }
                ) {
                    Text("Cancel")
                }
            }
        )
    }
} 

