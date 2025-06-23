package com.vidz.auth

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.vidz.auth.forgot_password.ForgotPasswordScreen
import com.vidz.auth.login.LoginScreen
import com.vidz.auth.login.LoginViewModel
import com.vidz.auth.register.RegisterScreen
import com.vidz.auth.register.RegisterViewModel

object DestinationRoutes {
    const val AUTH_ROUTE = "auth"
    const val LOGIN_SCREEN_ROUTE = "login"
    const val REGISTER_SCREEN_ROUTE = "register"
    const val FORGOT_PASSWORD_SCREEN_ROUTE = "forgot_password"
}

fun NavGraphBuilder.authGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit,
    onNavigateToApp: () -> Unit
) {
    navigation(
        startDestination = DestinationRoutes.LOGIN_SCREEN_ROUTE,
        route = DestinationRoutes.AUTH_ROUTE
    ) {
        composable(DestinationRoutes.LOGIN_SCREEN_ROUTE) {
            val viewModel: LoginViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            
            // Handle successful login with Firebase + Backend flow
            LaunchedEffect(uiState.isLoginSuccessful) {
                if (uiState.isLoginSuccessful) {
                    onNavigateToApp()
                }
            }
            
            // Handle errors
            LaunchedEffect(uiState.errorMessage) {
                uiState.errorMessage?.let { error ->
                    onShowSnackbar(error)
                    viewModel.onTriggerEvent(LoginViewModel.LoginEvent.ErrorDismissed)
                }
            }
            
            LoginScreen(
                uiState = uiState,
                onEvent = viewModel::onTriggerEvent,
                onNavigateToRegister = {
                    navController.navigate(DestinationRoutes.REGISTER_SCREEN_ROUTE)
                },
                onNavigateToForgotPassword = {
                    navController.navigate(DestinationRoutes.FORGOT_PASSWORD_SCREEN_ROUTE)
                }
            )
        }

        composable(DestinationRoutes.REGISTER_SCREEN_ROUTE) {
            val viewModel: RegisterViewModel = hiltViewModel()
            val uiState by viewModel.uiState.collectAsStateWithLifecycle()
            
            // Handle successful registration with Firebase + Backend flow
            LaunchedEffect(uiState.isRegistrationSuccessful) {
                if (uiState.isRegistrationSuccessful) {
                    onNavigateToApp()
                }
            }
            
            // Handle errors
            LaunchedEffect(uiState.errorMessage) {
                uiState.errorMessage?.let { error ->
                    onShowSnackbar(error)
                    viewModel.onTriggerEvent(RegisterViewModel.RegisterEvent.ErrorDismissed)
                }
            }
            
            RegisterScreen(
                uiState = uiState,
                onEvent = viewModel::onTriggerEvent,
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        composable(DestinationRoutes.FORGOT_PASSWORD_SCREEN_ROUTE) {
            ForgotPasswordScreen(
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
