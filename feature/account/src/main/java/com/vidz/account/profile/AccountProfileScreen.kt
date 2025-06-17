package com.vidz.account.profile

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
fun AccountProfileScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    onShowSnackbar: (String) -> Unit,
    accountProfileViewModel: AccountProfileViewModel = hiltViewModel(),
) {
    val accountProfileUiState = accountProfileViewModel.uiState.collectAsStateWithLifecycle()
    AccountProfileScreen(
        navController = navController,
        accountProfileUiState = accountProfileUiState
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun AccountProfileScreen(
    navController: NavController,
    accountProfileUiState: State<AccountProfileViewModel.AccountProfileViewState>
) {
    //region Define Var
    //endregion

    //region Event Handler
    //endregion

    //region ui
    Text(
        text = "Account Profile Screen - Quản lý tài khoản",
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    )
    
    //region Dialog and Sheet
    //endregion
    //endregion
} 