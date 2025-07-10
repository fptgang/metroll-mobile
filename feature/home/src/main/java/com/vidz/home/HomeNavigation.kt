package com.vidz.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.domain.model.AccountRole
import com.vidz.domain.usecase.account.ObserveLocalAccountInfoUseCase
import com.vidz.home.customerhome.CustomerHomeScreenRoot
import com.vidz.home.staffhome.StaffHomeScreenRoot
import com.vidz.home.staffhome.TicketValidationLogsScreenRoot
import dagger.hilt.android.lifecycle.HiltViewModel

fun NavGraphBuilder.addHomeNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {

    navigation(
        route = DestinationRoutes.ROOT_HOME_SCREEN_ROUTE,
        startDestination = DestinationRoutes.HOME_SCREEN_ROUTE
    ) {
        // Main home screen route that redirects based on user role
        composable("${DestinationRoutes.HOME_SCREEN_ROUTE}?numberItemOfPage={numberItemOfPage}") {
            RoleBasedHomeScreenRouter(
                navController = navController,
                onShowSnackbar = onShowSnackbar
            )
        }
        
        // Customer home screen route
        composable("${DestinationRoutes.CUSTOMER_HOME_SCREEN_ROUTE}?numberItemOfPage={numberItemOfPage}") {
            CustomerHomeScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
            )
        }
        
        // Staff home screen route
        composable("${DestinationRoutes.STAFF_HOME_SCREEN_ROUTE}?numberItemOfPage={numberItemOfPage}") {
            StaffHomeScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
            )
        }
        
        // Staff ticket validation logs screen route
        composable(DestinationRoutes.STAFF_SCAN_HISTORY_SCREEN_ROUTE) {
            TicketValidationLogsScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
            )
        }

    }
}

@Composable
private fun RoleBasedHomeScreenRouter(
    navController: NavController,
    onShowSnackbar: (String) -> Unit,
    viewModel : RoleBasedHomeViewModel = hiltViewModel()
) {
    // Use a simple ViewModel to get account info
    val account by viewModel.observeLocalAccountInfoUseCase().collectAsStateWithLifecycle(initialValue = null)
    
    LaunchedEffect(account) {
        if(account?.id?.isBlank() != true){
            account?.let { userAccount ->
                val destination = when (userAccount.role) {
                    AccountRole.STAFF, AccountRole.ADMIN -> {
                        DestinationRoutes.STAFF_HOME_SCREEN_ROUTE
                    }
                    AccountRole.CUSTOMER -> {
                        DestinationRoutes.CUSTOMER_HOME_SCREEN_ROUTE
                    }
                }

                // Navigate to the appropriate home screen based on role
                navController.navigate(destination) {
                    popUpTo(DestinationRoutes.HOME_SCREEN_ROUTE) { inclusive = true }
                }
            }
        }

    }
    
    // Show loading state while determining role
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Loading...",
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

// Simple ViewModel for role-based navigation
@HiltViewModel
class RoleBasedHomeViewModel @javax.inject.Inject constructor(
    val observeLocalAccountInfoUseCase: ObserveLocalAccountInfoUseCase
) : androidx.lifecycle.ViewModel()
