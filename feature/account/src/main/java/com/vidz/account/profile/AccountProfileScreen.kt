package com.vidz.account.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.base.extensions.toFormattedDate
import com.vidz.domain.model.Account
import com.vidz.domain.model.AccountRole

@Composable
fun AccountProfileScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    onShowSnackbar: (String) -> Unit,
    accountProfileViewModel: AccountProfileViewModel = hiltViewModel(),
) {
    val accountProfileUiState = accountProfileViewModel.uiState.collectAsStateWithLifecycle()
    AccountProfileScreen(
        navController = navController,
        accountProfileUiState = accountProfileUiState,
        onRefresh = { accountProfileViewModel.onTriggerEvent(AccountProfileViewModel.AccountProfileViewEvent.RefreshProfile) },
        onEditProfile = { navController.navigate(com.vidz.base.navigation.DestinationRoutes.EDIT_PROFILE_SCREEN_ROUTE) },
        onLogout = { accountProfileViewModel.onTriggerEvent(AccountProfileViewModel.AccountProfileViewEvent.Logout) },
        onShowSnackbar = onShowSnackbar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountProfileScreen(
    navController: NavController,
    accountProfileUiState: State<AccountProfileViewModel.AccountProfileViewState>,
    onRefresh: () -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    //region Define Var
    val uiState = accountProfileUiState.value
    val scrollState = rememberScrollState()
    
    // Handle logout success - navigate to auth
    LaunchedEffect(uiState.logoutSuccess) {
        if (uiState.logoutSuccess) {
            onShowSnackbar("Logged out successfully")
            // Navigate to auth screen (clearing back stack)
            navController.navigate(com.vidz.base.navigation.DestinationRoutes.ROOT_AUTH_SCREEN_ROUTE) {
                popUpTo(0) { inclusive = true }
            }
        }
    }
    
    // Handle logout error
    LaunchedEffect(uiState.logoutError) {
        uiState.logoutError?.let { error ->
            onShowSnackbar(error)
        }
    }
    //endregion

    //region Event Handler
    // Removed back click handler since this is a bottom bar screen
    //endregion

    //region ui
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Profile") },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { paddingValues ->
        
        when {
            uiState.isLoading -> {
                LoadingScreen(modifier = Modifier.padding(paddingValues))
            }
            uiState.error != null -> {
                ErrorScreen(
                    error = uiState.error,
                    onRetry = onRefresh,
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.account != null -> {
                ProfileContent(
                    account = uiState.account,
                    onRefresh = onRefresh,
                    onEditProfile = onEditProfile,
                    onLogout = onLogout,
                    isLoggingOut = uiState.isLoggingOut,
                    modifier = Modifier
                        .padding(paddingValues)
                        .verticalScroll(scrollState)
                )
            }
        }
    }
    
    //region Dialog and Sheet
    //endregion
    //endregion
}

@Composable
private fun LoadingScreen(
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            CircularProgressIndicator()
            Text(
                text = "Đang tải thông tin...",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun ErrorScreen(
    error: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(32.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.error,
                modifier = Modifier.size(64.dp)
            )
            Text(
                text = "Không tải được hồ sơ",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            Button(
                onClick = onRetry,
                modifier = Modifier.padding(top = 8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Thử lại")
            }
        }
    }
}

@Composable
private fun ProfileContent(
    account: Account,
    onRefresh: () -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    isLoggingOut: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 80.dp), // Add extra bottom padding for bottom navigation
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Profile Header
        ProfileHeader(account = account)
        
        // Profile Information Card
        ProfileInformationCard(account = account)
        
        // Account Status Card
        AccountStatusCard(account = account)
        
        // Actions
        ActionsSection(
            onRefresh = onRefresh, 
            onEditProfile = onEditProfile, 
            onLogout = onLogout,
            isLoggingOut = isLoggingOut
        )
        
        // Add spacer to ensure content is above bottom navigation
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun ProfileHeader(
    account: Account,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Profile Avatar
            Surface(
                modifier = Modifier.size(80.dp),
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer
            ) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = account.fullName.take(2).uppercase(),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
            
            // Profile Name and Email
            Text(
                text = account.fullName.ifEmpty { "Người dùng không xác định" },
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = account.email,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
            
            // Role Chip
            RoleChip(role = account.role)
        }
    }
}

@Composable
private fun ProfileInformationCard(
    account: Account,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Thông tin cá nhân",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            ProfileInfoRow(
                icon = Icons.Default.Person,
                label = "Họ và tên",
                value = account.fullName.ifEmpty { "Chưa cung cấp" }
            )
            
            ProfileInfoRow(
                icon = Icons.Default.Email,
                label = "Email",
                value = account.email
            )
            
            ProfileInfoRow(
                icon = Icons.Default.Phone,
                label = "Số điện thoại",
                value = account.phoneNumber.ifEmpty { "Chưa cung cấp" }
            )
            
            ProfileInfoRow(
                icon = Icons.Default.Security,
                label = "Mã tài khoản",
                value = account.id.ifEmpty { "Không rõ" }
            )
        }
    }
}

@Composable
private fun AccountStatusCard(
    account: Account,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Trạng thái tài khoản",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            // Account Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Trạng thái",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                StatusChip(isActive = account.active)
            }
            
            // Created Date
            if (account.createdAt.isNotEmpty()) {
                ProfileInfoRow(
                    icon = Icons.Default.Person,
                    label = "Là thành viên từ",
                    value = formatDate(account.createdAt)
                )
            }
            
            // Last Updated
            if (account.updatedAt.isNotEmpty()) {
                ProfileInfoRow(
                    icon = Icons.Default.Refresh,
                    label = "Cập nhật lần cuối",
                    value = formatDate(account.updatedAt)
                )
            }
        }
    }
}

@Composable
private fun ActionsSection(
    onRefresh: () -> Unit,
    onEditProfile: () -> Unit,
    onLogout: () -> Unit,
    isLoggingOut: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Tùy chọn",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            Button(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Chỉnh sửa hồ sơ")
            }
            
            OutlinedButton(
                onClick = onRefresh,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text("Làm mới thông tin")
            }
            
            // Logout Button
            OutlinedButton(
                onClick = onLogout,
                enabled = !isLoggingOut,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isLoggingOut) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đang đăng xuất...")
                } else {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Đăng xuất")
                }
            }
        }
    }
}

@Composable
private fun ProfileInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(20.dp)
        )
        
        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.Medium
            )
        }
    }
}

@Composable
private fun RoleChip(
    role: AccountRole,
    modifier: Modifier = Modifier
) {
    val (text, color) = when (role) {
        AccountRole.ADMIN -> "Admin" to MaterialTheme.colorScheme.error
        AccountRole.STAFF -> "Nhân viên" to MaterialTheme.colorScheme.primary
        AccountRole.CUSTOMER -> "Người dùng" to MaterialTheme.colorScheme.secondary
    }
    
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatusChip(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val (text, color) = if (isActive) {
        "Đang hoạt động" to MaterialTheme.colorScheme.primary
    } else {
        "Không khả dụng" to MaterialTheme.colorScheme.error
    }
    
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.small,
        color = color.copy(alpha = 0.1f)
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            style = MaterialTheme.typography.labelMedium,
            color = color,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatDate(dateString: String): String {
    return try {
        // If dateString is in milliseconds format, use the extension directly
        if (dateString.toLongOrNull() != null) {
            dateString.toFormattedDate("MMM dd, yyyy")
        } else {
            // If it's in ISO format or other string format, parse it first
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val date = inputFormat.parse(dateString)
            date?.time?.toString()?.toFormattedDate("MMM dd, yyyy") ?: dateString
        }
    } catch (e: Exception) {
        dateString
    }
} 
