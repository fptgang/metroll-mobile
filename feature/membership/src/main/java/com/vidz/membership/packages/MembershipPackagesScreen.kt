package com.vidz.membership.packages

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
fun MembershipPackagesScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    onShowSnackbar: (String) -> Unit,
    membershipPackagesViewModel: MembershipPackagesViewModel = hiltViewModel(),
) {
    val membershipPackagesUiState = membershipPackagesViewModel.uiState.collectAsStateWithLifecycle()
    MembershipPackagesScreen(
        navController = navController,
        membershipPackagesUiState = membershipPackagesUiState
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun MembershipPackagesScreen(
    navController: NavController,
    membershipPackagesUiState: State<MembershipPackagesViewModel.MembershipPackagesViewState>
) {
    //region Define Var
    //endregion

    //region Event Handler
    //endregion

    //region ui
    Text(
        text = "Membership Packages Screen - Quản lý gói thành viên",
        modifier = Modifier
            .padding(16.dp)
            .background(MaterialTheme.colorScheme.background)
    )
    
    //region Dialog and Sheet
    //endregion
    //endregion
} 