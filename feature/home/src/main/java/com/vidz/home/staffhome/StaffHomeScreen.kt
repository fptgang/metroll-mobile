package com.vidz.home.staffhome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.WavingHand
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vidz.base.navigation.DestinationRoutes

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffHomeScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    onShowSnackbar: ((String) -> Unit)? = null,
    viewModel: StaffHomeViewModel = hiltViewModel()
) {
    StaffHomeScreen(
        navController = navController,
        onShowSnackbar = onShowSnackbar ?: {},
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffHomeScreen(
    navController: NavController,
    onShowSnackbar: (String) -> Unit,
    viewModel: StaffHomeViewModel
) {
    val haptic = LocalHapticFeedback.current
    
    //region Define Var
    val uiState = viewModel.uiState.collectAsState().value
    var showSettingsBottomSheet by remember { mutableStateOf(false) }
    val bottomSheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true
    )
    //endregion
    
    //region Event Handler
    val onQRScannerClick: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        navController.navigate(DestinationRoutes.QR_SCANNER_SCREEN_ROUTE)
    }
    
    val onBuyTicketClick: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        navController.navigate(DestinationRoutes.ROOT_TICKET_SCREEN_ROUTE)
    }
    
    val onViewTicketsClick: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        navController.navigate(DestinationRoutes.MY_TICKETS_SCREEN_ROUTE)
    }
    
    val onSettingsClick: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        showSettingsBottomSheet = true
    }
    
    val onViewProfileClick: () -> Unit = {
        showSettingsBottomSheet = false
        navController.navigate(DestinationRoutes.STAFF_PROFILE_SCREEN_ROUTE)
    }
    
    val onEditProfileClick: () -> Unit = {
        showSettingsBottomSheet = false
        navController.navigate(DestinationRoutes.EDIT_PROFILE_SCREEN_ROUTE)
    }
    
    val onViewLogsClick: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        navController.navigate(DestinationRoutes.STAFF_SCAN_HISTORY_SCREEN_ROUTE)
    }
    
    val onLogoutClick: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        viewModel.onTriggerEvent(StaffHomeViewModel.StaffHomeEvent.LogoutClicked)
    }
    //endregion
    
    // Handle logout success - navigate to auth screen when logout is complete
    LaunchedEffect(uiState.logoutSuccessful) {
        if (uiState.logoutSuccessful) {
            navController.navigate(DestinationRoutes.ROOT_AUTH_SCREEN_ROUTE) {
                popUpTo(0) { inclusive = true }
            }
            viewModel.onTriggerEvent(StaffHomeViewModel.StaffHomeEvent.LogoutSuccessAcknowledged)
        }
    }
    
    // Handle error messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            onShowSnackbar(message)
            viewModel.onTriggerEvent(StaffHomeViewModel.StaffHomeEvent.DismissSnackbar)
        }
    }
    
    //region UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.05f),
                        MaterialTheme.colorScheme.background
                    )
                )
            )
    ) {
        // Top App Bar
        CenterAlignedTopAppBar(
            title = {
                Text(
                    text = "Metroll Staff",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
            },
            actions = {
                IconButton(onClick = onLogoutClick) {
                    Icon(
                        imageVector = Icons.Default.Logout,
                        contentDescription = "Logout",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            },
            colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                containerColor = Color.Transparent
            )
        )
        
        // Content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Welcome Message Card
            WelcomeMessageCard(
                staffName = uiState.staffName,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 24.dp)
            )
            
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Scan Ticket Button
                com.vidz.base.components.MetrollActionCard(
                    title = "Scan Ticket",
                    description = "Scan passenger tickets and passes",
                    icon = Icons.Default.QrCodeScanner,
                    onClick = onQRScannerClick,
                    isPrimary = true
                )

                // View Validation Logs Button
                com.vidz.base.components.MetrollActionCard(
                    title = "Validation Logs",
                    description = "View ticket validation history for your station",
                    icon = Icons.Default.History,
                    onClick = onViewLogsClick,
                    isPrimary = false
                )

                // Buy Ticket for Guest Button
                com.vidz.base.components.MetrollActionCard(
                    title = "Buy Ticket for Guest",
                    description = "Purchase tickets on behalf of passengers",
                    icon = Icons.Default.ShoppingCart,
                    onClick = onBuyTicketClick,
                    isPrimary = false
                )

                // View Tickets Button
                com.vidz.base.components.MetrollActionCard(
                    title = "View Tickets",
                    description = "View and manage ticket orders",
                    icon = Icons.Default.Receipt,
                    onClick = onViewTicketsClick,
                    isPrimary = false
                )

                // Profile & Settings Button
                com.vidz.base.components.MetrollActionCard(
                    title = "Tài khoản và cài đặt",
                    description = "Quản lí tài khoản của bạn",
                    icon = Icons.Default.Settings,
                    onClick = onSettingsClick,
                    isPrimary = false
                )
            }
        }
        
        //registration Dialog and Sheet
        // Settings Bottom Sheet
        if (showSettingsBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = { showSettingsBottomSheet = false },
                sheetState = bottomSheetState,
                dragHandle = { BottomSheetDefaults.DragHandle() }
            ) {
                StaffSettingsBottomSheetContent(
                    staffName = uiState.staffName,
                    onViewProfile = onViewProfileClick,
                    onEditProfile = onEditProfileClick,
                    onLogout = onLogoutClick,
                    isLoggingOut = uiState.isLoggingOut,
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
        //endregion
    }
    //endregion
}

@Composable
private fun StaffSettingsBottomSheetContent(
    staffName: String,
    onViewProfile: () -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    isLoggingOut: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = "Tùy chọn",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = "Chào mừng, $staffName",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
        
        // Profile Actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Button(
                onClick = onViewProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thông tin cá nhân")
            }
            
            OutlinedButton(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chỉnh sửa thông tin")
            }
        }
        
        // Logout Section
        OutlinedButton(
            onClick = onLogout,
            enabled = !isLoggingOut,
            colors = androidx.compose.material3.ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.error
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (isLoggingOut) {
                androidx.compose.material3.CircularProgressIndicator(
                    modifier = Modifier.size(18.dp),
                    strokeWidth = 2.dp,
                    color = MaterialTheme.colorScheme.error
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đang đăng xuất...")
            } else {
                Icon(
                    imageVector = Icons.Default.Logout,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Đăng xuất")
            }
        }
    }
}

@Composable
private fun WelcomeMessageCard(
    staffName: String,
    modifier: Modifier = Modifier
) {
    // Get current time for greeting
    val currentHour = remember { java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY) }
    val greeting = when (currentHour) {
        in 5..11 -> "Chào buổi sáng"
        in 12..16 -> "Chào buổi chiều"
        in 17..21 -> "Chào buổi tối"
        else -> "Chào buổi tối"
    }
    
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.WavingHand,
                contentDescription = "Chào mừng",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(32.dp)
            )
            
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = "$greeting, $staffName!",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                Text(
                    text = "Chúc bạn một ngày làm việc vui vẻ",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                )
            }
        }
    }
}
