package com.vidz.metroll_mobile.presentation.app

import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.model.AccountRole
import com.vidz.domain.usecase.account.ObserveLocalAccountInfoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class MetrollAppViewModel @Inject constructor(
    private val observeLocalAccountInfoUseCase: ObserveLocalAccountInfoUseCase
) : BaseViewModel<
        MetrollAppViewModel.MetrollViewEvent,
        MetrollAppViewModel.MetrollViewState,
        MetrollAppViewModel.MetrollViewModelState
        >(MetrollViewModelState()) {

    init {
        observeUserRole()
    }

    private fun observeUserRole() {
        viewModelScope.launch {
            observeLocalAccountInfoUseCase().collect { account ->
                viewModelState.update { 
                    it.copy(
                        userRole = account?.role ?: AccountRole.CUSTOMER,
                        isLoggedIn = account != null
                    )
                }
            }
        }
    }

    sealed class MetrollViewEvent : ViewEvent {
        data class ObserveNavDestination(
            val navController: NavController
        ) : MetrollViewEvent()
    }

    data class MetrollViewState(
        val shouldShowBottomBar: Boolean = true,
        val currentNavIndex: Int = 0,
        val userRole: AccountRole = AccountRole.CUSTOMER,
        val isLoggedIn: Boolean = false
    ) : ViewState()

    data class MetrollViewModelState(
        val shouldShowBottomBar: Boolean = true,
        val currentNavIndex: Int = 0,
        val userRole: AccountRole = AccountRole.CUSTOMER,
        val isLoggedIn: Boolean = false
    ) : ViewModelState() {
        override fun toUiState(): ViewState {
            return MetrollViewState(
                shouldShowBottomBar = shouldShowBottomBar,
                currentNavIndex = currentNavIndex,
                userRole = userRole,
                isLoggedIn = isLoggedIn
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
        val currentUserRole = viewModelState.value.userRole
        
        // Routes that should show bottom bar (only for customers)
        val customerRoutes = listOf(
            DestinationRoutes.HOME_SCREEN_ROUTE,
            DestinationRoutes.CUSTOMER_HOME_SCREEN_ROUTE,
            DestinationRoutes.ROUTE_MANAGEMENT_SCREEN_ROUTE,
            DestinationRoutes.TICKET_PURCHASE_SCREEN_ROUTE,
            DestinationRoutes.MY_TICKETS_SCREEN_ROUTE,
            DestinationRoutes.ACCOUNT_PROFILE_SCREEN_ROUTE,
        )

        // Staff routes that should not show bottom bar
        val staffRoutes = listOf(
            DestinationRoutes.STAFF_HOME_SCREEN_ROUTE,
            DestinationRoutes.STAFF_QR_SCANNER_SCREEN_ROUTE,
            DestinationRoutes.STAFF_TICKET_VALIDATION_SCREEN_ROUTE,
            DestinationRoutes.STAFF_SCAN_HISTORY_SCREEN_ROUTE
        )

        // Determine if bottom bar should be shown based on route and user role
        val shouldShowBottomBar = currentRoute?.let { route ->
            when (currentUserRole) {
                AccountRole.CUSTOMER -> {
                    // Show bottom bar only for customer routes
                    customerRoutes.any { allowedRoute ->
                        route.startsWith(allowedRoute)
                    }
                }
                AccountRole.STAFF, AccountRole.ADMIN -> {
                    // Staff users don't get bottom navigation
                    false
                }
            }
        } ?: false
        
        // Find the index for the current route (strip query parameters for matching) - only for customers
        val currentNavIndex = if (currentUserRole == AccountRole.CUSTOMER) {
            currentRoute?.let { route ->
                customerRoutes.indexOfFirst { allowedRoute ->
                    route.startsWith(allowedRoute)
                }.takeIf { it >= 0 } ?: 0
            } ?: 0
        } else {
            0
        }
        
        viewModelState.update {
            it.copy(
                shouldShowBottomBar = shouldShowBottomBar,
                currentNavIndex = currentNavIndex
            )
        }
    }
}
