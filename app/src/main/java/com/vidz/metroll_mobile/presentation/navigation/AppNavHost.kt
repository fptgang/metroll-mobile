
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.navigation
import com.vidz.account.addAccountNavGraph
import com.vidz.auth.authGraph
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.home.addHomeNavGraph
import com.vidz.membership.addMembershipNavGraph
import com.vidz.qrscanner.addQrScannerNavGraph
import com.vidz.routemanagement.addRouteManagementNavGraph
import com.vidz.staff.addStaffNavGraph
import com.vidz.test.addTestNavGraph
import com.vidz.ticket.addTicketNavGraph

@OptIn(ExperimentalSharedTransitionApi::class)
@Composable
fun AppNavHost(
    navController: NavHostController,
    onShowSnackbar: (String) -> Unit
) {
    SharedTransitionLayout {
        NavHost(
            navController = navController,
            startDestination = DestinationRoutes.ROOT_HOME_SCREEN_ROUTE,
//            enterTransition = enterTransition,
//            exitTransition = exitTransition,
//            popEnterTransition = popEnterTransition,
//            popExitTransition = popExitTransition
        ) {
            // Home Navigation
            addHomeNavGraph(navController, onShowSnackbar)
            
            // Route Management Navigation
            addRouteManagementNavGraph(navController, onShowSnackbar)
            
            // Ticket Management Navigation
            addTicketNavGraph(navController, onShowSnackbar)
            
            // Account Management Navigation  
            addAccountNavGraph(navController, onShowSnackbar)
            
            // Membership Management Navigation
            addMembershipNavGraph(navController, onShowSnackbar)
            
            // Staff Navigation
            addStaffNavGraph(navController, onShowSnackbar)
            
            // QR Scanner Navigation (shared component)
            addQrScannerNavGraph(navController, onShowSnackbar)
            
            // Test Navigation (for API testing)
            addTestNavGraph(navController, onShowSnackbar)
            
            // Authentication Navigation
            navigation(
                route = DestinationRoutes.ROOT_AUTH_SCREEN_ROUTE,
                startDestination = "auth"
            ) {
                authGraph(navController, onShowSnackbar) {
                    // Navigate to home after successful authentication
                    navController.navigate(DestinationRoutes.ROOT_HOME_SCREEN_ROUTE) {
                        popUpTo(DestinationRoutes.ROOT_AUTH_SCREEN_ROUTE) { inclusive = true }
                    }
                }
            }
        }
    }
}
