
import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.currentBackStackEntryAsState
import com.vidz.base.components.NotificationPermissionDialog
import com.vidz.base.components.PermissionManager
import com.vidz.base.components.PermissionState
import com.vidz.base.components.PermissionStatus
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.base.navigation.DestinationRoutes.HOME_SCREEN_ROUTE
import com.vidz.metroll.core.data.BuildConfig
import com.vidz.metroll_mobile.presentation.app.MetrollAppViewModel
import com.vidz.theme.MetrollTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
@SuppressLint(
    "UnusedMaterial3ScaffoldPaddingParameter",
    "RestrictedApi"
)
fun MetrollApp(
    shouldNavigateToCart: Boolean = false,
    isFromNotification: Boolean = false,
    metrollAppViewModel: MetrollAppViewModel = hiltViewModel()
) {
    val navController = rememberMetrollNavController()
    val uiState = metrollAppViewModel.uiState.collectAsStateWithLifecycle().value
    val currentBackStackEntry = navController.navController.currentBackStackEntryAsState()

    //region Define Var
    var showNotificationPermissionDialog by remember { mutableStateOf(false) }
    var requestNotificationPermission by remember { mutableStateOf<(() -> Unit)?>(null) }
    var notificationPermissionState by remember { mutableStateOf<PermissionState?>(null) }
    //endregion

    //region Event Handler
    val onNotificationPermissionRequest: () -> Unit = {
        showNotificationPermissionDialog = false
        requestNotificationPermission?.invoke()
    }
    val onNotificationPermissionDismiss = {
        showNotificationPermissionDialog = false
    }

    val onPermissionResult: (PermissionState) -> Unit = { state ->
        notificationPermissionState = state
        Log.d("MetrollApp", "Notification permission status: ${state.status}")
    }
    
    val onRequestPermissionCallback = { requestFunction: () -> Unit ->
        requestNotificationPermission = requestFunction
    }
    //endregion

    // Track both the route and the navController itself to ensure proper recomposition
    LaunchedEffect(currentBackStackEntry.value?.destination?.route) {
        val currentRoute = navController.navController.currentDestination?.route
        Log.d(
            "MetrollApp",
            "Current destination: $currentRoute"
        )
        metrollAppViewModel.onTriggerEvent(
            MetrollAppViewModel.MetrollViewEvent.ObserveNavDestination(navController.navController)
        )
    }

    // Handle navigation to ticket from notification
    LaunchedEffect(shouldNavigateToCart) {
        if (shouldNavigateToCart) {
            delay(500) // Small delay to ensure UI is ready
            navController.navigateToNavigationBar(DestinationRoutes.TICKET_PURCHASE_SCREEN_ROUTE)
        }
    }

    // Handle notification permission on app start (only if NOT from notification)
    LaunchedEffect(notificationPermissionState, isFromNotification) {
        notificationPermissionState?.let { state ->
            if (state.status == PermissionStatus.NOT_REQUESTED && !isFromNotification) {
                // Show custom dialog for better UX, but not when coming from notification
                showNotificationPermissionDialog = true
            }
        }
    }

    // Track if back was already pressed once
    val backPressedState = remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current
    // Update backStackList whenever the back stack changes

    val currentRoute = navController.navController.currentDestination?.route
    if (currentRoute?.startsWith(HOME_SCREEN_ROUTE) == true || 
        currentRoute?.startsWith(DestinationRoutes.CUSTOMER_HOME_SCREEN_ROUTE) == true) {
        DoubleBackToExitApp(
            streamUiState = uiState
        )
    }

    if (BuildConfig.DEBUG) {
        val backStack by navController.navController.currentBackStackEntryAsState()
        val backStackList = remember { mutableStateListOf<String>() }
        LaunchedEffect(backStack) {
            backStackList.clear()
            navController.navController.currentBackStack.value.forEach { entry ->
                backStackList.add(entry.destination.route ?: "Unknown")
            }
        }
        Log.d(
            "MetrollApp",
            "AppTheme:  | CurrentDestination: ${
                backStack?.destination?.route
            } | BackStack: ${
                backStackList.joinToString(" -> ")
            }"
        )
        Log.d(
            "MetrollApp",
            "BackStackTree:"
        )
        backStackList.forEachIndexed { index, route ->
            val indentation = "  ".repeat(index) // Create indentation for hierarchy
            Log.d(
                "MetrollApp",
                "$indentation├─ $route"
            )
        }
    }



    // Permission Manager - handles notification permission (invisible component)
    PermissionManager(
        permission = Manifest.permission.POST_NOTIFICATIONS,
        onPermissionResult = onPermissionResult,
        onRequestPermission = onRequestPermissionCallback
    )

    MetrollTheme(darkTheme = true ) {
        //region ui
        Scaffold(
            snackbarHost = { SnackbarHost(snackbarHostState) },
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
            bottomBar = {
                if (uiState.shouldShowBottomBar) {
                    MetrollBottomAppBar(
                        navController = navController,
                        currentRoute = navController.navController.currentDestination?.route
                    )
                }
            }
        ) { paddingvalue ->
            if (uiState.isInitialized) {
                AppNavHost(
                    navController = navController.navController,
                    startDestination = uiState.startDestination,
                    onShowSnackbar = { message ->
                        scope.launch {
                            snackbarHostState.showSnackbar(message, withDismissAction = true)
                        }
                    }
                )
            } else {
                AppLoadingScreen(
                    modifier = Modifier.padding(paddingvalue)
                )
            }
        }

        //region Dialog and Sheet
        // Show notification permission dialog
        if (showNotificationPermissionDialog) {
            NotificationPermissionDialog(
                onRequestPermission = onNotificationPermissionRequest,
                onDismiss = onNotificationPermissionDismiss
            )
        }
        //endregion
        //endregion
    }
}


data class BottomNavItem(
    val label: String,
    val icon: ImageVector,
    val route: String
)

val bottomNavItems = listOf(
    BottomNavItem(
        "Trang chủ",
        Icons.Filled.Home,
        DestinationRoutes.CUSTOMER_HOME_SCREEN_ROUTE
    ),
    BottomNavItem(
        "Tuyến",
        Icons.Filled.Search,
        DestinationRoutes.ROUTE_MANAGEMENT_SCREEN_ROUTE
    ),
    BottomNavItem(
        "Mua vé",
        Icons.AutoMirrored.Filled.List,
        DestinationRoutes.TICKET_PURCHASE_SCREEN_ROUTE
    ),
    BottomNavItem(
        "Đơn hàng",
        Icons.Filled.Receipt,
        DestinationRoutes.MY_TICKETS_SCREEN_ROUTE
    ),
    BottomNavItem(
        "Tài khoản",
        Icons.Filled.Settings,
        DestinationRoutes.ACCOUNT_PROFILE_SCREEN_ROUTE
    ),
)

@Composable
fun MetrollBottomAppBar(
    navController: BlindboxNavController,
    currentRoute: String?,
) {
    NavigationBar {
        bottomNavItems.forEach { item ->
            NavigationBarItem(
                icon = {
                    Icon(
                        item.icon,
                        contentDescription = item.label
                    )
                },
                label = { Text(item.label, style = MaterialTheme.typography
                        .labelSmall) },
                colors = NavigationBarItemColors(
                    selectedIconColor = MaterialTheme.colorScheme.primary,
                    selectedTextColor = MaterialTheme.colorScheme.primary,
                    selectedIndicatorColor = MaterialTheme.colorScheme.primaryContainer,
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    disabledIconColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f),
                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.38f)
                ),
                selected = when (item.route) {
                    DestinationRoutes.CUSTOMER_HOME_SCREEN_ROUTE -> {
                        currentRoute?.startsWith(DestinationRoutes.CUSTOMER_HOME_SCREEN_ROUTE) == true || 
                        currentRoute?.startsWith(HOME_SCREEN_ROUTE) == true
                    }
                    else -> currentRoute?.startsWith(item.route) == true
                },
                onClick = {
                    navController.navigateToNavigationBar(
                        item.route,
                        numberItemOfPage = "10"
                    )

                }
            )
        }
    }
}

@Composable
fun DoubleBackToExitApp(streamUiState: MetrollAppViewModel.MetrollViewState) {
    val context = LocalContext.current
    var backPressedOnce by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()
    val message = " Press back again to exit"

    val isEnable = streamUiState.currentNavIndex == 0

    BackHandler(isEnable) {
        if (backPressedOnce) {
            (context as? Activity)?.finish()
        } else {
            backPressedOnce = true
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
            coroutineScope.launch {
                delay(2000)
                backPressedOnce = false
            }
        }
    }
}

@Composable
fun AppLoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        MaterialTheme.colorScheme.primary.copy(alpha = 0.1f),
                        MaterialTheme.colorScheme.background
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // App Logo/Brand
            Card(
                modifier = Modifier.size(120.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "M",
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                }
            }
            
            // App Name
            Text(
                text = "Metroll",
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Loading Indicator
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                color = MaterialTheme.colorScheme.primary,
                strokeWidth = 4.dp
            )
            
            // Loading Text
            Text(
                text = "Loading...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
