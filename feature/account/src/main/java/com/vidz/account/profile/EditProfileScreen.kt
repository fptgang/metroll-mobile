package com.vidz.account.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
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
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            CircularProgressIndicator(
                modifier = Modifier.size(48.dp),
                strokeWidth = 4.dp,
                color = MaterialTheme.colorScheme.primary
            )
            Text(
                text = "Đang tải hồ sơ...",
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
                text = "Không thể tải hồ sơ",
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
            .padding(horizontal = 20.dp)
            .padding(top = 8.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Current Profile Info Section
        CurrentProfileSection(account = account)
        
        // Edit Form Section
        EditFormSection(
            fullName = fullName,
            phoneNumber = phoneNumber,
            fullNameError = fullNameError,
            phoneNumberError = phoneNumberError,
            onFullNameChange = onFullNameChange,
            onPhoneNumberChange = onPhoneNumberChange
        )
        
        // Save Button Section
        SaveButtonSection(
            isUpdating = isUpdating,
            onSaveClick = onSaveClick,
            canSave = fullNameError == null && phoneNumberError == null && fullName.isNotBlank()
        )
    }
}

@Composable
private fun CurrentProfileSection(
    account: Account,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SectionHeader(title = "Thông tin hiện tại")
        
        ReadOnlyInfoItem(
            icon = Icons.Default.Email,
            label = "Email",
            value = account.email
        )
    }
}

@Composable
private fun EditFormSection(
    fullName: String,
    phoneNumber: String,
    fullNameError: String?,
    phoneNumberError: String?,
    onFullNameChange: (String) -> Unit,
    onPhoneNumberChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SectionHeader(title = "Chỉnh sửa thông tin")
        
        Column(
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
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
                supportingText = fullNameError?.let { { 
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    ) 
                } },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
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
                supportingText = phoneNumberError?.let { { 
                    Text(
                        text = it,
                        color = MaterialTheme.colorScheme.error
                    ) 
                } },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                placeholder = { 
                    Text(
                        text = "Tùy chọn",
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                    ) 
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = MaterialTheme.colorScheme.primary,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                )
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
    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        SectionHeader(title = "Lưu thay đổi")
        
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Button(
                onClick = onSaveClick,
                enabled = canSave && !isUpdating,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            ) {
                if (isUpdating) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.onPrimary
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Đang lưu...",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Save,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Text(
                        text = "Lưu thay đổi",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
            
            // Helper text
            if (!canSave) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                ) {
                    Text(
                        text = "Vui lòng nhập đầy đủ thông tin hợp lệ",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                ) {
                    Text(
                        text = "Sẵn sàng lưu thay đổi",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer,
                        fontWeight = FontWeight.Medium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                        textAlign = TextAlign.Center
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
private fun ReadOnlyInfoItem(
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