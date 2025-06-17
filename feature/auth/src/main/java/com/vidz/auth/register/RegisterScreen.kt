package com.vidz.auth.register

import android.annotation.SuppressLint
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.auth.components.AuthTopAppBar
import com.vidz.base.components.PrimaryButton

@Composable
fun RegisterScreenRoot(
    navController: NavController,
    modifier: Modifier = Modifier,
    registerViewModel: RegisterViewModel = hiltViewModel(),
    onShowSnackbar: ((String) -> Unit)? = null,
    onNavigateToLogin: (() -> Unit)? = null
) {
    val registerUiState = registerViewModel.uiState.collectAsStateWithLifecycle()
    RegisterScreen(
        navController = navController,
        registerUiState = registerUiState,
        onShowSnackbar = onShowSnackbar,
        onNavigateToLogin = onNavigateToLogin,
        onEvent = registerViewModel::onTriggerEvent
    )
}

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    navController: NavController,
    registerUiState: State<RegisterViewModel.RegisterUiState>,
    onShowSnackbar: ((String) -> Unit)? = null,
    onNavigateToLogin: (() -> Unit)? = null,
    onEvent: ((RegisterViewModel.RegisterEvent) -> Unit)? = null
) {
    //region Define Var
    val uiState = registerUiState.value
    var passwordVisible by remember { mutableStateOf(false) }
    var confirmPasswordVisible by remember { mutableStateOf(false) }
    //endregion

    //region Event Handler
    val onFirstNameChanged: (String) -> Unit = { firstName ->
        onEvent?.invoke(RegisterViewModel.RegisterEvent.UpdateFirstName(firstName))
    }

    val onLastNameChanged: (String) -> Unit = { lastName ->
        onEvent?.invoke(RegisterViewModel.RegisterEvent.UpdateLastName(lastName))
    }

    val onEmailChanged: (String) -> Unit = { email ->
        onEvent?.invoke(RegisterViewModel.RegisterEvent.UpdateEmail(email))
    }

    val onPasswordChanged: (String) -> Unit = { password ->
        onEvent?.invoke(RegisterViewModel.RegisterEvent.UpdatePassword(password))
    }

    val onConfirmPasswordChanged: (String) -> Unit = { confirmPassword ->
        onEvent?.invoke(RegisterViewModel.RegisterEvent.UpdateConfirmPassword(confirmPassword))
    }

    val onRegisterClick: () -> Unit = {
        onEvent?.invoke(RegisterViewModel.RegisterEvent.Register)
    }

    val onLoginClick: () -> Unit = {
        onNavigateToLogin?.invoke()
    }

    val onBackClick: () -> Unit = {
        navController.popBackStack()
    }
    //endregion

    // Handle registration success
    LaunchedEffect(uiState.isRegistrationSuccessful) {
        if (uiState.isRegistrationSuccessful) {
            onShowSnackbar?.invoke("Registration successful! Please login with your credentials.")
            onNavigateToLogin?.invoke()
        }
    }

    //region ui
    Scaffold(
        topBar = {
            AuthTopAppBar(
                title = "Create Account",
                onBackClick = {onBackClick.invoke()}
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(vertical = 32.dp)
            ) {
                Text(
                    text = "Join Our Platform",
                    style = MaterialTheme.typography.displaySmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = "Create your account to get started",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
            }
            
            Spacer(modifier = Modifier.height(24.dp))

            // Form Section
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Name Fields Row
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // First Name Field
                    OutlinedTextField(
                        value = uiState.firstName,
                        onValueChange = onFirstNameChanged,
                        modifier = Modifier.weight(1f),
                        label = { Text("First Name") },
                        placeholder = { Text("First name") },
                        leadingIcon = {
                            Icon(
                                imageVector = Icons.Default.Person,
                                contentDescription = "First Name",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        },
                        isError = uiState.firstNameError != null,
                        supportingText = uiState.firstNameError?.let { { Text(it) } },
                        singleLine = true
                    )

                    // Last Name Field
                    OutlinedTextField(
                        value = uiState.lastName,
                        onValueChange = onLastNameChanged,
                        modifier = Modifier.weight(1f),
                        label = { Text("Last Name") },
                        placeholder = { Text("Last name") },
                        isError = uiState.lastNameError != null,
                        supportingText = uiState.lastNameError?.let { { Text(it) } },
                        singleLine = true
                    )
                }

                // Email Field
                OutlinedTextField(
                    value = uiState.email,
                    onValueChange = onEmailChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Email Address") },
                    placeholder = { Text("Enter your email") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Email,
                            contentDescription = "Email",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                    isError = uiState.emailError != null,
                    supportingText = uiState.emailError?.let { { Text(it) } },
                    singleLine = true
                )

                // Password Field
                OutlinedTextField(
                    value = uiState.password,
                    onValueChange = onPasswordChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Password") },
                    placeholder = { Text("Create a password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Password",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { passwordVisible = !passwordVisible }) {
                            Icon(
                                imageVector = if (passwordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (passwordVisible) "Hide password" else "Show password",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = uiState.passwordError != null,
                    supportingText = uiState.passwordError?.let { { Text(it) } },
                    singleLine = true
                )

                // Confirm Password Field
                OutlinedTextField(
                    value = uiState.confirmPassword,
                    onValueChange = onConfirmPasswordChanged,
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text("Confirm Password") },
                    placeholder = { Text("Confirm your password") },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Lock,
                            contentDescription = "Confirm Password",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    },
                    trailingIcon = {
                        IconButton(onClick = { confirmPasswordVisible = !confirmPasswordVisible }) {
                            Icon(
                                imageVector = if (confirmPasswordVisible) Icons.Default.Visibility else Icons.Default.VisibilityOff,
                                contentDescription = if (confirmPasswordVisible) "Hide password" else "Show password",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    },
                    visualTransformation = if (confirmPasswordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                    isError = uiState.confirmPasswordError != null,
                    supportingText = uiState.confirmPasswordError?.let { { Text(it) } },
                    singleLine = true
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Register Button
                PrimaryButton(
                    onClick = onRegisterClick,
                    modifier = Modifier.fillMaxWidth(),
                    isLoading = uiState.isLoading,
                    enabled = !uiState.isLoading && uiState.isFormValid,
                    text = "Create Account"
                )

                // Error Message
                uiState.error?.let { error ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                MaterialTheme.colorScheme.errorContainer,
                                MaterialTheme.shapes.small
                            )
                            .padding(16.dp)
                    ) {
                        Text(
                            text = error,
                            color = MaterialTheme.colorScheme.onErrorContainer,
                            style = MaterialTheme.typography.bodyMedium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Login Link
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Already have an account?",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.width(4.dp))
                TextButton(onClick = onLoginClick) {
                    Text(
                        text = "Sign In",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }

    //region Dialog and Sheet
    //endregion
    //endregion
}
