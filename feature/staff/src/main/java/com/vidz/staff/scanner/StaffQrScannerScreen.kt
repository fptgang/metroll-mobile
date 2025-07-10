package com.vidz.staff.scanner

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.vidz.qrscanner.scanner.QrScannerScreenRoot
import com.vidz.qrscanner.scanner.QrScannerViewModel

/**
 * Staff-specific wrapper that delegates directly to the shared [QrScannerScreenRoot].
 * This re-uses the full scanner workflow (mode selection, validation, success/error handling)
 * while living under the Staff navigation graph.
 */
@Composable
fun StaffQrScannerScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    onShowSnackbar: (String) -> Unit,
    qrScannerViewModel: QrScannerViewModel = hiltViewModel(),
) {
    QrScannerScreenRoot(
        navController = navController,
        modifier = modifier,
        onShowSnackbar = onShowSnackbar,
        qrScannerViewModel = qrScannerViewModel,
    )
} 
