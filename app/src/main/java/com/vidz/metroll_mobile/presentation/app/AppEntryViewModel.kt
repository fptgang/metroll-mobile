package com.vidz.metroll_mobile.presentation.app

import androidx.lifecycle.viewModelScope
import com.vidz.base.interfaces.ViewEvent
import com.vidz.base.interfaces.ViewModelState
import com.vidz.base.interfaces.ViewState
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.viewmodel.BaseViewModel
import com.vidz.domain.Result
import com.vidz.domain.model.AuthenticationState
import com.vidz.domain.model.User
import com.vidz.domain.model.UserRole
import com.vidz.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppEntryViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : BaseViewModel<
    AppEntryViewModel.AppEntryViewEvent,
    AppEntryViewModel.AppEntryViewState,
    AppEntryViewModel.AppEntryViewModelState
>(AppEntryViewModelState()) {
    
    init {
        observeAuthenticationState()
    }
    
    private fun observeAuthenticationState() {
        viewModelScope.launch {
            authUseCase.getAuthenticationState().collect { authState ->
                viewModelState.update { currentState ->
                    currentState.copy(
                        authenticationState = authState,
                        isLoading = authState is AuthenticationState.Loading,
                        startDestination = getStartDestinationForAuthState(authState)
                    )
                }
            }
        }
    }
    
    private fun getStartDestinationForAuthState(authState: AuthenticationState): String {
        return when (authState) {
            is AuthenticationState.Loading -> "splash"
            is AuthenticationState.Unauthenticated -> DestinationRoutes.ROOT_AUTH_SCREEN_ROUTE
            is AuthenticationState.Authenticated -> {
                getHomeDestinationForRole(authState.user.role)
            }
            is AuthenticationState.Error -> DestinationRoutes.ROOT_AUTH_SCREEN_ROUTE
        }
    }
    
    private fun getHomeDestinationForRole(role: UserRole): String {
        return when (role) {
            UserRole.CUSTOMER -> DestinationRoutes.ROOT_HOME_SCREEN_ROUTE
            UserRole.STAFF -> DestinationRoutes.ROOT_STAFF_SCREEN_ROUTE
            UserRole.ADMIN -> DestinationRoutes.ROOT_HOME_SCREEN_ROUTE // Admin also uses home for now
        }
    }
    
    override fun onTriggerEvent(event: AppEntryViewEvent) {
        when (event) {
            is AppEntryViewEvent.InitializeApp -> {
                initializeApp()
            }
            is AppEntryViewEvent.CheckAuthenticationStatus -> {
                checkAuthenticationStatus()
            }
        }
    }
    
    private fun initializeApp() {
        viewModelScope.launch {
            try {
                viewModelState.update { it.copy(isLoading = true) }
                
                // Simulate initialization delay (splash screen)
                kotlinx.coroutines.delay(2000)
                
                // Check if user is already authenticated
                checkAuthenticationStatus()
                
            } catch (e: Exception) {
                viewModelState.update { 
                    it.copy(
                        isLoading = false,
                        error = e.message
                    )
                }
            }
        }
    }
    
    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            try {
                val isAuthenticated = authUseCase.isAuthenticated()
                
                if (isAuthenticated) {
                    // Get current user
                    authUseCase.getCurrentUser().collect { result ->
                        when (result) {
                            is Result.Success -> {
                                val user = result.data
                                if (user != null) {
                                    viewModelState.update {
                                        it.copy(
                                            authenticationState = AuthenticationState.Authenticated(user),
                                            isLoading = false,
                                            startDestination = getHomeDestinationForRole(user.role)
                                        )
                                    }
                                } else {
                                    // User is null, treat as unauthenticated
                                    viewModelState.update {
                                        it.copy(
                                            authenticationState = AuthenticationState.Unauthenticated,
                                            isLoading = false,
                                            startDestination = DestinationRoutes.ROOT_AUTH_SCREEN_ROUTE
                                        )
                                    }
                                }
                            }
                            is Result.ServerError -> {
                                viewModelState.update {
                                    it.copy(
                                        authenticationState = AuthenticationState.Error(result.message),
                                        isLoading = false,
                                        error = result.message,
                                        startDestination = DestinationRoutes.ROOT_AUTH_SCREEN_ROUTE
                                    )
                                }
                            }
                            is Result.Init -> {
                                // Keep loading state
                            }
                        }
                    }
                } else {
                    // User is not authenticated, navigate to auth
                    viewModelState.update {
                        it.copy(
                            authenticationState = AuthenticationState.Unauthenticated,
                            isLoading = false,
                            startDestination = DestinationRoutes.ROOT_AUTH_SCREEN_ROUTE
                        )
                    }
                }
            } catch (e: Exception) {
                viewModelState.update {
                    it.copy(
                        authenticationState = AuthenticationState.Error(e.message ?: "Unknown error"),
                        isLoading = false,
                        error = e.message,
                        startDestination = DestinationRoutes.ROOT_AUTH_SCREEN_ROUTE
                    )
                }
            }
        }
    }

    sealed interface AppEntryViewEvent : ViewEvent {
        object InitializeApp : AppEntryViewEvent
        object CheckAuthenticationStatus : AppEntryViewEvent
    }

    data class AppEntryViewState(
        val authenticationState: AuthenticationState = AuthenticationState.Loading,
        val isLoading: Boolean = true,
        val error: String? = null,
        val startDestination: String = "splash"
    ) : ViewState() {
        val isAuthenticated: Boolean
            get() = authenticationState is AuthenticationState.Authenticated
        
        val currentUser: User?
            get() = (authenticationState as? AuthenticationState.Authenticated)?.user
        
        val showSplash: Boolean
            get() = isLoading && startDestination == "splash"
    }

    data class AppEntryViewModelState(
        val authenticationState: AuthenticationState = AuthenticationState.Loading,
        val isLoading: Boolean = true,
        val error: String? = null,
        val startDestination: String = "splash"
    ) : ViewModelState() {
        override fun toUiState(): ViewState {
            return AppEntryViewState(
                authenticationState = authenticationState,
                isLoading = isLoading,
                error = error,
                startDestination = startDestination
            )
        }
    }
} 