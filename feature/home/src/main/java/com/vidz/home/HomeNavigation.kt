package com.vidz.home

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.home.customerhome.CustomerHomeScreenRoot
import com.vidz.home.staffhome.StaffHomeScreenRoot

fun NavGraphBuilder.addHomeNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {

    navigation(
        route = DestinationRoutes.ROOT_HOME_SCREEN_ROUTE,
        startDestination = DestinationRoutes.HOME_SCREEN_ROUTE
    ) {

        // Home screen route with optional query parameter (defaults to customer for backwards compatibility)
        composable("${DestinationRoutes.HOME_SCREEN_ROUTE}?numberItemOfPage={numberItemOfPage}") {
            CustomerHomeScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
            )
        }
        
        // Customer home screen route
        composable("${DestinationRoutes.CUSTOMER_HOME_SCREEN_ROUTE}?numberItemOfPage={numberItemOfPage}") {
            CustomerHomeScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
            )
        }
        
        // Staff home screen route
        composable("${DestinationRoutes.STAFF_HOME_SCREEN_ROUTE}?numberItemOfPage={numberItemOfPage}") {
            StaffHomeScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
            )
        }
        

    }

}
