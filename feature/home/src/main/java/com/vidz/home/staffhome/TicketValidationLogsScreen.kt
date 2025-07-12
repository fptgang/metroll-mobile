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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Login
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
    val listState = rememberLazyListState()
    
    // Check if we need to load more items when scrolling near the end
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItemIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            val totalItemsCount = listState.layoutInfo.totalItemsCount
            lastVisibleItemIndex >= totalItemsCount - 3 && uiState.hasMorePages && !uiState.isLoading
        }
    }
    //endregion
    
    //region Event Handler
    val onBackClick: () -> Unit = {
        navController.popBackStack()
    }
    
    val onRefreshClick: () -> Unit = {
        viewModel.onTriggerEvent(TicketValidationLogsViewModel.TicketValidationLogsEvent.RefreshLogs)
    }
    
    val onToggleFilters: () -> Unit = {
        viewModel.onTriggerEvent(TicketValidationLogsViewModel.TicketValidationLogsEvent.ToggleFilters)
    }
    
    val onSearchQueryChange: (String) -> Unit = { query ->
        viewModel.onTriggerEvent(TicketValidationLogsViewModel.TicketValidationLogsEvent.UpdateSearchQuery(query))
    }
    
    val onApplySearch: () -> Unit = {
        viewModel.onTriggerEvent(TicketValidationLogsViewModel.TicketValidationLogsEvent.ApplySearch)
    }
    
    val onSelectValidationType: (ValidationType?) -> Unit = { type ->
        viewModel.onTriggerEvent(TicketValidationLogsViewModel.TicketValidationLogsEvent.SelectValidationType(type))
    }
    
    val onClearFilters: () -> Unit = {
        viewModel.onTriggerEvent(TicketValidationLogsViewModel.TicketValidationLogsEvent.ClearFilters)
    }
    //endregion

    LaunchedEffect(Unit) {
        viewModel.onTriggerEvent(TicketValidationLogsViewModel.TicketValidationLogsEvent.RefreshLogs)
    }
    
    // Load more items when scrolling near the end
    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore) {
            viewModel.onTriggerEvent(TicketValidationLogsViewModel.TicketValidationLogsEvent.LoadMoreLogs)
        }
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
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (uiState.assignedStationId.isNotBlank()) {
                                Text(
                                    text = "Station: ${uiState.assignedStationId}",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            if (uiState.totalElements > 0) {
                                Text(
                                    text = "• ${uiState.totalElements} total",
                                    style = MaterialTheme.typography.labelMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
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
                    IconButton(onClick = onToggleFilters) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Toggle Filters",
                            tint = if (uiState.showFilters) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }
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
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Filters Section
            if (uiState.showFilters) {
                FilterSection(
                    searchQuery = uiState.searchQuery,
                    selectedValidationType = uiState.selectedValidationType,
                    onSearchQueryChange = onSearchQueryChange,
                    onApplySearch = onApplySearch,
                    onSelectValidationType = onSelectValidationType,
                    onClearFilters = onClearFilters,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
            
            // Content Section
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .weight(1f)
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
                            state = listState,
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
                            
                            // Loading more indicator
                            if (uiState.isLoading && uiState.ticketValidations.isNotEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator(
                                            modifier = Modifier.size(24.dp),
                                            color = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                }
                            }
                            
                            // End of list indicator
                            if (!uiState.hasMorePages && uiState.ticketValidations.isNotEmpty()) {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(
                                            text = "End of logs • ${uiState.ticketValidations.size} of ${uiState.totalElements} shown",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
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
    }
    //endregion
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun FilterSection(
    searchQuery: String,
    selectedValidationType: ValidationType?,
    onSearchQueryChange: (String) -> Unit,
    onApplySearch: () -> Unit,
    onSelectValidationType: (ValidationType?) -> Unit,
    onClearFilters: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                label = { Text("Search tickets...") },
                placeholder = { Text("Enter ticket ID or number") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search"
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotBlank()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear search"
                            )
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Filter chips
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Type:",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                FilterChip(
                    selected = selectedValidationType == null,
                    onClick = { onSelectValidationType(null) },
                    label = { Text("All") }
                )
                
                FilterChip(
                    selected = selectedValidationType == ValidationType.ENTRY,
                    onClick = { onSelectValidationType(ValidationType.ENTRY) },
                    label = { Text("Entry") }
                )
                
                FilterChip(
                    selected = selectedValidationType == ValidationType.EXIT,
                    onClick = { onSelectValidationType(ValidationType.EXIT) },
                    label = { Text("Exit") }
                )
            }
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Button(
                    onClick = onApplySearch,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Apply Search")
                }
                
                OutlinedButton(
                    onClick = onClearFilters,
                    modifier = Modifier.weight(1f)
                ) {
                    Text("Clear All")
                }
            }
        }
    }
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Header row with validation type and time
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    ValidationTypeIcon(
                        validationType = validation.validationType,
                        modifier = Modifier.size(32.dp)
                    )
                    
                    Column {
                        Text(
                            text = validation.validationType.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = getValidationTypeColor(validation.validationType)
                        )
                        Text(
                            text = "Station: ${validation.stationId}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = formatTimestamp(validation.validationTime),
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = formatTimestampRelative(validation.validationTime),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Ticket information section
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Ticket Information",
                        style = MaterialTheme.typography.labelMedium,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Ticket ID:",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Text(
                            text = validation.ticketId.takeLast(12),
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    if (validation.validatorId.isNotBlank()) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Validator:",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = validation.validatorId.takeLast(8),
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
            
            // Validation details section
            if (validation.createdAt.isNotBlank()) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Processed: ${formatTimestamp(validation.createdAt)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    // Status indicator
                    Surface(
                        color = getValidationTypeColor(validation.validationType).copy(alpha = 0.1f),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text(
                            text = "✓ Validated",
                            style = MaterialTheme.typography.labelSmall,
                            color = getValidationTypeColor(validation.validationType),
                            fontWeight = FontWeight.Medium,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
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
            modifier = Modifier.padding(6.dp),
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
 * Formats timestamp strings that may come in decimal format (e.g., 1752119620.022000000)
 * to human readable datetime
 */
private fun formatTimestamp(timestamp: String): String {
    return try {
        when {
            // Handle empty or blank strings
            timestamp.isBlank() -> "N/A"
            
            // Handle decimal timestamp format (e.g., 1752119620.022000000)
            timestamp.contains(".") -> {
                val seconds = timestamp.toDouble()
                val milliseconds = (seconds * 1000).toLong()
                val date = java.util.Date(milliseconds)
                val formatter = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
                formatter.format(date)
            }
            
            // Handle scientific notation format (e.g., 1.750908857122E9)
            timestamp.contains("E") -> {
                val milliseconds = timestamp.toDouble().toLong()
                val date = java.util.Date(milliseconds)
                val formatter = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
                formatter.format(date)
            }
            
            // Handle regular milliseconds format
            timestamp.toLongOrNull() != null -> {
                val milliseconds = timestamp.toLong()
                val date = java.util.Date(milliseconds)
                val formatter = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
                formatter.format(date)
            }
            
            // Handle ISO format or other string formats
            else -> {
                val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
                val date = inputFormat.parse(timestamp)
                val outputFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm:ss", java.util.Locale.getDefault())
                date?.let { outputFormat.format(it) } ?: timestamp
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
            
            // Handle decimal timestamp format
            timestamp.contains(".") -> {
                val seconds = timestamp.toDouble()
                val milliseconds = (seconds * 1000).toLong()
                val now = System.currentTimeMillis()
                val diff = now - milliseconds
                
                when {
                    diff < 60_000 -> "Just now"
                    diff < 3_600_000 -> "${diff / 60_000} min ago"
                    diff < 86_400_000 -> "${diff / 3_600_000} hr ago"
                    else -> "${diff / 86_400_000} days ago"
                }
            }
            
            // Handle scientific notation format
            timestamp.contains("E") -> {
                val milliseconds = timestamp.toDouble().toLong()
                val now = System.currentTimeMillis()
                val diff = now - milliseconds
                
                when {
                    diff < 60_000 -> "Just now"
                    diff < 3_600_000 -> "${diff / 60_000} min ago"
                    diff < 86_400_000 -> "${diff / 3_600_000} hr ago"
                    else -> "${diff / 86_400_000} days ago"
                }
            }
            
            // Handle regular milliseconds format
            timestamp.toLongOrNull() != null -> {
                val milliseconds = timestamp.toLong()
                val now = System.currentTimeMillis()
                val diff = now - milliseconds
                
                when {
                    diff < 60_000 -> "Just now"
                    diff < 3_600_000 -> "${diff / 60_000} min ago"
                    diff < 86_400_000 -> "${diff / 3_600_000} hr ago"
                    else -> "${diff / 86_400_000} days ago"
                }
            }
            
            else -> timestamp
        }
    } catch (e: Exception) {
        timestamp
    }
} 