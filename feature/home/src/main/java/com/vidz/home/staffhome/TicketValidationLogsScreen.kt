package com.vidz.home.staffhome

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Refresh
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vidz.base.extensions.toFormattedDate
import com.vidz.base.extensions.toRelativeTime
import com.vidz.domain.model.TicketValidation
import com.vidz.domain.model.ValidationType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketValidationLogsScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    onShowSnackbar: ((String) -> Unit)? = null,
    viewModel: TicketValidationLogsViewModel = hiltViewModel()
) {
    TicketValidationLogsScreen(
        navController = navController,
        onShowSnackbar = onShowSnackbar ?: {},
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketValidationLogsScreen(
    navController: NavController,
    onShowSnackbar: (String) -> Unit,
    viewModel: TicketValidationLogsViewModel
) {
    //region Define Var
    val uiState = viewModel.uiState.collectAsState().value
    //endregion
    
    //region Event Handler
    val onBackClick: () -> Unit = {
        navController.popBackStack()
    }
    
    val onRefreshClick: () -> Unit = {
        viewModel.onTriggerEvent(TicketValidationLogsViewModel.TicketValidationLogsEvent.RefreshLogs)
    }
    //endregion

    LaunchedEffect(Unit) {
        viewModel.onTriggerEvent(TicketValidationLogsViewModel.TicketValidationLogsEvent.RefreshLogs)
    }
    
    // Handle error messages
    LaunchedEffect(uiState.snackbarMessage) {
        uiState.snackbarMessage?.let { message ->
            onShowSnackbar(message)
            viewModel.onTriggerEvent(TicketValidationLogsViewModel.TicketValidationLogsEvent.DismissSnackbar)
        }
    }
    
    //region UI
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Validation Logs",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                        if (uiState.assignedStationId.isNotBlank()) {
                            Text(
                                text = "Station: ${uiState.assignedStationId}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            // Test button - force load with test station
                            viewModel.onTriggerEvent(TicketValidationLogsViewModel.TicketValidationLogsEvent.ForceLoadWithTestStation)
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Test Load",
                            tint = MaterialTheme.colorScheme.secondary
                        )
                    }
                    IconButton(onClick = onRefreshClick) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            when {
                uiState.isLoading && uiState.ticketValidations.isEmpty() -> {
                    // Show loading state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "Loading validation logs...",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
                
                uiState.error != null && uiState.ticketValidations.isEmpty() -> {
                    // Show error state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = "Error",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.error
                            )
                            Text(
                                text = "Failed to load logs",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = uiState.error,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                uiState.ticketValidations.isEmpty() -> {
                    // Show empty state
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            modifier = Modifier.padding(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "No logs",
                                modifier = Modifier.size(64.dp),
                                tint = MaterialTheme.colorScheme.primary
                            )
                            Text(
                                text = "No validation logs yet",
                                style = MaterialTheme.typography.headlineSmall,
                                color = MaterialTheme.colorScheme.onSurface,
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "Ticket validations will appear here as passengers scan their tickets at your station.",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }
                
                else -> {
                    // Show ticket validation logs
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = androidx.compose.foundation.layout.PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(uiState.ticketValidations) { validation ->
                            TicketValidationLogItem(
                                validation = validation,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        
                        // Add some bottom spacing
                        item {
                            Spacer(modifier = Modifier.height(16.dp))
                        }
                    }
                }
            }
            

        }
    }
    //endregion
}

@Composable
private fun TicketValidationLogItem(
    validation: TicketValidation,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Validation type icon
            ValidationTypeIcon(
                validationType = validation.validationType,
                modifier = Modifier.size(40.dp)
            )
            
            // Validation details
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = validation.validationType.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = getValidationTypeColor(validation.validationType)
                    )
                    
                    Column(
                        horizontalAlignment = Alignment.End
                    ) {
                        Text(
                            text = formatTimestamp(validation.validationTime),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = formatTimestampRelative(validation.validationTime),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
                
                Text(
                    text = "Ticket: ${validation.ticketId.take(8)}...",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (validation.deviceId.isNotBlank()) {
                        Text(
                            text = "Device: ${validation.deviceId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    
                    if (validation.createdAt.isNotBlank()) {
                        Text(
                            text = "Created: ${formatTimestamp(validation.createdAt)}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ValidationTypeIcon(
    validationType: ValidationType,
    modifier: Modifier = Modifier
) {
    val (icon, backgroundColor) = when (validationType) {
        ValidationType.ENTRY -> Icons.Default.Login to MaterialTheme.colorScheme.primaryContainer
        ValidationType.EXIT -> Icons.Default.ExitToApp to MaterialTheme.colorScheme.tertiaryContainer
    }
    
    Surface(
        modifier = modifier,
        color = backgroundColor,
        shape = RoundedCornerShape(8.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = validationType.name,
            modifier = Modifier.padding(8.dp),
            tint = getValidationTypeColor(validationType)
        )
    }
}

@Composable
private fun getValidationTypeColor(validationType: ValidationType): Color {
    return when (validationType) {
        ValidationType.ENTRY -> MaterialTheme.colorScheme.primary
        ValidationType.EXIT -> MaterialTheme.colorScheme.tertiary
    }
}

/**
 * Formats timestamp strings that may come in scientific notation format (e.g., 1.750908857122E9)
 * or regular milliseconds format to human readable datetime
 */
private fun formatTimestamp(timestamp: String): String {
    return try {
        when {
            // Handle empty or blank strings
            timestamp.isBlank() -> "N/A"
            
            // Handle scientific notation format (e.g., 1.750908857122E9)
            timestamp.contains("E") -> {
                val milliseconds = timestamp.toDouble().toLong()
                milliseconds.toString().toFormattedDate("dd/MM/yyyy HH:mm")
            }
            
            // Handle regular milliseconds format
            timestamp.toLongOrNull() != null -> {
                timestamp.toFormattedDate("dd/MM/yyyy HH:mm")
            }
            
            // Handle ISO format or other string formats
            else -> {
                val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
                val date = inputFormat.parse(timestamp)
                date?.time?.toString()?.toFormattedDate("dd/MM/yyyy HH:mm") ?: timestamp
            }
        }
    } catch (e: Exception) {
        // Fallback to original string if parsing fails
        timestamp
    }
}

/**
 * Formats timestamp for relative time display (e.g., "2 hours ago")
 */
private fun formatTimestampRelative(timestamp: String): String {
    return try {
        when {
            timestamp.isBlank() -> "N/A"
            
            // Handle scientific notation format
            timestamp.contains("E") -> {
                val milliseconds = timestamp.toDouble().toLong()
                milliseconds.toString().toRelativeTime()
            }
            
            // Handle regular milliseconds format
            timestamp.toLongOrNull() != null -> {
                timestamp.toRelativeTime()
            }
            
            else -> timestamp
        }
    } catch (e: Exception) {
        timestamp
    }
} 