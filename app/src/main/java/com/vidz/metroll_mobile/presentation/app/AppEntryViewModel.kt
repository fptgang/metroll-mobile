package com.vidz.metroll_mobile.presentation.app

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vidz.domain.model.AuthenticationState
import com.vidz.domain.model.User
import com.vidz.domain.model.UserRole
import com.vidz.domain.usecase.AuthUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AppEntryViewModel @Inject constructor(
    private val authUseCase: AuthUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(AppEntryViewState())
    val uiState: StateFlow<AppEntryViewState> = _uiState.asStateFlow()
    
    init {
        observeAuthenticationState()
    }
    
    private fun observeAuthenticationState() {
        viewModelScope.launch {
            authUseCase.getAuthenticationState().collect { authState ->
                _uiState.value = _uiState.value.copy(
                    authenticationState = authState,
                    isLoading = authState is AuthenticationState.Loading,
                    startDestination = getStartDestinationForAuthState(authState)
                )
            }
        }
    }
    
    private fun getStartDestinationForAuthState(authState: AuthenticationState): String {
        return when (authState) {
            is AuthenticationState.Loading -> "splash"
            is AuthenticationState.Unauthenticated -> "auth_graph"
            is AuthenticationState.Authenticated -> {
                getHomeDestinationForRole(authState.user.role)
            }
            is AuthenticationState.Error -> "auth_graph"
        }
    }
    
    private fun getHomeDestinationForRole(role: UserRole): String {
        return when (role) {
            UserRole.CUSTOMER -> "customer_home_graph"
            UserRole.STAFF -> "staff_home_graph"
            UserRole.ADMIN -> "admin_dashboard_graph"
        }
    }
    
    fun onTriggerEvent(event: AppEntryEvent) {
        when (event) {
            is AppEntryEvent.InitializeApp -> {
                initializeApp()
            }
            is AppEntryEvent.CheckAuthenticationStatus -> {
                checkAuthenticationStatus()
            }
        }
    }
    
    private fun initializeApp() {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true)
                
                // Simulate initialization delay (splash screen)
                kotlinx.coroutines.delay(2000)
                
                // Check if user is already authenticated
                checkAuthenticationStatus()
                
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }
    
    private fun checkAuthenticationStatus() {
        viewModelScope.launch {
            try {
                val isAuthenticated = authUseCase.isAuthenticated()
                val currentUser = authUseCase.getCurrentUser()
                
                if (isAuthenticated && currentUser != null) {
                    // User is authenticated, navigate to appropriate home
                    _uiState.value = _uiState.value.copy(
                        authenticationState = AuthenticationState.Authenticated(currentUser),
                        isLoading = false,
                        startDestination = getHomeDestinationForRole(currentUser.role)
                    )
                } else {
                    // User is not authenticated, navigate to auth
                    _uiState.value = _uiState.value.copy(
                        authenticationState = AuthenticationState.Unauthenticated,
                        isLoading = false,
                        startDestination = "auth_graph"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    authenticationState = AuthenticationState.Error(e.message ?: "Unknown error"),
                    isLoading = false,
                    error = e.message,
                    startDestination = "auth_graph"
                )
            }
        }
    }
}

data class AppEntryViewState(
    val authenticationState: AuthenticationState = AuthenticationState.Loading,
    val isLoading: Boolean = true,
    val error: String? = null,
    val startDestination: String = "splash"
) {
    val isAuthenticated: Boolean
        get() = authenticationState is AuthenticationState.Authenticated
    
    val currentUser: User?
        get() = (authenticationState as? AuthenticationState.Authenticated)?.user
    
    val showSplash: Boolean
        get() = isLoading && startDestination == "splash"
}

sealed class AppEntryEvent {
    object InitializeApp : AppEntryEvent()
    object CheckAuthenticationStatus : AppEntryEvent()
} 