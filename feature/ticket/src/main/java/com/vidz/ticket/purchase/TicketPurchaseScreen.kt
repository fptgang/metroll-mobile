package com.vidz.ticket.purchase

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
fun TicketPurchaseScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    onShowSnackbar: (String) -> Unit,
    ticketPurchaseViewModel: TicketPurchaseViewModel = hiltViewModel(),
) {
    val ticketPurchaseUiState = ticketPurchaseViewModel.uiState.collectAsStateWithLifecycle()
    TicketPurchaseScreen(
        navController = navController,
        ticketPurchaseUiState = ticketPurchaseUiState
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun TicketPurchaseScreen(
    navController: NavController,
    ticketPurchaseUiState: State<TicketPurchaseViewModel.TicketPurchaseViewState>
) {
    //region Define Var
    //endregion

    //region Event Handler
    //endregion

    //region ui
    Text(
        text = "Ticket Purchase Screen - Mua vé",
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    )
    
    //region Dialog and Sheet
    //endregion
    //endregion
} 