package com.vidz.staff.scanner

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
fun StaffQrScannerScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    onShowSnackbar: (String) -> Unit,
    staffQrScannerViewModel: StaffQrScannerViewModel = hiltViewModel(),
) {
    val staffQrScannerUiState = staffQrScannerViewModel.uiState.collectAsStateWithLifecycle()
    StaffQrScannerScreen(
        navController = navController,
        staffQrScannerUiState = staffQrScannerUiState
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun StaffQrScannerScreen(
    navController: NavController,
    staffQrScannerUiState: State<StaffQrScannerViewModel.StaffQrScannerViewState>
) {
    //region Define Var
    //endregion

    //region Event Handler
    //endregion

    //region ui
    Text(
        text = "Staff QR Scanner Screen - Quét QR vé tàu",
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    )
    
    //region Dialog and Sheet
    //endregion
    //endregion
} 