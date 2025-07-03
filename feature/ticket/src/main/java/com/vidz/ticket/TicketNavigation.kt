package com.vidz.ticket

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.navigation.NavigationAnimations.enterTransition
import com.vidz.base.navigation.NavigationAnimations.exitTransition
import com.vidz.base.navigation.NavigationAnimations.popEnterTransition
import com.vidz.base.navigation.NavigationAnimations.popExitTransition
import com.vidz.ticket.cart.TicketCartScreenRoot
import com.vidz.ticket.detail.OrderDetailScreenRoot
import com.vidz.ticket.management.TicketManagementScreenRoot
import com.vidz.ticket.purchase.TicketPurchaseScreenRoot
import com.vidz.ticket.qr.QRDisplayScreenRoot

fun NavGraphBuilder.addTicketNavGraph(
    navController: NavController,
    onShowSnackbar: (String) -> Unit
) {
    navigation(
        route = DestinationRoutes.ROOT_TICKET_SCREEN_ROUTE,
        startDestination = DestinationRoutes.TICKET_PURCHASE_SCREEN_ROUTE
    ) {
        
        // Ticket purchase route with optional query parameter
        composable("${DestinationRoutes.TICKET_PURCHASE_SCREEN_ROUTE}?numberItemOfPage={numberItemOfPage}") {
            TicketPurchaseScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
            )
        }
        
        composable(DestinationRoutes.TICKET_CART_SCREEN_ROUTE,
                   enterTransition = enterTransition,
                   exitTransition = exitTransition,
                   popEnterTransition = popEnterTransition,
                   popExitTransition = popExitTransition
                   ) {
            TicketCartScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
            )
        }
        
        composable(DestinationRoutes.TICKET_PAYMENT_SCREEN_ROUTE) {
            // TODO: Implement TicketPaymentScreenRoot
        }
        
        composable(DestinationRoutes.TICKET_CONFIRMATION_SCREEN_ROUTE) {
            // TODO: Implement TicketConfirmationScreenRoot
        }
        
        composable(DestinationRoutes.MY_TICKETS_SCREEN_ROUTE) {
            TicketManagementScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
            )
        }
        
        composable(DestinationRoutes.ORDER_DETAIL_SCREEN_ROUTE,
                   enterTransition = enterTransition,
                   exitTransition = exitTransition,
                   popEnterTransition = popEnterTransition,
                   popExitTransition = popExitTransition

                   ) { backStackEntry ->
            val orderId = backStackEntry.arguments?.getString("orderId") ?: ""
            OrderDetailScreenRoot(
                navController = navController,
                onShowSnackbar = onShowSnackbar,
            )
        }
        
        composable(DestinationRoutes.CHECKIN_SCREEN_ROUTE) {
            // TODO: Implement CheckinScreenRoot
        }
        
        composable("${DestinationRoutes.QR_TICKET_SCREEN_ROUTE}/{ticketId}") { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: ""
            QRDisplayScreenRoot(
                navController = navController,
                ticketId = ticketId,
                onShowSnackbar = onShowSnackbar
            )
        }
        
        // Parametrized routes
        composable(DestinationRoutes.TICKET_DETAIL_SCREEN_ROUTE) { backStackEntry ->
            val ticketId = backStackEntry.arguments?.getString("ticketId") ?: ""
            // TODO: Implement TicketDetailScreenRoot(ticketId)
        }
    }
} 
