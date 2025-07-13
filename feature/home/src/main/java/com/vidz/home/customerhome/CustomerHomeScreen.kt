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
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
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
import androidx.compose.ui.graphics.Color
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
        TransitAction("Mua Vé", Icons.Default.ConfirmationNumber, "Mua vé tàu điện ngầm", DestinationRoutes.TICKET_PURCHASE_SCREEN_ROUTE, true),
        TransitAction("Vé Của Tôi", Icons.Default.Badge, "Quản lý vé của bạn", DestinationRoutes.MY_TICKETS_SCREEN_ROUTE, true),
        TransitAction("Bản Đồ Tuyến", Icons.Default.Map, "Lập kế hoạch hành trình", DestinationRoutes.ROUTE_MANAGEMENT_SCREEN_ROUTE, false)
    )
    
    // Service Actions - Additional features
    val serviceActions = listOf(
        ServiceAction("Thành Viên", "Quản lý thẻ tàu điện ngầm", Icons.Default.CreditCard, DestinationRoutes.MEMBERSHIP_SCREEN_ROUTE),
        ServiceAction("Phương Thức Thanh Toán", "Quản lý tùy chọn thanh toán", Icons.Default.Wallet, DestinationRoutes.PAYMENT_METHODS_SCREEN_ROUTE),
        ServiceAction("Lịch Sử Di Chuyển", "Xem các chuyến đi trước", Icons.Default.History, DestinationRoutes.TRAVEL_HISTORY_SCREEN_ROUTE),
        ServiceAction("Trung Tâm Hỗ Trợ", "Nhận hỗ trợ", Icons.Default.Support, "support")
    )
    

    
    // Metro System Updates - Transit-specific announcements
    val systemUpdates = listOf(
        SystemUpdate("Tuyến 1 Hoạt Động Bình Thường", "Tất cả các ga đều có thể truy cập", Icons.Default.Train, true),
        SystemUpdate("Lịch Cuối Tuần", "Giờ hoạt động kéo dài vào cuối tuần này", Icons.Default.Schedule, false),
        SystemUpdate("Vé Điện Tử", "Tính năng mã QR mới đã có sẵn", Icons.Default.Smartphone, false)
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
                "support" -> onShowSnackbar("🎧 Chat hỗ trợ sẽ có sẵn sớm!")
                ROOT_TEST_ROUTE -> navController.navigate(ROOT_TEST_ROUTE)
                else -> onShowSnackbar("🚧 Tính năng sắp ra mắt!")
            }
        } catch (e: Exception) {
            onShowSnackbar("❌ Lỗi điều hướng: ${e.message}")
        }
    }
    
    val onProfileClick: () -> Unit = {
        try {
            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
            navController.navigate(DestinationRoutes.ACCOUNT_PROFILE_SCREEN_ROUTE)
        } catch (e: Exception) {
            onShowSnackbar("❌ Không thể mở hồ sơ: ${e.message}")
        }
    }
    
    val onNotificationClick: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        onShowSnackbar("🔔 Không có thông báo mới")
    }
    
    val onLogoutClick: () -> Unit = {
        if (!uiState.isLoggingOut) {
            try {
                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                viewModel.onTriggerEvent(CustomerHomeViewModel.CustomerHomeEvent.LogoutClicked)
            } catch (e: Exception) {
                onShowSnackbar("❌ Đăng xuất thất bại: ${e.message}")
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
                onShowSnackbar("✅ Đăng xuất thành công")
            } catch (e: Exception) {
                onShowSnackbar("❌ Điều hướng đăng xuất thất bại: ${e.message}")
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
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background, modifier = Modifier.padding(top = WindowInsets.statusBars.asPaddingValues().calculateTopPadding())
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentPadding = PaddingValues(
                bottom = 56.dp + innerPadding.calculateBottomPadding()
            ),
            verticalArrangement = Arrangement.spacedBy(0.dp)
        ) {
            // Modern Metro App Bar
            item {
                ModernMetroTopAppBar(
                    customerName = uiState.localAccount?.fullName ?: "Chào mừng",
                    isLoggedIn = uiState.isLoggedIn,
                    isLoggingOut = uiState.isLoggingOut,
                    onProfileClick = onProfileClick,
                    onNotificationClick = onNotificationClick,
                    onLogoutClick = onLogoutClick
                )
            }

            // Modern Welcome Section
            item {
                ModernWelcomeSection(
                    account = uiState.localAccount,
                    isLoggedIn = uiState.isLoggedIn
                )
            }

            // Primary Transit Actions
            item {
                ModernActionSection(
                    title = "Thao Tác Nhanh",
                    actions = transitActions,
                    onActionClick = onActionClick
                )
            }
        }
    }
    //endregion
}

@Composable
private fun ModernMetroTopAppBar(
    customerName: String,
    isLoggedIn: Boolean,
    isLoggingOut: Boolean,
    onProfileClick: () -> Unit,
    onNotificationClick: () -> Unit,
    onLogoutClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Profile Icon
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.08f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier.size(44.dp)
                ) {
                    Icon(
                        imageVector = if (isLoggedIn) Icons.Default.AccountCircle else Icons.Default.Person,
                        contentDescription = "Hồ sơ",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            // Title Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Metro HCM",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "Hệ Thống Giao Thông",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }

            // Logout Button
            if (isLoggedIn) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .background(
                            color = MaterialTheme.colorScheme.error.copy(alpha = 0.08f),
                            shape = CircleShape
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    IconButton(
                        onClick = onLogoutClick,
                        enabled = !isLoggingOut,
                        modifier = Modifier.size(44.dp)
                    ) {
                        if (isLoggingOut) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Default.Logout,
                                contentDescription = "Đăng xuất",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }
            } else {
                Spacer(modifier = Modifier.size(44.dp))
            }
        }
    }
}

@Composable
private fun ModernWelcomeSection(
    account: Account?,
    isLoggedIn: Boolean
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 20.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Welcome Text
            Column(
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = if (isLoggedIn) "Chào mừng trở lại" else "Chào mừng đến với Metro",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
                
                Text(
                    text = account?.fullName ?: "Khách hàng",
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            // Status Indicator
            Box(
                modifier = Modifier
                    .background(
                        color = if (isLoggedIn) 
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.08f) 
                        else 
                            MaterialTheme.colorScheme.outline.copy(alpha = 0.08f),
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 16.dp, vertical = 12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (isLoggedIn) 
                                    MaterialTheme.colorScheme.primary 
                                else 
                                    MaterialTheme.colorScheme.outline,
                                shape = CircleShape
                            )
                    )
                    
                    Text(
                        text = if (isLoggedIn) "Sẵn sàng di chuyển" else "Vui lòng đăng nhập để tiếp tục",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                    )
                }
            }
        }
    }
}

@Composable
private fun ModernActionSection(
    title: String,
    actions: List<TransitAction>,
    onActionClick: (String) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
            .padding(top = 8.dp)
    ) {
        // Section Header
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(horizontal = 24.dp, vertical = 20.dp)
        )

        // Actions Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp),
            modifier = Modifier.height(280.dp)
        ) {
            items(actions) { action ->
                ModernTransitActionCard(
                    action = action,
                    onClick = { onActionClick(action.route) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModernTransitActionCard(
    action: TransitAction,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.96f else 1f,
        animationSpec = spring(dampingRatio = Spring.DampingRatioMediumBouncy),
        label = "action_card_scale"
    )
    
    Card(
        onClick = onClick,
        interactionSource = interactionSource,
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .scale(scale),
        colors = CardDefaults.cardColors(
            containerColor = if (action.isPrimary) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.surface
            }
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(20.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .background(
                            color = if (action.isPrimary) {
                                Color.White.copy(alpha = 0.15f)
                            } else {
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.08f)
                            },
                            shape = RoundedCornerShape(8.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = action.icon,
                        contentDescription = action.title,
                        tint = if (action.isPrimary) {
                            Color.White
                        } else {
                            MaterialTheme.colorScheme.primary
                        },
                        modifier = Modifier.size(18.dp)
                    )
                }
                
                // Text Content
                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = action.title,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = if (action.isPrimary) {
                            Color.White
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        },
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                    
                    Text(
                        text = action.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = if (action.isPrimary) {
                            Color.White.copy(alpha = 0.8f)
                        } else {
                            MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        },
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
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
                    contentDescription = "Khẩn cấp",
                    tint = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(8.dp)
                )
            }
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "Hỗ Trợ Khẩn Cấp",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.error
                )
                
                Text(
                    text = "Hỗ trợ 24/7 cho các vấn đề khẩn cấp",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )
            }
            
            MetrollButton(
                text = "Liên hệ",
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
        orderDetails?.p2pJourney != null -> orderDetails.p2pJourney ?: "Chuyến Đi P2P"
        orderDetails?.timedTicketPlan != null -> orderDetails.timedTicketPlan ?: "Vé Theo Giờ"
        else -> "Chuyến Đi Metro"
    }
}

fun Order.getJourneyIcon(): ImageVector = when {
    this.orderDetails.any { it.ticketType == TicketType.P2P } -> Icons.Default.Train
    this.orderDetails.any { it.ticketType == TicketType.TIMED } -> Icons.Default.Timer
    else -> Icons.Default.ConfirmationNumber
}

fun Order.getDisplayStatus(): String = when (this.status) {
    OrderStatus.COMPLETED -> "Hoàn thành"
    OrderStatus.PENDING -> "Đang chờ"
    OrderStatus.FAILED -> "Thất bại"
} 
