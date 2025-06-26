package com.vidz.routemanagement.management

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.base.components.MetroLineItem
import com.vidz.base.components.MetroLineSelector
import com.vidz.domain.model.MetroLine
import com.vidz.routemanagement.presentation.components.MetroLineMapView

@Composable
fun RouteManagementScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    onShowSnackbar: (String) -> Unit,
    routeManagementViewModel: RouteManagementViewModel = hiltViewModel(),
) {
    val routeManagementUiState = routeManagementViewModel.uiState.collectAsStateWithLifecycle()
    
    RouteManagementScreen(
        navController = navController,
        routeManagementUiState = routeManagementUiState,
        onEvent = routeManagementViewModel::onTriggerEvent,
        onShowSnackbar = onShowSnackbar,
        modifier = modifier
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RouteManagementScreen(
    navController: NavController,
    routeManagementUiState: State<RouteManagementViewModel.RouteManagementViewState>,
    onEvent: (RouteManagementViewModel.RouteManagementViewEvent) -> Unit,
    onShowSnackbar: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    //region Define Var
    val uiState = routeManagementUiState.value
    val snackbarHostState = remember { SnackbarHostState() }
    //endregion

    //region Event Handler
    // Show error messages in snackbar
    LaunchedEffect(uiState.error) {
        uiState.error?.let { error ->
            snackbarHostState.showSnackbar(error)
        }
    }

    val handleMetroLineSelected = { metroLineItem: MetroLineItem ->
        val selectedLine = uiState.metroLines.find { it.id == metroLineItem.id }
        if (selectedLine != null) {
            onEvent(RouteManagementViewModel.RouteManagementViewEvent.SelectMetroLine(selectedLine))
        }
    }

    val handleRefresh = {
        onEvent(RouteManagementViewModel.RouteManagementViewEvent.RefreshData)
    }
    //endregion

    //region ui
    Scaffold(
        modifier = modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Route Management",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                    titleContentColor = MaterialTheme.colorScheme.onSurface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = handleRefresh,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Refresh"
                )
            }
        },
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            if (uiState.isLoading && uiState.metroLines.isEmpty()) {
                // Show loading indicator when initially loading
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
                            color = MaterialTheme.colorScheme.primary
                        )
                        Text(
                            text = "Loading metro lines...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (uiState.metroLines.isEmpty() && !uiState.isLoading) {
                // Show empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No metro lines available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            } else {
                // Show content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Metro Line Selector
                    val metroLineItems = uiState.metroLines.map { metroLine ->
                        MetroLineItem(
                            id = metroLine.id,
                            name = metroLine.name,
                            color = metroLine.color
                        )
                    }
                    
                    val selectedMetroLineItem = uiState.selectedMetroLine?.let { selectedLine ->
                        MetroLineItem(
                            id = selectedLine.id,
                            name = selectedLine.name,
                            color = selectedLine.color
                        )
                    }

                    MetroLineSelector(
                        selectedLine = selectedMetroLineItem,
                        metroLines = metroLineItems,
                        onLineSelected = handleMetroLineSelected,
                        label = "Select Metro Line",
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    // MapBox Map
                    MetroLineMapView(
                        selectedMetroLine = uiState.selectedMetroLine,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                    )
                }
            }
        }
    }
    
    //region Dialog and Sheet
    //endregion
    //endregion
} 