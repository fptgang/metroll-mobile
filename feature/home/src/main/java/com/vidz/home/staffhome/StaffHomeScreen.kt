package com.vidz.home.staffhome

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
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
    //endregion
    
    //region Event Handler
    val onQRScannerClick: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        navController.navigate("qr_scanner")
    }
    
    val onSettingsClick: () -> Unit = {
        haptic.performHapticFeedback(HapticFeedbackType.LongPress)
        onShowSnackbar("Settings coming soon!")
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

                // Settings Button
                com.vidz.base.components.MetrollActionCard(
                    title = "Settings",
                    description = "Manage your account and preferences",
                    icon = Icons.Default.Settings,
                    onClick = onSettingsClick,
                    isPrimary = false
                )
            }
        }
    }
    //endregion
}
