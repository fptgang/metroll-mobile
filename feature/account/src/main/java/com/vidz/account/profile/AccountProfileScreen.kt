package com.vidz.account.profile

import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.outlined.AccountCircle
import androidx.compose.material.icons.outlined.Schedule
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
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
                title = { 
                    Text(
                        text = "Profile",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.surface
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Đang tải thông tin...",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontWeight = FontWeight.Medium
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
            verticalArrangement = Arrangement.spacedBy(24.dp),
            modifier = Modifier.padding(40.dp)
        ) {
            Box(
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.errorContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.size(40.dp)
                )
            }
            
            Text(
                text = "Không tải được hồ sơ",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )
            
            Text(
                text = error,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
            )
            
            FilledTonalButton(
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
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Profile Header
        ProfileHeader(account = account)
        
        // Profile Information Section
        ProfileInformationSection(account = account)
        
        // Account Status Section
        AccountStatusSection(account = account)
        
        // Actions Section
        ActionsSection(
            onRefresh = onRefresh, 
            onEditProfile = onEditProfile, 
            onLogout = onLogout,
            isLoggingOut = isLoggingOut
        )
    }
}

@Composable
private fun ProfileHeader(
    account: Account,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Profile Avatar
        Box(
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = account.fullName.take(2).uppercase(),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }
        
        // Profile Name and Details
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = account.fullName.ifEmpty { "Người dùng không xác định" },
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.SemiBold,
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
private fun ProfileInformationSection(
    account: Account,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SectionHeader(title = "Thông tin cá nhân")
        
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            ProfileInfoItem(
                icon = Icons.Default.Person,
                label = "Họ và tên",
                value = account.fullName.ifEmpty { "Chưa cung cấp" }
            )
            
            ProfileInfoItem(
                icon = Icons.Default.Email,
                label = "Email",
                value = account.email
            )
            
            ProfileInfoItem(
                icon = Icons.Default.Phone,
                label = "Số điện thoại",
                value = account.phoneNumber.ifEmpty { "Chưa cung cấp" }
            )
            
            ProfileInfoItem(
                icon = Icons.Default.Security,
                label = "Mã tài khoản",
                value = account.id.ifEmpty { "Không rõ" }
            )
        }
    }
}

@Composable
private fun AccountStatusSection(
    account: Account,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SectionHeader(title = "Trạng thái tài khoản")
        
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Account Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.AccountCircle,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Trạng thái",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                StatusChip(isActive = account.active)
            }
            
            // Created Date
            if (account.createdAt.isNotEmpty()) {
                ProfileInfoItem(
                    icon = Icons.Outlined.Schedule,
                    label = "Là thành viên từ",
                    value = formatDate(account.createdAt)
                )
            }
            
            // Last Updated
            if (account.updatedAt.isNotEmpty()) {
                ProfileInfoItem(
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
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SectionHeader(title = "Tùy chọn")
        
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Button(
                onClick = onEditProfile,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Chỉnh sửa hồ sơ",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            
            FilledTonalButton(
                onClick = onRefresh,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Làm mới thông tin",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium
                )
            }
            
            // Logout Button
            OutlinedButton(
                onClick = onLogout,
                enabled = !isLoggingOut,
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                ),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp)
            ) {
                if (isLoggingOut) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đang đăng xuất...",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.ExitToApp,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Đăng xuất",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
private fun SectionHeader(
    title: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleLarge,
        fontWeight = FontWeight.SemiBold,
        color = MaterialTheme.colorScheme.onSurface,
        modifier = modifier
    )
}

@Composable
private fun ProfileInfoItem(
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
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = value,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun RoleChip(
    role: AccountRole,
    modifier: Modifier = Modifier
) {
    val (text, containerColor, contentColor) = when (role) {
        AccountRole.ADMIN -> Triple(
            "Admin",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
        AccountRole.STAFF -> Triple(
            "Nhân viên",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
        AccountRole.CUSTOMER -> Triple(
            "Người dùng",
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer
        )
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = containerColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun StatusChip(
    isActive: Boolean,
    modifier: Modifier = Modifier
) {
    val (text, containerColor, contentColor) = if (isActive) {
        Triple(
            "Hoạt động",
            MaterialTheme.colorScheme.primaryContainer,
            MaterialTheme.colorScheme.onPrimaryContainer
        )
    } else {
        Triple(
            "Không khả dụng",
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer
        )
    }
    
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = containerColor
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            style = MaterialTheme.typography.labelLarge,
            color = contentColor,
            fontWeight = FontWeight.Medium
        )
    }
}

private fun formatDate(dateString: String): String {
    return try {
        // Handle Unix timestamp format (seconds with decimal places)
        val timestamp = dateString.toDoubleOrNull()
        if (timestamp != null) {
            // Convert seconds to milliseconds for Date formatting
            val milliseconds = (timestamp * 1000).toLong()
            val date = java.util.Date(milliseconds)
            val formatter = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
            formatter.format(date)
        } else {
            // Fallback for other formats
            val inputFormat = java.text.SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", java.util.Locale.getDefault())
            val date = inputFormat.parse(dateString)
            val formatter = java.text.SimpleDateFormat("dd MMM yyyy", java.util.Locale.getDefault())
            date?.let { formatter.format(it) } ?: dateString
        }
    } catch (e: Exception) {
        // If all parsing fails, return original string
        dateString
    }
} 
