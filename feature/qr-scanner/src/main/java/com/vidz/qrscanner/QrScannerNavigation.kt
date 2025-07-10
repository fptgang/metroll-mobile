package com.vidz.qrscanner

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.navigation.NavigationAnimations
import com.vidz.qrscanner.scanner.QrScannerScreenRoot

fun NavGraphBuilder.addQrScannerNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    // QR Scanner can be used as a shared component, so we don't need its own root route
    // It will be called from other modules when needed
    composable(DestinationRoutes.QR_SCANNER_SCREEN_ROUTE,
               enterTransition = NavigationAnimations.enterTransition,
                exitTransition = NavigationAnimations.exitTransition,
                popEnterTransition = NavigationAnimations.popEnterTransition,
                popExitTransition = NavigationAnimations.popExitTransition
               ) {
        QrScannerScreenRoot(
            navController = navController,
            onShowSnackbar = onShowSnackbar,
        )
    }
} 
