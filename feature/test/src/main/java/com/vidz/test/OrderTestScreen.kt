package com.vidz.test

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel

@Composable
fun OrderTestScreen(
    viewModel: OrderTestViewModel = hiltViewModel()
) {
    //region Define Var
    val uiState = viewModel.uiState.value
    //endregion

    //region Event Handler
    val handleCheckout = {
        viewModel.onTriggerEvent(OrderTestViewModel.OrderTestEvent.TestCheckout)
    }
    
    val handleGetAllOrders = {
        viewModel.onTriggerEvent(OrderTestViewModel.OrderTestEvent.TestGetAllOrders)
    }
    
    val handleGetMyOrders = {
        viewModel.onTriggerEvent(OrderTestViewModel.OrderTestEvent.TestGetMyOrders)
    }
    
    val handleGetOrderById = {
        viewModel.onTriggerEvent(OrderTestViewModel.OrderTestEvent.TestGetOrderById(uiState.testOrderId))
    }
    
    val handleGetPaymentStatus = {
        viewModel.onTriggerEvent(OrderTestViewModel.OrderTestEvent.TestGetPaymentStatus(uiState.testOrderId))
    }
    
    val handlePaymentSuccess = {
        viewModel.onTriggerEvent(OrderTestViewModel.OrderTestEvent.TestPaymentSuccess(uiState.testOrderId))
    }
    
    val handlePaymentCancel = {
        viewModel.onTriggerEvent(OrderTestViewModel.OrderTestEvent.TestPaymentCancel(uiState.testOrderId))
    }
    
    val handleClearResults = {
        viewModel.onTriggerEvent(OrderTestViewModel.OrderTestEvent.ClearResults)
    }
    
    val handleOrderIdChange = { orderId: String ->
        viewModel.onTriggerEvent(OrderTestViewModel.OrderTestEvent.UpdateOrderId(orderId))
    }
    //endregion

    //region ui
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title
        Text(
            text = "Order API Test Screen",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )
        
        Text(
            text = "Test all Order and Payment API endpoints",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        // Order ID Input for specific order tests
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Order ID for Specific Tests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                OutlinedTextField(
                    value = uiState.testOrderId,
                    onValueChange = handleOrderIdChange,
                    label = { Text("Order ID") },
                    placeholder = { Text("Enter order ID for testing...") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }

        // Order API Tests
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Order API Tests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                // Checkout Test
                Button(
                    onClick = handleCheckout,
                    enabled = !uiState.isLoading,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    if (uiState.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text("Test Checkout")
                    }
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = handleGetAllOrders,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Get All Orders")
                    }
                    
                    Button(
                        onClick = handleGetMyOrders,
                        enabled = !uiState.isLoading,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Get My Orders")
                    }
                }
                
                Button(
                    onClick = handleGetOrderById,
                    enabled = !uiState.isLoading && uiState.testOrderId.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Get Order By ID")
                }
            }
        }

        // Payment API Tests
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceVariant
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Payment API Tests",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
                
                Button(
                    onClick = handleGetPaymentStatus,
                    enabled = !uiState.isLoading && uiState.testOrderId.isNotBlank(),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Get Payment Status")
                }
                
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = handlePaymentSuccess,
                        enabled = !uiState.isLoading && uiState.testOrderId.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Payment Success")
                    }
                    
                    Button(
                        onClick = handlePaymentCancel,
                        enabled = !uiState.isLoading && uiState.testOrderId.isNotBlank(),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Payment Cancel")
                    }
                }
            }
        }

        // Results Section
        Card(
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Test Results",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                    
                    if (uiState.result.isNotBlank()) {
                        IconButton(onClick = handleClearResults) {
                            Icon(
                                Icons.Default.Clear,
                                contentDescription = "Clear results"
                            )
                        }
                    }
                }
                
                if (uiState.result.isNotBlank()) {
                    Card(
                        colors = CardDefaults.cardColors(
                            containerColor = if (uiState.result.startsWith("✅")) 
                                MaterialTheme.colorScheme.primaryContainer
                            else 
                                MaterialTheme.colorScheme.errorContainer
                        )
                    ) {
                        Text(
                            text = uiState.result,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(12.dp)
                        )
                    }
                } else {
                    Text(
                        text = "No results yet. Click a button above to test the APIs.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Clear Results Button
        if (uiState.result.isNotBlank()) {
            OutlinedButton(
                onClick = handleClearResults,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Clear Results")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
    }
    //endregion

    //region Dialog and Sheet
    //endregion
} 
