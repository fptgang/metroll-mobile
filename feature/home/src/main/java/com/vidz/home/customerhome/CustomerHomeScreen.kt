package com.vidz.home.customerhome

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.ConfirmationNumber
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.Support
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Train
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vidz.base.components.MetrollActionCard
import com.vidz.base.components.MetrollButton
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.domain.model.Account
import com.vidz.domain.model.Order
import com.vidz.domain.model.OrderDetail
import com.vidz.domain.model.OrderStatus
import com.vidz.domain.model.TicketType
import com.vidz.test.ROOT_TEST_ROUTE

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    onShowSnackbar: ((String) -> Unit)? = null,
    viewModel: CustomerHomeViewModel = hiltViewModel()
) {
    CustomerHomeScreen(
        navController = navController,
        onShowSnackbar = onShowSnackbar ?: {},
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomerHomeScreen(
    navController: NavController,
    onShowSnackbar: (String) -> Unit,
    viewModel: CustomerHomeViewModel
) {
    val haptic = LocalHapticFeedback.current
    
    //region Define Var
    val uiState by viewModel.uiState.collectAsState()
    
    // Primary Transit Actions - Essential metro operations
    val transitActions = listOf(
        TransitAction("Buy Tickets", Icons.Default.ConfirmationNumber, "Purchase metro tickets", DestinationRoutes.TICKET_PURCHASE_SCREEN_ROUTE, true),
        TransitAction("My Tickets", Icons.Default.Badge, "Manage your tickets", DestinationRoutes.MY_TICKETS_SCREEN_ROUTE, true),
        TransitAction("QR Scanner", Icons.Default.QrCodeScanner, "Scan for entry", "qr_scanner", true),
        TransitAction("Route Map", Icons.Default.Map, "Plan your journey", DestinationRoutes.ROUTE_MANAGEMENT_SCREEN_ROUTE, false)
    )
    
    // Service Actions - Additional features
    val serviceActions = listOf(
        ServiceAction("Membership", "Manage your metro card", Icons.Default.CreditCard, DestinationRoutes.MEMBERSHIP_SCREEN_ROUTE),
        ServiceAction("Payment Methods", "Manage payment options", Icons.Default.Wallet, DestinationRoutes.PAYMENT_METHODS_SCREEN_ROUTE),
        ServiceAction("Travel History", "View past journeys", Icons.Default.History, DestinationRoutes.TRAVEL_HISTORY_SCREEN_ROUTE),
        ServiceAction("Support Center", "Get assistance", Icons.Default.Support, "support")
    )
    
    // Sample recent orders for UI display
    val recentOrders = listOf(
        Order(
            id = "ord_001",
            customerId = "customer_123",
            baseTotal = 15.50,
            discountTotal = 0.0,
            finalTotal = 15.50,
            paymentMethod = "Metro Card",
            status = OrderStatus.COMPLETED,
            orderDetails = listOf(
                OrderDetail(
                    id = "detail_001",
                    orderId = "ord_001",
                    ticketType = TicketType.P2P,
                    p2pJourney = "Ben Thanh ↔ Saigon Station",
                    quantity = 1,
                    unitPrice = 15.50,
                    baseTotal = 15.50,
                    discountTotal = 0.0,
                    finalTotal = 15.50,
                    createdAt = java.time.LocalDateTime.now().minusHours(3)
                )
            ),
            createdAt = java.time.LocalDateTime.now().minusHours(3),
            updatedAt = java.time.LocalDateTime.now().minusHours(3)
        ),
        Order(
            id = "ord_002", 
            customerId = "customer_123",
            baseTotal = 50.00,
            discountTotal = 5.00,
            finalTotal = 45.00,
            paymentMethod = "Digital Wallet",
            status = OrderStatus.COMPLETED,
            orderDetails = listOf(
                OrderDetail(
                    id = "detail_002",
                    orderId = "ord_002",
                    ticketType = TicketType.TIMED,
                    timedTicketPlan = "Daily Pass",
                    quantity = 1,
                    unitPrice = 50.00,
                    baseTotal = 50.00,
                    discountTotal = 5.00,
                    finalTotal = 45.00,
                    createdAt = java.time.LocalDateTime.now().minusDays(1)
                )
            ),
            createdAt = java.time.LocalDateTime.now().minusDays(1),
            updatedAt = java.time.LocalDateTime.now().minusDays(1)
        )
    )
    
    // Metro System Updates - Transit-specific announcements
    val systemUpdates = listOf(
        SystemUpdate("Line 1 Operating Normally", "All stations accessible", Icons.Default.Train, true),
        SystemUpdate("Weekend Schedule", "Extended hours this weekend", Icons.Default.Schedule, false),
        SystemUpdate("Mobile Ticketing", "New QR code features available", Icons.Default.Smartphone, false)
    )
    //endregion
    
    //region Event Handler
    val onActionClick: (String) -> Unit = { route ->
        try {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            when (route) {
                DestinationRoutes.TICKET_PURCHASE_SCREEN_ROUTE -> navController.navigate(DestinationRoutes.TICKET_PURCHASE_SCREEN_ROUTE)
                DestinationRoutes.MY_TICKETS_SCREEN_ROUTE -> navController.navigate(DestinationRoutes.MY_TICKETS_SCREEN_ROUTE)
                DestinationRoutes.ROUTE_MANAGEMENT_SCREEN_ROUTE -> navController.navigate(DestinationRoutes.ROUTE_MANAGEMENT_SCREEN_ROUTE)
                DestinationRoutes.MEMBERSHIP_SCREEN_ROUTE -> navController.navigate(DestinationRoutes.MEMBERSHIP_SCREEN_ROUTE)
                DestinationRoutes.PAYMENT_METHODS_SCREEN_ROUTE -> navController.navigate(DestinationRoutes.PAYMENT_METHODS_SCREEN_ROUTE)
                DestinationRoutes.TRAVEL_HISTORY_SCREEN_ROUTE -> navController.navigate(DestinationRoutes.TRAVEL_HISTORY_SCREEN_ROUTE)
                "qr_scanner" -> navController.navigate("qr_scanner")
                "support" -> onShowSnackbar("🎧 Support chat will be available soon!")
                ROOT_TEST_ROUTE -> navController.navigate(ROOT_TEST_ROUTE)
                else -> onShowSnackbar("🚧 Feature coming soon!")
            }
        } catch (e: Exception) {
            onShowSnackbar("❌ Navigation error: ${e.message}")
        }
    }
    
    val onProfileClick: () -> Unit = {
        try {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            navController.navigate(DestinationRoutes.ACCOUNT_PROFILE_SCREEN_ROUTE)
        } catch (e: Exception) {
            onShowSnackbar("❌ Unable to open profile: ${e.message}")
        }
    }
    
    val onNotificationClick: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        onShowSnackbar("🔔 No new notifications")
    }
    
    val onLogoutClick: () -> Unit = {
        if (!uiState.isLoggingOut) {
            try {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.onTriggerEvent(CustomerHomeViewModel.CustomerHomeEvent.LogoutClicked)
            } catch (e: Exception) {
                onShowSnackbar("❌ Logout failed: ${e.message}")
            }
        }
    }
    //endregion
    
    // Handle logout success
    LaunchedEffect(uiState.logoutSuccessful) {
        if (uiState.logoutSuccessful) {
            try {
                navController.navigate(DestinationRoutes.ROOT_AUTH_SCREEN_ROUTE) {
                    popUpTo(0) { inclusive = true }
                }
                viewModel.onTriggerEvent(CustomerHomeViewModel.CustomerHomeEvent.LogoutSuccessAcknowledged)
                onShowSnackbar("✅ Successfully logged out")
            } catch (e: Exception) {
                onShowSnackbar("❌ Logout navigation failed: ${e.message}")
            }
        }
    }
    
    // Handle error messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            onShowSnackbar(message)
            viewModel.onTriggerEvent(CustomerHomeViewModel.CustomerHomeEvent.DismissSnackbar)
        }
    }
    
    //region ui
    Scaffold {innerPadding->
        LazyColumn(
            modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                bottom = 56.dp + innerPadding.calculateBottomPadding()
            )
        ) {
            // Professional Metro App Bar
            item {
                MetroTopAppBar(
                    customerName = uiState.localAccount?.fullName ?: "Welcome",
                    isLoggedIn = uiState.isLoggedIn,
                    isLoggingOut = uiState.isLoggingOut,
                    onProfileClick = onProfileClick,
                    onNotificationClick = onNotificationClick,
                    onLogoutClick = onLogoutClick
                )
            }

            // User Welcome Card
            item {
                MetroWelcomeCard(
                    account = uiState.localAccount,
                    isLoggedIn = uiState.isLoggedIn
                )
            }

            // Primary Transit Actions
            item {
                Spacer(modifier = Modifier.height(24.dp))
                MetroSectionHeader("Quick Actions")
                Spacer(modifier = Modifier.height(16.dp))

                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier
                            .height(240.dp)
                            .padding(horizontal = 20.dp)
                ) {
                    items(transitActions) { action ->
                        TransitActionCard(
                            action = action,
                            onClick = { onActionClick(action.route) }
                        )
                    }
                }
            }

            // Recent Travel History
            if (recentOrders.isNotEmpty()) {
                item {
                    Spacer(modifier = Modifier.height(24.dp))
                    MetroSectionHeader("Recent Journeys")
                    Spacer(modifier = Modifier.height(16.dp))

                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(16.dp),
                        contentPadding = PaddingValues(horizontal = 20.dp)
                    ) {
                        items(recentOrders) { order ->
                            RecentJourneyCard(
                                order = order,
                                onClick = { onShowSnackbar("📋 Journey: ${order.toJourneyDisplayText()}") }
                            )
                        }
                    }
                }
            }

            // Service Features
            item {
                Spacer(modifier = Modifier.height(24.dp))
                MetroSectionHeader("Services")
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    serviceActions.forEach { action ->
                        MetrollActionCard(
                            title = action.title,
                            description = action.description,
                            icon = action.icon,
                            onClick = { onActionClick(action.route) }
                        )
                    }
                }
            }

            // System Updates
            item {
                Spacer(modifier = Modifier.height(24.dp))
                MetroSectionHeader("System Updates")
                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.padding(horizontal = 20.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    systemUpdates.forEach { update ->
                        SystemUpdateCard(
                            update = update,
                            onClick = { onShowSnackbar("📢 ${update.title}") }
                        )
                    }
                }
            }

            // Emergency Contact
            item {
                Spacer(modifier = Modifier.height(24.dp))
                EmergencyContactCard(
                    onContactClick = { onShowSnackbar("🚨 Emergency services: 115") }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
    //endregion
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MetroTopAppBar(
    customerName: String,
    isLoggedIn: Boolean,
    isLoggingOut: Boolean,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "HCMC Metro",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                if (isLoggedIn) {
                    Text(
                        text = "Transit System",
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        },
        navigationIcon = {
            IconButton(onClick = onProfileClick) {
                Surface(
                    modifier = Modifier.size(36.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = if (isLoggedIn) Icons.Default.AccountCircle else Icons.Default.Person,
                        contentDescription = "Profile",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(6.dp)
                    )
                }
            }
        },
        actions = {
            // Notifications
            IconButton(onClick = onNotificationClick) {
                BadgedBox(
                    badge = {
                        Badge(
                            containerColor = MaterialTheme.colorScheme.error,
                            modifier = Modifier.size(6.dp)
                        )
                    }
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
            
            // Logout
            if (isLoggedIn) {
                IconButton(
                    onClick = onLogoutClick,
                    enabled = !isLoggingOut
                ) {
                    Surface(
                        modifier = Modifier.size(36.dp),
                        color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                        shape = CircleShape
                    ) {
                        if (isLoggingOut) {
                            CircularProgressIndicator(
                                modifier = Modifier.padding(6.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Logout",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(6.dp)
                            )
                        }
                    }
                }
            }
        },
        colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface,
            actionIconContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
    )
}

@Composable
private fun MetroWelcomeCard(
    account: Account?,
    isLoggedIn: Boolean
) {
    Card(
        modifier = Modifier
                .fillMaxWidth()
                .padding(
                    horizontal = 20.dp,
                    vertical = 8.dp
                ),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // User Avatar
            Surface(
                modifier = Modifier.size(56.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = if (isLoggedIn) Icons.Default.AccountCircle else Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier.padding(12.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            // Welcome Text
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isLoggedIn) "Welcome back," else "Welcome to",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                )
                
                Text(
                    text = account?.fullName ?: "HCMC Metro",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Text(
                    text = if (isLoggedIn) "🚇 Ready to travel?" else "🎫 Please log in to continue",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
        }
    }
}

@Composable
private fun MetroSectionHeader(title: String) {
    Row(
        modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                    .size(4.dp)
                    .background(
                        MaterialTheme.colorScheme.primary,
                        CircleShape
                    )
        )
        
        Spacer(modifier = Modifier.width(12.dp))
        
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun TransitActionCard(
    action: TransitAction,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.95f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "action_card_scale"
    )
    
    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
                .fillMaxWidth()
                .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = if (action.isPrimary) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (action.isPrimary) 6.dp else 2.dp
        ),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                color = if (action.isPrimary) {
                    MaterialTheme.colorScheme.primary.copy(alpha = 0.2f)
                } else {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)
                },
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = action.icon,
                    contentDescription = action.title,
                    modifier = Modifier.padding(8.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Text(
                text = action.title,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            
            Text(
                text = action.description,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecentJourneyCard(
    order: Order,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.width(220.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Surface(
                    modifier = Modifier.size(32.dp),
                    color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                    shape = CircleShape
                ) {
                    Icon(
                        imageVector = order.getJourneyIcon(),
                        contentDescription = null,
                        modifier = Modifier.padding(6.dp),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = order.toJourneyDisplayText(),
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = "₫${String.format("%.0f", order.finalTotal)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
            
            Surface(
                color = when (order.status) {
                    OrderStatus.COMPLETED -> MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                    OrderStatus.PENDING -> MaterialTheme.colorScheme.secondary.copy(alpha = 0.1f)
                    OrderStatus.FAILED -> MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
                },
                shape = RoundedCornerShape(6.dp)
            ) {
                Text(
                    text = order.getDisplayStatus(),
                    style = MaterialTheme.typography.labelSmall,
                    color = when (order.status) {
                        OrderStatus.COMPLETED -> MaterialTheme.colorScheme.primary
                        OrderStatus.PENDING -> MaterialTheme.colorScheme.secondary
                        OrderStatus.FAILED -> MaterialTheme.colorScheme.error
                    },
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SystemUpdateCard(
    update: SystemUpdate,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (update.isImportant) {
                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (update.isImportant) 4.dp else 1.dp
        ),
        shape = RoundedCornerShape(8.dp)
    ) {
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(32.dp),
                color = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = update.icon,
                    contentDescription = null,
                    modifier = Modifier.padding(6.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = update.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = update.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            if (update.isImportant) {
                Surface(
                    modifier = Modifier.size(8.dp),
                    color = MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                ) {}
            }
        }
    }
}

@Composable
private fun EmergencyContactCard(
    onContactClick: () -> Unit
) {
    Card(
        modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            MaterialTheme.colorScheme.error.copy(alpha = 0.3f)
        )
    ) {
        Row(
            modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                modifier = Modifier.size(40.dp),
                color = MaterialTheme.colorScheme.error.copy(alpha = 0.1f),
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Support,
                    contentDescription = "Emergency",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Emergency Assistance",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.error
                )
                
                Text(
                    text = "24/7 support for urgent matters",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            MetrollButton(
                text = "Contact",
                onClick = onContactClick,
                modifier = Modifier.width(100.dp)
            )
        }
    }
}

// Data classes for UI display
data class TransitAction(
    val title: String,
    val icon: ImageVector,
    val description: String,
    val route: String,
    val isPrimary: Boolean = false
)

data class ServiceAction(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val route: String
)

data class SystemUpdate(
    val title: String,
    val description: String,
    val icon: ImageVector,
    val isImportant: Boolean = false
)

// Extension functions for domain models
fun Order.toJourneyDisplayText(): String {
    val orderDetails = this.orderDetails.firstOrNull()
    return when {
        orderDetails?.p2pJourney != null -> orderDetails.p2pJourney ?: "P2P Journey"
        orderDetails?.timedTicketPlan != null -> orderDetails.timedTicketPlan ?: "Timed Pass"
        else -> "Metro Journey"
    }
}

fun Order.getJourneyIcon(): ImageVector = when {
    this.orderDetails.any { it.ticketType == TicketType.P2P } -> Icons.Default.Train
    this.orderDetails.any { it.ticketType == TicketType.TIMED } -> Icons.Default.Timer
    else -> Icons.Default.ConfirmationNumber
}

fun Order.getDisplayStatus(): String = when (this.status) {
    OrderStatus.COMPLETED -> "Completed"
    OrderStatus.PENDING -> "Pending"
    OrderStatus.FAILED -> "Failed"
} 
