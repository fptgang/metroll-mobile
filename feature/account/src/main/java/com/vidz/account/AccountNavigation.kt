package com.vidz.account

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vidz.account.profile.AccountProfileScreenRoot
import com.vidz.account.profile.EditProfileScreenRoot
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.navigation.NavigationAnimations.enterTransition
import com.vidz.base.navigation.NavigationAnimations.exitTransition
import com.vidz.base.navigation.NavigationAnimations.popEnterTransition
import com.vidz.base.navigation.NavigationAnimations.popExitTransition

fun NavGraphBuilder.addAccountNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    navigation(
        route = DestinationRoutes.ROOT_ACCOUNT_SCREEN_ROUTE,
        startDestination = DestinationRoutes.ACCOUNT_PROFILE_SCREEN_ROUTE

    ) {
        
        // Account profile route with optional query parameter
        composable("${DestinationRoutes.ACCOUNT_PROFILE_SCREEN_ROUTE}?numberItemOfPage={numberItemOfPage}") {
            AccountProfileScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
            )
        }
           // Account profile route with optional query parameter
        composable("${DestinationRoutes.STAFF_PROFILE_SCREEN_ROUTE}?numberItemOfPage={numberItemOfPage}",
                   enterTransition = enterTransition,
                   exitTransition = exitTransition,
                   popEnterTransition = popEnterTransition,
                   popExitTransition = popExitTransition
                   ) {
            AccountProfileScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,

            )
        }

        composable(DestinationRoutes.EDIT_PROFILE_SCREEN_ROUTE,
                   enterTransition = enterTransition,
                   exitTransition = exitTransition,
                   popEnterTransition = popEnterTransition,
                   popExitTransition = popExitTransition
                   ) {
            EditProfileScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar
            )
        }
        
        composable(DestinationRoutes.ACCOUNT_SETTINGS_SCREEN_ROUTE) {
            // TODO: Implement AccountSettingsScreenRoot
        }
        
        composable(DestinationRoutes.CHANGE_PASSWORD_SCREEN_ROUTE) {
            // TODO: Implement ChangePasswordScreenRoot
        }
        
        composable(DestinationRoutes.PAYMENT_METHODS_SCREEN_ROUTE) {
            // TODO: Implement PaymentMethodsScreenRoot
        }
        
        composable(DestinationRoutes.TRAVEL_HISTORY_SCREEN_ROUTE) {
            // TODO: Implement TravelHistoryScreenRoot
        }
    }
} 
