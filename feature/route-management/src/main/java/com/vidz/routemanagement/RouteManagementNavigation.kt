package com.vidz.routemanagement

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.routemanagement.management.RouteManagementScreenRoot

fun NavGraphBuilder.addRouteManagementNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    navigation(
        route = DestinationRoutes.ROOT_ROUTE_MANAGEMENT_SCREEN_ROUTE,
        startDestination = DestinationRoutes.ROUTE_MANAGEMENT_SCREEN_ROUTE
    ) {
        
        // Route management main screen with optional query parameter
        composable("${DestinationRoutes.ROUTE_MANAGEMENT_SCREEN_ROUTE}?numberItemOfPage={numberItemOfPage}") {
            RouteManagementScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
            )
        }
        
        composable(DestinationRoutes.ROUTE_MAP_SCREEN_ROUTE) {
            // TODO: Implement RouteMapScreenRoot
        }
        
        composable(DestinationRoutes.STATION_LIST_SCREEN_ROUTE) {
            // TODO: Implement StationListScreenRoot
        }
        
        composable(DestinationRoutes.TRAIN_SCHEDULE_SCREEN_ROUTE) {
            // TODO: Implement TrainScheduleScreenRoot
        }
        
        // Parametrized routes
        composable(DestinationRoutes.STATION_DETAIL_SCREEN_ROUTE) { backStackEntry ->
            val stationId = backStackEntry.arguments?.getString("stationId") ?: ""
            val stationName = backStackEntry.arguments?.getString("stationName") ?: ""
            // TODO: Implement StationDetailScreenRoot(stationId, stationName)
        }
        
        composable(DestinationRoutes.TRAIN_DETAIL_SCREEN_ROUTE) { backStackEntry ->
            val trainId = backStackEntry.arguments?.getString("trainId") ?: ""
            val trainNumber = backStackEntry.arguments?.getString("trainNumber") ?: ""
            // TODO: Implement TrainDetailScreenRoot(trainId, trainNumber)
        }
        
        composable(DestinationRoutes.ROUTE_DETAIL_SCREEN_ROUTE) { backStackEntry ->
            val routeId = backStackEntry.arguments?.getString("routeId") ?: ""
            val routeName = backStackEntry.arguments?.getString("routeName") ?: ""
            // TODO: Implement RouteDetailScreenRoot(routeId, routeName)
        }
    }
} 