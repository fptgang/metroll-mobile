package com.vidz.auth.register

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusDirection
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RegisterScreen(
    uiState: RegisterViewModel.RegisterViewState,
    onEvent: (RegisterViewModel.RegisterEvent) -> Unit,
    onNavigateToLogin: () -> Unit
) {
    //region Define Var
    val focusManager = LocalFocusManager.current
    //endregion

    //region Event Handler
    val handleDisplayNameChange = { displayName: String ->
        onEvent(RegisterViewModel.RegisterEvent.DisplayNameChanged(displayName))
    }
    
    val handleEmailChange = { email: String ->
        onEvent(RegisterViewModel.RegisterEvent.EmailChanged(email))
    }
    
    val handlePasswordChange = { password: String ->
        onEvent(RegisterViewModel.RegisterEvent.PasswordChanged(password))
    }
    
    val handleConfirmPasswordChange = { confirmPassword: String ->
        onEvent(RegisterViewModel.RegisterEvent.ConfirmPasswordChanged(confirmPassword))
    }
    
    val handlePasswordVisibilityToggle = {
        onEvent(RegisterViewModel.RegisterEvent.PasswordVisibilityToggled)
    }
    
    val handleConfirmPasswordVisibilityToggle = {
        onEvent(RegisterViewModel.RegisterEvent.ConfirmPasswordVisibilityToggled)
    }
    
    val handleRegister = {
        onEvent(RegisterViewModel.RegisterEvent.RegisterClicked)
    }
    //endregion

    //region ui
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        // Title
        Text(
            text = "Create Account",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center
        )
        
        Text(
            text = "Sign up to get started",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Display Name Field
        OutlinedTextField(
            value = uiState.displayName,
            onValueChange = handleDisplayNameChange,
            label = { Text("Display Name") },
            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) },
            singleLine = true,
            isError = uiState.displayNameError != null,
            supportingText = uiState.displayNameError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Email Field
        OutlinedTextField(
            value = uiState.email,
            onValueChange = handleEmailChange,
            label = { Text("Email") },
            leadingIcon = { Icon(Icons.Outlined.Email, contentDescription = null) },
            singleLine = true,
            isError = uiState.emailError != null,
            supportingText = uiState.emailError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Password Field
        OutlinedTextField(
            value = uiState.password,
            onValueChange = handlePasswordChange,
            label = { Text("Password") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(
                    onClick = handlePasswordVisibilityToggle
                ) {
                    Icon(
                        imageVector = if (uiState.isPasswordVisible) 
                            Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (uiState.isPasswordVisible) 
                            "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (uiState.isPasswordVisible) 
                VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            isError = uiState.passwordError != null,
            supportingText = uiState.passwordError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Next
            ),
            keyboardActions = KeyboardActions(
                onNext = { focusManager.moveFocus(FocusDirection.Down) }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        // Confirm Password Field
        OutlinedTextField(
            value = uiState.confirmPassword,
            onValueChange = handleConfirmPasswordChange,
            label = { Text("Confirm Password") },
            leadingIcon = { Icon(Icons.Outlined.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(
                    onClick = handleConfirmPasswordVisibilityToggle
                ) {
                    Icon(
                        imageVector = if (uiState.isConfirmPasswordVisible) 
                            Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                        contentDescription = if (uiState.isConfirmPasswordVisible) 
                            "Hide password" else "Show password"
                    )
                }
            },
            visualTransformation = if (uiState.isConfirmPasswordVisible) 
                VisualTransformation.None else PasswordVisualTransformation(),
            singleLine = true,
            isError = uiState.confirmPasswordError != null,
            supportingText = uiState.confirmPasswordError?.let { { Text(it) } },
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { 
                    focusManager.clearFocus()
                    handleRegister()
                }
            ),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Register Button
        Button(
            onClick = handleRegister,
            enabled = !uiState.isLoading && 
                      uiState.email.isNotBlank() && 
                      uiState.displayName.isNotBlank() && 
                      uiState.password.isNotBlank() && 
                      uiState.confirmPassword.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        ) {
            if (uiState.isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(20.dp),
                    strokeWidth = 2.dp
                )
            } else {
                Text("Create Account")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // Login Link
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Already have an account? ",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Sign In",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.Medium,
                modifier = Modifier.clickable { onNavigateToLogin() }
            )
        }
    }
    //endregion

    //region Dialog and Sheet
    // No dialogs or sheets needed for this screen
    //endregion
} 
