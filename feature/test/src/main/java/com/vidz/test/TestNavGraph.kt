package com.vidz.test

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation

const val ROOT_TEST_ROUTE = "test_root"

fun NavGraphBuilder.addTestNavGraph(
    navController: NavHostController,
    onShowSnackbar: (String) -> Unit
) {
    navigation(
        route = ROOT_TEST_ROUTE,
        startDestination = ORDER_TEST_ROUTE
    ) {
        composable(ORDER_TEST_ROUTE) {
            OrderTestScreen()
        }
    }
} 