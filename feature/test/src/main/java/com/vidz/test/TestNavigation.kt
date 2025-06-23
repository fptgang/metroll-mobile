package com.vidz.test

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

const val TEST_ROUTE = "test"
const val ORDER_TEST_ROUTE = "order_test"

@Composable
fun TestNavigation(
    navController: NavHostController = rememberNavController()
) {
    NavHost(
        navController = navController,
        startDestination = ORDER_TEST_ROUTE
    ) {
        composable(ORDER_TEST_ROUTE) {
            OrderTestScreen()
        }
    }
}

// Navigation functions for external use
fun NavHostController.navigateToOrderTest() {
    navigate(ORDER_TEST_ROUTE) {
        launchSingleTop = true
    }
} 