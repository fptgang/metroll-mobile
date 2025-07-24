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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
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
import androidx.compose.material3.MenuAnchorType
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
import androidx.compose.ui.draw.clip
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
                title = { 
                    Text(
                        "Mua Vé",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },

                actions = {
                    IconButton(
                        onClick = { 
                            navController.navigate(com.vidz.base.navigation.DestinationRoutes.TICKET_CART_SCREEN_ROUTE)
                        },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        if (uiState.cartItemCount > 0) {
                            BadgedBox(
                                badge = {
                                    Badge(
                                        containerColor = MaterialTheme.colorScheme.primary,
                                        contentColor = MaterialTheme.colorScheme.onPrimary
                                    ) {
                                        Text(
                                            text = uiState.cartItemCount.toString(),
                                            style = MaterialTheme.typography.labelSmall,
                                            fontWeight = FontWeight.Bold
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
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            if (uiState.cartItemCount > 0) {
                ExtendedFloatingActionButton(
                    onClick = onNavigateToCart,
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.height(56.dp)
                ) {
                    Icon(
                        Icons.Default.ShoppingCart, 
                        contentDescription = "Giỏ hàng",
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Giỏ hàng (${uiState.cartItemCount}) • ${String.format("%,.0f", uiState.subtotal)}₫",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        },
        // Add content padding to account for bottom navigation bar
        contentWindowInsets = WindowInsets(0, 0, 0, 0)
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                // Tab Row - Only show for customers, staff only see P2P
                if (uiState.isCustomer) {
                    TabRow(
                        selectedTabIndex = if (uiState.selectedTicketType == TicketType.TIMED) 0 else 1,
                        modifier = Modifier.fillMaxWidth(),
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        indicator = { tabPositions ->
                            Box(
                                modifier = Modifier
                                    .tabIndicatorOffset(tabPositions[if (uiState.selectedTicketType == TicketType.TIMED) 0 else 1])
                                    .height(3.dp)
                                    .background(
                                        MaterialTheme.colorScheme.primary,
                                        RoundedCornerShape(topStart = 2.dp, topEnd = 2.dp)
                                    )
                            )
                        }
                    ) {
                        Tab(
                            selected = uiState.selectedTicketType == TicketType.TIMED,
                            onClick = { onTabSelected(TicketType.TIMED) },
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text(
                                "Vé Thời Hạn",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (uiState.selectedTicketType == TicketType.TIMED) FontWeight.SemiBold else FontWeight.Medium
                            )
                        }
                        Tab(
                            selected = uiState.selectedTicketType == TicketType.P2P,
                            onClick = { onTabSelected(TicketType.P2P) },
                            modifier = Modifier.height(56.dp)
                        ) {
                            Text(
                                "Vé Theo Trạm",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (uiState.selectedTicketType == TicketType.P2P) FontWeight.SemiBold else FontWeight.Medium
                            )
                        }
                    }
                } else {
                    // For staff, show a modern header instead of tabs
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                Brush.horizontalGradient(
                                    colors = listOf(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Text(
                            text = "Vé Theo Trạm",
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Content with bottom padding for FAB and bottom navigation
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = if (uiState.cartItemCount > 0) 144.dp else 80.dp) // Extra padding for bottom nav + FAB
                ) {
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
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(48.dp),
                    color = MaterialTheme.colorScheme.primary,
                    strokeWidth = 4.dp
                )
                Text(
                    text = "Đang tải vé thời hạn...",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Vé Thời Hạn Khả Dụng",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            items(tickets) { ticket ->
                TimedTicketCard(
                    ticket = ticket,
                    onAddToCart = { onAddToCart(ticket) }
                )
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
    
    // Automatically select the staff's assigned station as the default "from" station
    LaunchedEffect(isStaff, staffAssignedStation, stations) {
        if (isStaff && !staffAssignedStation.isNullOrBlank() && selectedFromStation == null && stations.isNotEmpty()) {
            // For staff users, automatically select their assigned station as the "from" station
            val assignedStation = stations.find { it.code == staffAssignedStation }
            assignedStation?.let { station ->
                onFromStationSelect(station)
            }
        }
    }
    
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(20.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Station Selection Section - Only show for non-staff users
        if (!isStaff) {
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.surfaceContainer
                    ),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "Chọn Tuyến Đường",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            }
                            
                            // Show clear button when either station is selected
                            if (selectedFromStation != null || selectedToStation != null) {
                                IconButton(
                                    onClick = onClearStations,
                                    modifier = Modifier
                                        .size(40.dp)
                                        .background(
                                            MaterialTheme.colorScheme.errorContainer,
                                            RoundedCornerShape(12.dp)
                                        )
                                ) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Xóa trạm",
                                        tint = MaterialTheme.colorScheme.onErrorContainer,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        
                        if (isLoadingStations) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(24.dp),
                                        strokeWidth = 3.dp
                                    )
                                    Text(
                                        text = "Đang tải danh sách trạm...",
                                        style = MaterialTheme.typography.bodyLarge,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        } else {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(16.dp),
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
                                        label = {
                                            Text(
                                                "Từ trạm",
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        },
                                        placeholder = {
                                            Text(
                                                "Chọn trạm khởi hành",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showFromDropdown)
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
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
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        style = MaterialTheme.typography.bodyMedium
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
                                                                style = MaterialTheme.typography.bodyLarge,
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
                                        label = {
                                            Text(
                                                "Đến trạm",
                                                style = MaterialTheme.typography.labelLarge
                                            )
                                        },
                                        placeholder = {
                                            Text(
                                                "Chọn trạm đích",
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        },
                                        trailingIcon = {
                                            ExposedDropdownMenuDefaults.TrailingIcon(expanded = showToDropdown)
                                        },
                                        modifier = Modifier
                                            .menuAnchor()
                                            .fillMaxWidth(),
                                        singleLine = true,
                                        shape = RoundedCornerShape(12.dp),
                                        colors = OutlinedTextFieldDefaults.colors(
                                            focusedBorderColor = MaterialTheme.colorScheme.primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
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
                                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                        style = MaterialTheme.typography.bodyMedium
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
                                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                                            style = MaterialTheme.typography.bodyMedium
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
                                                                    style = MaterialTheme.typography.bodyLarge,
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
                            Spacer(modifier = Modifier.height(12.dp))
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(
                                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                        RoundedCornerShape(8.dp)
                                    )
                                    .padding(12.dp)
                            ) {
                                Text(
                                    text = if (selectedFromStation != null && selectedToStation != null) {
                                        "Tuyến đã chọn: ${selectedFromStation.name} → ${selectedToStation.name}"
                                    } else if (selectedFromStation != null) {
                                        "Trạm khởi hành: ${selectedFromStation.name}"
                                    } else {
                                        "Trạm đích: ${selectedToStation?.name}"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Medium
                                )
                            }
                        }
                    }
                }
            }
        }
        
        // Journey Results Section
        if (isLoading) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(48.dp),
                            color = MaterialTheme.colorScheme.primary,
                            strokeWidth = 4.dp
                        )
                        Text(
                            text = "Đang tìm kiếm tuyến đường...",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        } else {
            // Sort Options and Results Count
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Tìm thấy ${journeys.size} tuyến đường",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.SemiBold
                    )
                    
                    // Only show filter box for non-staff users
                    if (!isStaff) {
                        Box {
                            OutlinedButton(
                                onClick = { showSortMenu = true },
                                shape = RoundedCornerShape(12.dp),
                                colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.onSurface
                                )
                            ) {
                                Icon(
                                    Icons.Default.FilterList,
                                    contentDescription = "Sắp xếp",
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    currentSort.displayName,
                                    style = MaterialTheme.typography.labelLarge
                                )
                            }
                            
                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                P2PSortType.values().forEach { sortType ->
                                    DropdownMenuItem(
                                        text = {
                                            Text(
                                                sortType.displayName,
                                                style = MaterialTheme.typography.bodyMedium
                                            )
                                        },
                                        onClick = {
                                            onSortChange(sortType)
                                            showSortMenu = false
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
            
            // Journey List
            if (journeys.isEmpty()) {
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(40.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                Text(
                                    text = if (selectedFromStation != null || selectedToStation != null) {
                                        "Không tìm thấy tuyến đường"
                                    } else if (isStaff && !staffAssignedStation.isNullOrBlank()) {
                                        "Chọn trạm để tìm kiếm"
                                    } else {
                                        "Chọn trạm để bắt đầu"
                                    },
                                    style = MaterialTheme.typography.headlineSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = if (selectedFromStation != null || selectedToStation != null) {
                                        "Hãy thử chọn các trạm khác"
                                    } else if (isStaff && !staffAssignedStation.isNullOrBlank()) {
                                        "Trạm được gán: $staffAssignedStation"
                                    } else {
                                        "Chọn trạm khởi hành và đích đến"
                                    },
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            } else {
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = ticket.name,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${ticket.validDuration} ngày",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                Text(
                    text = "${String.format("%,.0f", ticket.basePrice)}₫",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm vào giỏ hàng",
                        tint = MaterialTheme.colorScheme.onPrimary,
                        modifier = Modifier.size(24.dp)
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
            .fillMaxWidth()
            .animateContentSize(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
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
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface,
                        modifier = Modifier.fillMaxWidth(0.85f)
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                        Text(
                            text = "Từ: ${fromStation?.name ?: journey.startStationId}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = "Đến: ${toStation?.name ?: journey.endStationId}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Text(
                    text = "${String.format("%,.0f", journey.basePrice)}₫",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.secondaryContainer,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${journey.distance} km",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSecondaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.tertiaryContainer,
                                RoundedCornerShape(8.dp)
                            )
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${journey.travelTime} phút",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onTertiaryContainer,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
                
                IconButton(
                    onClick = onAddToCart,
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            MaterialTheme.colorScheme.primary,
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Thêm vào giỏ hàng",
                        tint = MaterialTheme.colorScheme.onPrimary,
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
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                item.description?.let { desc ->
                    Text(
                        text = desc,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
                
                Text(
                    text = "${String.format("%,.0f", item.price)}₫ mỗi vé",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
            
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                IconButton(
                    onClick = { onQuantityChange(item.quantity - 1) },
                    enabled = item.quantity > 1,
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            if (item.quantity > 1) MaterialTheme.colorScheme.primaryContainer
                            else MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Remove, 
                        contentDescription = "Giảm số lượng",
                        tint = if (item.quantity > 1) MaterialTheme.colorScheme.onPrimaryContainer
                               else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                Text(
                    text = item.quantity.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.width(32.dp)
                )
                
                IconButton(
                    onClick = { onQuantityChange(item.quantity + 1) },
                    modifier = Modifier
                        .size(36.dp)
                        .background(
                            MaterialTheme.colorScheme.primaryContainer,
                            RoundedCornerShape(8.dp)
                        )
                ) {
                    Icon(
                        Icons.Default.Add, 
                        contentDescription = "Tăng số lượng",
                        tint = MaterialTheme.colorScheme.onPrimaryContainer,
                        modifier = Modifier.size(18.dp)
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
            title = { 
                Text(
                    "Thanh toán - PayOS",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            text = {
                Column {
                    Text(
                        text = "Vui lòng hoàn tất thanh toán trong trình duyệt web bên dưới:",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier.padding(bottom = 16.dp)
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
                            .clip(RoundedCornerShape(12.dp))
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
                    Text(
                        "Hoàn tất thanh toán",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { 
                        showDialog = false
                        onPaymentFailed()
                    }
                ) {
                    Text(
                        "Hủy",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            },
            shape = RoundedCornerShape(16.dp)
        )
    }
}

// Extension function for tab indicator offset (if not available)
@Composable
private fun Modifier.tabIndicatorOffset(tabPosition: androidx.compose.material3.TabPosition): Modifier {
    return this.then(
        Modifier
            .fillMaxWidth()
            .wrapContentSize(Alignment.BottomStart)
            .offset(x = tabPosition.left)
            .width(tabPosition.width)
    )
} 

