package com.vidz.account.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.base.components.TopAppBarWithBack
import com.vidz.domain.model.Account

@Composable
fun EditProfileScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    onShowSnackbar: (String) -> Unit,
    editProfileViewModel: EditProfileViewModel = hiltViewModel(),
) {
    val editProfileUiState = editProfileViewModel.uiState.collectAsStateWithLifecycle()
    
    EditProfileScreen(
        navController = navController,
        editProfileUiState = editProfileUiState,
        onSaveProfile = { fullName, phoneNumber ->
            editProfileViewModel.onTriggerEvent(
                EditProfileViewModel.EditProfileViewEvent.SaveProfile(fullName, phoneNumber)
            )
        },
        onShowSnackbar = onShowSnackbar
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    navController: NavController,
    editProfileUiState: State<EditProfileViewModel.EditProfileViewState>,
    onSaveProfile: (String, String) -> Unit,
    onShowSnackbar: (String) -> Unit
) {
    //region Define Var
    val uiState = editProfileUiState.value
    val scrollState = rememberScrollState()
    
    var fullName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    var fullNameError by remember { mutableStateOf<String?>(null) }
    var phoneNumberError by remember { mutableStateOf<String?>(null) }
    
    // Initialize fields when account data is loaded
    LaunchedEffect(uiState.account) {
        uiState.account?.let { account ->
            fullName = account.fullName
            phoneNumber = account.phoneNumber
        }
    }
    
    // Handle save success
    LaunchedEffect(uiState.isUpdateSuccess) {
        if (uiState.isUpdateSuccess) {
            onShowSnackbar("Cập nhật tài khoản thành công")
            navController.popBackStack()
        }
    }
    
    // Handle save error
    LaunchedEffect(uiState.updateError) {
        uiState.updateError?.let { error ->
            onShowSnackbar(error)
        }
    }
    //endregion

    //region Event Handler
    val handleBackClick = {
        navController.popBackStack()
        Unit
    }
    
    val handleSaveClick = {
        // Validate inputs
        fullNameError = when {
            fullName.isBlank() -> "Không được để trống"
            fullName.length < 2 -> "Tối thiểu 2 kí tự"
            else -> null
        }
        
        phoneNumberError = when {
            phoneNumber.isNotBlank() && phoneNumber.length < 8 -> "Tối thiểu 8 kí tự"
            phoneNumber.isNotBlank() && !phoneNumber.matches(Regex("^[0-9+\\-\\s()]+$")) -> "Invalid phone number format"
            else -> null
        }
        
        // Save if no errors
        if (fullNameError == null && phoneNumberError == null) {
            onSaveProfile(fullName.trim(), phoneNumber.trim())
        }
    }
    //endregion

    //region ui
    Scaffold(
        topBar = {
            TopAppBarWithBack(
                title = "Chỉnh sửa thông tin",
                onBackClick = handleBackClick
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
                    onRetry = { /* Could add retry logic here */ },
                    modifier = Modifier.padding(paddingValues)
                )
            }
            uiState.account != null -> {
                EditProfileContent(
                    account = uiState.account,
                    fullName = fullName,
                    phoneNumber = phoneNumber,
                    fullNameError = fullNameError,
                    phoneNumberError = phoneNumberError,
                    isUpdating = uiState.isUpdating,
                    onFullNameChange = { 
                        fullName = it
                        fullNameError = null
                    },
                    onPhoneNumberChange = { 
                        phoneNumber = it
                        phoneNumberError = null
                    },
                    onSaveClick = handleSaveClick,
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
                text = "Đang tải hồ sơ...",
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
                text = "Không thể tải hồ sơ",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium
            )
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun EditProfileContent(
    account: Account,
    fullName: String,
    phoneNumber: String,
    fullNameError: String?,
    phoneNumberError: String?,
    isUpdating: Boolean,
    onFullNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    onSaveClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .padding(bottom = 80.dp), // Add extra bottom padding for bottom navigation
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Current Profile Info Card
        CurrentProfileCard(account = account)
        
        // Edit Form Card
        EditFormCard(
            fullName = fullName,
            phoneNumber = phoneNumber,
            fullNameError = fullNameError,
            phoneNumberError = phoneNumberError,
            onFullNameChange = onFullNameChange,
            onPhoneNumberChange = onPhoneNumberChange
        )
        
        // Save Button
        SaveButtonSection(
            isUpdating = isUpdating,
            onSaveClick = onSaveClick,
            canSave = fullNameError == null && phoneNumberError == null && fullName.isNotBlank()
        )
        
        // Add spacer to ensure content is above bottom navigation
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
private fun CurrentProfileCard(
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
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Thông tin hiện tại",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            ReadOnlyInfoRow(
                icon = Icons.Default.Email,
                label = "Email",
                value = account.email
            )
        }
    }
}

@Composable
private fun EditFormCard(
    fullName: String,
    phoneNumber: String,
    fullNameError: String?,
    phoneNumberError: String?,
    onFullNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
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
                text = "Chỉnh sửa thông tin",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Medium
            )
            
            // Full Name Field
            OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = { Text("Họ và tên") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = null
                    )
                },
                isError = fullNameError != null,
                supportingText = fullNameError?.let { { Text(it) } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true
            )
            
            // Phone Number Field
            OutlinedTextField(
                value = phoneNumber,
                onValueChange = onPhoneNumberChange,
                label = { Text("Số điện thoại") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Phone,
                        contentDescription = null
                    )
                },
                isError = phoneNumberError != null,
                supportingText = phoneNumberError?.let { { Text(it) } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { Text("Optional") }
            )
        }
    }
}

@Composable
private fun SaveButtonSection(
    isUpdating: Boolean,
    onSaveClick: () -> Unit,
    canSave: Boolean,
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
            Button(
                onClick = onSaveClick,
                enabled = canSave && !isUpdating,
                modifier = Modifier.fillMaxWidth()
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Saving...")
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Lưu thay đổi")
                }
            }
            
            if (!canSave) {
                Text(
                    text = "Vui lòng nhập đẩy đủ thông tin",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
private fun ReadOnlyInfoRow(
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