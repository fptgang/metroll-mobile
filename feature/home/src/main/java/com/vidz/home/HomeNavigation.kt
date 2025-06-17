package com.vidz.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.home.customerhome.CustomerHomeScreenRoot
import com.vidz.home.staffhome.StaffHomeScreenRoot

fun NavGraphBuilder.addHomeNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {

    navigation(
        route = DestinationRoutes.ROOT_HOME_SCREEN_ROUTE,
        startDestination = DestinationRoutes.HOME_SCREEN_ROUTE
    ) {

        // Home screen route with optional query parameter (defaults to customer for backwards compatibility)
        composable("${DestinationRoutes.HOME_SCREEN_ROUTE}?numberItemOfPage={numberItemOfPage}") {
            CustomerHomeScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
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
        
        // Route Management screens - TODO: These should be implemented with proper screens
        composable("${DestinationRoutes.ROUTE_MANAGEMENT_SCREEN_ROUTE}?numberItemOfPage={numberItemOfPage}") {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Route Management Screen - Quản lý tuyến (ga, tàu, tuyến)",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        composable(DestinationRoutes.ROUTE_MAP_SCREEN_ROUTE) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Route Map Screen - TODO: Implement",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        composable(DestinationRoutes.STATION_LIST_SCREEN_ROUTE) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Station List Screen - TODO: Implement",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        composable(DestinationRoutes.TRAIN_SCHEDULE_SCREEN_ROUTE) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Train Schedule Screen - TODO: Implement",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        // Parametrized routes
        composable(DestinationRoutes.STATION_DETAIL_SCREEN_ROUTE) { backStackEntry ->
            val stationId = backStackEntry.arguments?.getString("stationId") ?: ""
            val stationName = backStackEntry.arguments?.getString("stationName") ?: ""
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Station Detail: $stationName (ID: $stationId) - TODO: Implement",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        composable(DestinationRoutes.TRAIN_DETAIL_SCREEN_ROUTE) { backStackEntry ->
            val trainId = backStackEntry.arguments?.getString("trainId") ?: ""
            val trainNumber = backStackEntry.arguments?.getString("trainNumber") ?: ""
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Train Detail: $trainNumber (ID: $trainId) - TODO: Implement",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
        
        composable(DestinationRoutes.ROUTE_DETAIL_SCREEN_ROUTE) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: ""
            val routeName = backStackEntry.arguments?.getString("routeName") ?: ""
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Route Detail: $routeName (ID: $routeId) - TODO: Implement",
                    style = MaterialTheme.typography.headlineSmall,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }

}
