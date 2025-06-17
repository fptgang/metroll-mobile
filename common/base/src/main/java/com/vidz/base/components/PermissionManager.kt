package com.vidz.base.components

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat

enum class PermissionStatus {
    GRANTED,
    DENIED,
    PERMANENTLY_DENIED,
    NOT_REQUESTED
}

data class PermissionState(
    val permission: String,
    val status: PermissionStatus,
    val shouldShowRationale: Boolean = false
)

@Composable
fun PermissionManager(
    permission: String,
    onPermissionResult: (PermissionState) -> Unit,
    onRequestPermission: ((() -> Unit) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var permissionState by remember { mutableStateOf(PermissionState(permission, PermissionStatus.NOT_REQUESTED)) }
    var shouldShowDialog by remember { mutableStateOf(false) }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        val newStatus = if (isGranted) {
            PermissionStatus.GRANTED
        } else {
            // Check if we should show rationale to determine if permanently denied
            val shouldShowRationale = when (permission) {
                Manifest.permission.POST_NOTIFICATIONS -> {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        false // System handles rationale for notifications
                    } else false
                }
                else -> false
            }
            if (shouldShowRationale) PermissionStatus.DENIED else PermissionStatus.PERMANENTLY_DENIED
        }
        
        val newState = PermissionState(permission, newStatus, shouldShowRationale = false)
        permissionState = newState
        onPermissionResult(newState)
    }

    // Function to request permission
    val requestPermission = {
        when {
            Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && 
            permission == Manifest.permission.POST_NOTIFICATIONS -> {
                permissionLauncher.launch(permission)
            }
            Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && 
            permission == Manifest.permission.POST_NOTIFICATIONS -> {
                // Notifications are automatically granted on older versions
                val grantedState = PermissionState(permission, PermissionStatus.GRANTED)
                permissionState = grantedState
                onPermissionResult(grantedState)
            }
            else -> {
                permissionLauncher.launch(permission)
            }
        }
    }

    // Check current permission status
    LaunchedEffect(permission) {
        val currentStatus = checkPermissionStatus(context, permission)
        val currentState = PermissionState(permission, currentStatus)
        permissionState = currentState
        onPermissionResult(currentState)
    }

    // Expose request function to parent if callback provided
    LaunchedEffect(onRequestPermission) {
        onRequestPermission?.invoke(requestPermission)
    }

    // Show rationale dialog if needed
    if (shouldShowDialog) {
        PermissionRationaleDialog(
            permission = permission,
            onConfirm = {
                shouldShowDialog = false
                requestPermission()
            },
            onDismiss = {
                shouldShowDialog = false
                val deniedState = PermissionState(permission, PermissionStatus.DENIED)
                permissionState = deniedState
                onPermissionResult(deniedState)
            }
        )
    }
}

@Composable
private fun PermissionRationaleDialog(
    permission: String,
    onConfirm: () -> Unit,
    onDismiss: () -> Unit
) {
    val (title, description) = when (permission) {
        Manifest.permission.POST_NOTIFICATIONS -> {
            "Enable Notifications" to "This app would like to send you notifications about your orders, promotions, and important updates. You can change this setting anytime in your device settings."
        }
        Manifest.permission.CAMERA -> {
            "Camera Permission" to "This app needs camera access to take photos. You can change this setting anytime in your device settings."
        }
        Manifest.permission.READ_EXTERNAL_STORAGE -> {
            "Storage Permission" to "This app needs storage access to save and read files. You can change this setting anytime in your device settings."
        }
        else -> {
            "Permission Required" to "This app needs this permission to function properly. You can change this setting anytime in your device settings."
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center
            )
        },
        confirmButton = {
            Button(
                onClick = onConfirm,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Allow",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Not Now",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    )
}

@Composable
fun NotificationPermissionDialog(
    onRequestPermission: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        icon = {
            Icon(
                imageVector = Icons.Default.Notifications,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary
            )
        },
        title = {
            Text(
                text = "Enable Notifications",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center
            )
        },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Stay updated with your orders and exclusive offers!",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = "• Order status updates\n• Special promotions\n• Important announcements",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Start,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        },
        confirmButton = {
            Button(
                onClick = onRequestPermission,
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Enable Notifications",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        dismissButton = {
            OutlinedButton(
                onClick = onDismiss,
                shape = RoundedCornerShape(12.dp)
            ) {
                Text(
                    text = "Maybe Later",
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Medium
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp,
        modifier = modifier
    )
}

private fun checkPermissionStatus(context: Context, permission: String): PermissionStatus {
    return when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && 
        permission == Manifest.permission.POST_NOTIFICATIONS -> {
            when (ContextCompat.checkSelfPermission(context, permission)) {
                PackageManager.PERMISSION_GRANTED -> PermissionStatus.GRANTED
                PackageManager.PERMISSION_DENIED -> PermissionStatus.NOT_REQUESTED
                else -> PermissionStatus.NOT_REQUESTED
            }
        }
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU && 
        permission == Manifest.permission.POST_NOTIFICATIONS -> {
            // Notifications are automatically granted on older versions
            PermissionStatus.GRANTED
        }
        else -> {
            when (ContextCompat.checkSelfPermission(context, permission)) {
                PackageManager.PERMISSION_GRANTED -> PermissionStatus.GRANTED
                PackageManager.PERMISSION_DENIED -> PermissionStatus.NOT_REQUESTED
                else -> PermissionStatus.NOT_REQUESTED
            }
        }
    }
} 
