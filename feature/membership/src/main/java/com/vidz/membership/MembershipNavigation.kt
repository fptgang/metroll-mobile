package com.vidz.membership

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.membership.packages.MembershipPackagesScreenRoot

fun NavGraphBuilder.addMembershipNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    navigation(
        route = DestinationRoutes.ROOT_MEMBERSHIP_SCREEN_ROUTE,
        startDestination = DestinationRoutes.MEMBERSHIP_PACKAGES_SCREEN_ROUTE
    ) {
        
        composable(DestinationRoutes.MEMBERSHIP_SCREEN_ROUTE) {
            // TODO: Implement MembershipScreenRoot
        }
        
        composable(DestinationRoutes.MEMBERSHIP_PACKAGES_SCREEN_ROUTE) {
            MembershipPackagesScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
            )
        }
        
        composable(DestinationRoutes.MEMBERSHIP_BENEFITS_SCREEN_ROUTE) {
            // TODO: Implement MembershipBenefitsScreenRoot
        }
        
        // Parametrized routes
        composable(DestinationRoutes.MEMBERSHIP_PURCHASE_SCREEN_ROUTE) { backStackEntry ->
            val packageId = backStackEntry.arguments?.getString("packageId") ?: ""
            // TODO: Implement MembershipPurchaseScreenRoot(packageId)
        }
    }
} 