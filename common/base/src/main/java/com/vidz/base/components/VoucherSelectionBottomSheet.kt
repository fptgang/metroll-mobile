package com.vidz.base.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.vidz.domain.model.Voucher
import com.vidz.domain.model.VoucherStatus

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VoucherSelectionBottomSheet(
    vouchers: List<Voucher>,
    selectedVoucher: Voucher?,
    isLoading: Boolean,
    cartTotal: Double,
    onVoucherSelected: (Voucher?) -> Unit,
    onDismiss: () -> Unit,
    onFetchVoucherByCode: ((String) -> Unit)? = null
) {
    var voucherCode by remember { mutableStateOf(TextFieldValue("")) }
    val fetchedVoucher = vouchers.firstOrNull()
    
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Nhập mã voucher",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            IconButton(onClick = onDismiss) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Đóng",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Voucher Code Input
        OutlinedTextField(
            value = voucherCode,
            onValueChange = { voucherCode = it },
            label = { Text("Mã voucher") },
            placeholder = { Text("Nhập mã voucher của bạn") },
            modifier = Modifier.fillMaxWidth(),
            trailingIcon = {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                } else {
                    IconButton(
                        onClick = {
                            if (voucherCode.text.isNotBlank()) {
                                onFetchVoucherByCode?.invoke(voucherCode.text.trim())
                            }
                        },
                        enabled = voucherCode.text.isNotBlank() && !isLoading
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Tìm voucher",
                            tint = if (voucherCode.text.isNotBlank()) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                focusedLabelColor = MaterialTheme.colorScheme.primary
            ),
            shape = RoundedCornerShape(12.dp)
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Search Button
        MetrollButton(
            text = if (isLoading) "Đang tìm..." else "Tìm voucher",
            onClick = {
                if (voucherCode.text.isNotBlank()) {
                    onFetchVoucherByCode?.invoke(voucherCode.text.trim())
                }
            },
            enabled = voucherCode.text.isNotBlank() && !isLoading,
            isLoading = isLoading,
            modifier = Modifier.fillMaxWidth()
        )
        
        Spacer(modifier = Modifier.height(20.dp))
        
        // Display fetched voucher
        if (fetchedVoucher != null) {
            VoucherCard(
                voucher = fetchedVoucher,
                isSelected = selectedVoucher?.id == fetchedVoucher.id,
                cartTotal = cartTotal,
                onSelect = { 
                    if (fetchedVoucher.status == VoucherStatus.VALID && cartTotal >= fetchedVoucher.minTransactionAmount) {
                        onVoucherSelected(fetchedVoucher)
                    }
                }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
        
        // Option to not use any voucher
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(
                containerColor = if (selectedVoucher == null) {
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                } else {
                    MaterialTheme.colorScheme.surfaceContainer
                }
            ),
            shape = RoundedCornerShape(16.dp),
            onClick = { onVoucherSelected(null) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Không sử dụng voucher",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium,
                    color = if (selectedVoucher == null) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurface
                    }
                )
                
                if (selectedVoucher == null) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Đã chọn",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
}

@Composable
private fun VoucherCard(
    voucher: Voucher,
    isSelected: Boolean,
    cartTotal: Double,
    onSelect: () -> Unit
) {
    val isApplicable = voucher.status == VoucherStatus.VALID && cartTotal >= voucher.minTransactionAmount

    Card(
        modifier = Modifier.fillMaxWidth(),
        onClick = { if (isApplicable) onSelect() },
        colors = CardDefaults.cardColors(
            containerColor = when {
                isSelected -> MaterialTheme.colorScheme.primaryContainer
                isApplicable -> MaterialTheme.colorScheme.surfaceContainer
                else -> MaterialTheme.colorScheme.surfaceVariant
            }
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = voucher.code,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isApplicable) MaterialTheme.colorScheme.onSurface 
                                   else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                        
                        if (isApplicable) {
                            Surface(
                                color = MaterialTheme.colorScheme.primaryContainer,
                                shape = RoundedCornerShape(8.dp)
                            ) {
                                Text(
                                    text = "Có thể dùng",
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Text(
                        text = "Tiết kiệm ${String.format("%,.0f", voucher.discountAmount)}₫",
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isApplicable) MaterialTheme.colorScheme.primary 
                               else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                        fontWeight = FontWeight.Medium
                    )
                    
                    Spacer(modifier = Modifier.height(4.dp))
                    
                    Text(
                        text = "Đơn tối thiểu: ${String.format("%,.0f", voucher.minTransactionAmount)}₫",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    if (!isApplicable) {
                        Spacer(modifier = Modifier.height(8.dp))
                        if (voucher.status != VoucherStatus.VALID) {
                            Text(
                                text = "Trạng thái: ${voucher.status.name}",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        } else if (cartTotal < voucher.minTransactionAmount) {
                            Text(
                                text = "Chưa đạt đơn tối thiểu",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
                
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Đã chọn",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
} 