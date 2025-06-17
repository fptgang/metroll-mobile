package com.vidz.routemanagement.management

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController

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
        routeManagementUiState = routeManagementUiState
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun RouteManagementScreen(
    navController: NavController,
    routeManagementUiState: State<RouteManagementViewModel.RouteManagementViewState>
) {
    //region Define Var
    //endregion

    //region Event Handler
    //endregion

    //region ui
    Text(
        text = "Route Management Screen - Quản lý tuyến (ga, tàu, tuyến)",
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    )
    
    //region Dialog and Sheet
    //endregion
    //endregion
} 