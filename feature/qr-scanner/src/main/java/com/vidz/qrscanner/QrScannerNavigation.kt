package com.vidz.qrscanner

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.vidz.qrscanner.scanner.QrScannerScreenRoot

fun NavGraphBuilder.addQrScannerNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    // QR Scanner can be used as a shared component, so we don't need its own root route
    // It will be called from other modules when needed
    composable("qr_scanner") {
        QrScannerScreenRoot(
            navController = navController,
            onShowSnackbar = onShowSnackbar,
        )
    }
} 
