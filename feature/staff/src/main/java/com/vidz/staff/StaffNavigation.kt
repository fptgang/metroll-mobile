package com.vidz.staff

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.staff.scanner.StaffQrScannerScreenRoot

fun NavGraphBuilder.addStaffNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    navigation(
        route = DestinationRoutes.ROOT_STAFF_SCREEN_ROUTE,
        startDestination = DestinationRoutes.STAFF_QR_SCANNER_SCREEN_ROUTE
    ) {
        
        composable(DestinationRoutes.STAFF_LOGIN_SCREEN_ROUTE) {
            // TODO: Implement StaffLoginScreenRoot
        }
        
        composable(DestinationRoutes.STAFF_QR_SCANNER_SCREEN_ROUTE) {
            StaffQrScannerScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
            )
        }
        
        composable(DestinationRoutes.STAFF_TICKET_VALIDATION_SCREEN_ROUTE) {
            // TODO: Implement StaffTicketValidationScreenRoot
        }
        
        composable(DestinationRoutes.STAFF_SCAN_HISTORY_SCREEN_ROUTE) {
            // TODO: Implement StaffScanHistoryScreenRoot
        }
    }
} 