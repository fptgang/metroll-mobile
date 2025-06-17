
import androidx.compose.animation.ExperimentalSharedTransitionApi
import androidx.compose.animation.SharedTransitionLayout
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import com.vidz.account.addAccountNavGraph
import com.vidz.auth.addAuthNavGraph
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.navigation.NavigationAnimations.enterTransition
import com.vidz.base.navigation.NavigationAnimations.exitTransition
import com.vidz.base.navigation.NavigationAnimations.popEnterTransition
import com.vidz.base.navigation.NavigationAnimations.popExitTransition
import com.vidz.home.addHomeNavGraph
import com.vidz.membership.addMembershipNavGraph
import com.vidz.qrscanner.addQrScannerNavGraph
import com.vidz.staff.addStaffNavGraph
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
            enterTransition = enterTransition,
            exitTransition = exitTransition,
            popEnterTransition = popEnterTransition,
            popExitTransition = popExitTransition
        ) {
            // Home Navigation (includes Route Management)
            addHomeNavGraph(navController, onShowSnackbar)
            
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
            
            // Authentication Navigation
            addAuthNavGraph(navController, onShowSnackbar)
        }
    }
}
