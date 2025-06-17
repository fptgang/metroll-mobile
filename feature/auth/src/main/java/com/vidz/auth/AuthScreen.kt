package com.vidz.auth

import android.annotation.SuppressLint
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.auth.forgot_password.ForgotPasswordScreenRoot
import com.vidz.auth.login.LoginScreenRoot
import com.vidz.auth.register.RegisterScreenRoot

@Composable
fun AuthScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    authViewModel: AuthViewModel = hiltViewModel(),
) {
    val authUiState = authViewModel.uiState.collectAsStateWithLifecycle()
    AuthScreen(
        navController = navController,
        authUiState = authUiState,
        onEvent = authViewModel::onTriggerEvent
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthScreen(
    navController: NavController,
    authUiState: State<AuthViewModel.AuthViewState>,
    onEvent: (AuthViewModel.AuthViewEvent) -> Unit
) {
    //region Define Var
    val currentScreen = authUiState.value.currentScreen
    //endregion

    //region Event Handler
    val onShowLoginScreen = {
        onEvent(AuthViewModel.AuthViewEvent.ShowLoginScreen)
    }

    val onShowRegisterScreen = {
        onEvent(AuthViewModel.AuthViewEvent.ShowRegisterScreen)
    }

    val onShowForgotPasswordScreen = {
        onEvent(AuthViewModel.AuthViewEvent.ShowForgotPasswordScreen)
    }
    //endregion

    //region ui
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        when (currentScreen) {
            AuthViewModel.AuthScreen.LOGIN -> {
                LoginScreenRoot(
                    navController = navController,
                    onNavigateToRegister = onShowRegisterScreen,
                    onNavigateToForgotPassword = onShowForgotPasswordScreen
                )
            }
            AuthViewModel.AuthScreen.REGISTER -> {
                RegisterScreenRoot(
                    navController = navController,
                    onNavigateToLogin = onShowLoginScreen
                )
            }
            AuthViewModel.AuthScreen.FORGOT_PASSWORD -> {
                ForgotPasswordScreenRoot(
                    navController = navController,
                    onNavigateToLogin = onShowLoginScreen
                )
            }
        }
    }

    //region Dialog and Sheet
    //endregion
    //endregion
}
