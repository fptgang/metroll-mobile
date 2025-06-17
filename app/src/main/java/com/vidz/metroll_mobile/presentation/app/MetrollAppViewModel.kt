package com.vidz.metroll_mobile.presentation.app

import androidx.navigation.NavController
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.viewmodel.BaseViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class MetrollAppViewModel @Inject constructor() : BaseViewModel<
        MetrollAppViewModel.MetrollViewEvent,
        MetrollAppViewModel.MetrollViewState,
        MetrollAppViewModel.MetrollViewModelState
        >(MetrollViewModelState()) {

    sealed class MetrollViewEvent : ViewEvent {
        data class ObserveNavDestination(
            val navController: NavController
        ) : MetrollViewEvent()
    }

    data class MetrollViewState(
        val shouldShowBottomBar: Boolean = true,
        val currentNavIndex: Int = 0
    ) : ViewState()

    data class MetrollViewModelState(
        val shouldShowBottomBar: Boolean = true,
        val currentNavIndex: Int = 0
    ) : ViewModelState() {
        override fun toUiState(): ViewState {
            return MetrollViewState(
                shouldShowBottomBar = shouldShowBottomBar,
                currentNavIndex = currentNavIndex
            )
        }
    }

    override fun onTriggerEvent(event: MetrollViewEvent) {
        when (event) {
            is MetrollViewEvent.ObserveNavDestination -> handleShowBottomBar(event.navController)
        }
    }

    private fun handleShowBottomBar(controller: NavController) {
        val currentDestination = controller.currentDestination
        val currentRoute = currentDestination?.route
        
        // Routes that should show bottom bar (only for customers)
        val customerRoutes = listOf(
            DestinationRoutes.HOME_SCREEN_ROUTE,
            DestinationRoutes.CUSTOMER_HOME_SCREEN_ROUTE,
            DestinationRoutes.ROUTE_MANAGEMENT_SCREEN_ROUTE,
            DestinationRoutes.TICKET_PURCHASE_SCREEN_ROUTE,
            DestinationRoutes.ACCOUNT_PROFILE_SCREEN_ROUTE,
        )

        // Check if current route matches any allowed route (handle query parameters)
        val shouldShowBottomBar = currentRoute?.let { route ->
            customerRoutes.any { allowedRoute ->
                route.startsWith(allowedRoute)
            }
        } == true
        
        // Find the index for the current route (strip query parameters for matching)
        val currentNavIndex = currentRoute?.let { route ->
            customerRoutes.indexOfFirst { allowedRoute ->
                route.startsWith(allowedRoute)
            }.takeIf { it >= 0 } ?: 0
        } ?: 0
        
        viewModelState.update {
            it.copy(
                shouldShowBottomBar = shouldShowBottomBar,
                currentNavIndex = currentNavIndex
            )
        }
    }
}
