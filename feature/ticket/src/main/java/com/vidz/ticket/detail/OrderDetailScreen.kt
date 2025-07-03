package com.vidz.ticket.detail

import android.annotation.SuppressLint
import android.app.Activity

import android.view.WindowManager
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue

import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.base.components.MetrollButton
import com.vidz.base.navigation.DestinationRoutes
import com.vidz.domain.model.Order
import com.vidz.domain.model.OrderDetail
import com.vidz.domain.model.OrderStatus
import com.vidz.domain.model.TicketType
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.OptIn

@Composable
fun OrderDetailScreenRoot(
    navController: NavController,
    onShowSnackbar: (String) -> Unit,
    viewModel: OrderDetailViewModel = hiltViewModel()
) {
    OrderDetailScreen(
        navController = navController,
        onShowSnackbar = onShowSnackbar,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderDetailScreen(
    navController: NavController,
    onShowSnackbar: (String) -> Unit,
    viewModel: OrderDetailViewModel
) {
    //region Define Var
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current
    //endregion

    //region Event Handler
    val onQRCodeClick = { orderDetail: OrderDetail ->
        if (orderDetail.ticketId.isEmpty()) {
            onShowSnackbar("No ticket ID available")
        } else {
            navController.navigate("${DestinationRoutes.QR_TICKET_SCREEN_ROUTE}/${orderDetail.ticketId}")
        }
    }



    val onContinuePayment = {
        viewModel.onTriggerEvent(OrderDetailViewModel.OrderDetailEvent.OpenPayment)
    }

    val onClosePayment = {
        viewModel.onTriggerEvent(OrderDetailViewModel.OrderDetailEvent.ClosePayment)
    }
    //endregion

    //region UI
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = uiState.order?.let { "Order #${it.id.take(8)}" } ?: "Order Details",
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoadingOrder -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            uiState.orderError != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "Failed to load order",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.error
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        val errorText = uiState.orderError ?: "Unknown error"
                        Text(
                            text = errorText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }
            uiState.order != null -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    item {
                        OrderSummaryCard(
                            order = uiState.order!!,
                            onContinuePayment = onContinuePayment
                        )
                    }

                    item {
                        Text(
                            text = "Order Items",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    items(uiState.order!!.orderDetails) { orderDetail ->
                        OrderDetailCard(
                            orderDetail = orderDetail,
                            onQRCodeClick = { onQRCodeClick(orderDetail) }
                        )
                    }
                }
            } else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "No order data available",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }

    // Error handling

    uiState.orderError?.let { error ->
        LaunchedEffect(error) {
            onShowSnackbar("Failed to load order: $error")
            viewModel.onTriggerEvent(OrderDetailViewModel.OrderDetailEvent.ClearError)
        }
    }



    // Full-screen Payment WebView
    if (uiState.showPaymentWebView && uiState.order?.paymentUrl != null) {
        FullScreenPaymentWebView(
            url = uiState.order!!.paymentUrl!!,
            onClose = onClosePayment
        )
    }
    //endregion
}

@Composable
private fun OrderSummaryCard(
    order: Order,
    onContinuePayment: () -> Unit = {}
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Order #${order.id.take(8)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    
                    Text(
                        text = order.createdAt.format(
                            DateTimeFormatter.ofPattern("EEEE, MMM dd, yyyy 'at' HH:mm", Locale.getDefault())
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                    )
                }
                
                OrderStatusChip(status = order.status)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Payment Info
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Payment Method",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = order.paymentMethod,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "Total Amount",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f)
                    )
                    Text(
                        text = "$${String.format("%.2f", order.finalTotal)}",
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }

            // Show payment button for pending orders with payment URL
            if (order.status == OrderStatus.PENDING && !order.paymentUrl.isNullOrBlank()) {
                Spacer(modifier = Modifier.height(16.dp))
                
                MetrollButton(
                    text = "Continue Payment",
                    onClick = onContinuePayment,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun OrderDetailCard(
    orderDetail: OrderDetail,
    onQRCodeClick: () -> Unit
) {
    OutlinedCard(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.outlinedCardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = when (orderDetail.ticketType) {
                            TicketType.TIMED -> "Timed Ticket"
                            TicketType.P2P -> "Point-to-Point Journey"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    Text(
                        text = "Ord detail ID: ${orderDetail.id.take(8)}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    Row {
                        Text(
                            text = "Quantity: ${orderDetail.quantity}",
                            style = MaterialTheme.typography.bodyMedium
                        )
                        Spacer(modifier = Modifier.width(16.dp))
                        Text(
                            text = "$${String.format("%.2f", orderDetail.unitPrice)} each",
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
                
                // QR Code Button
//                MetrollButton(
//                    text = "View",
//                    onClick = onQRCodeClick,
//                    enabled = orderDetail.ticketId.isNotEmpty(),
//                    modifier = Modifier.width(80.dp)
//                )
               if (orderDetail.ticketId.isNotEmpty()) {
                   IconButton(onClick = onQRCodeClick) {
                       Icon(
                           Icons.Default.RemoveRedEye,
                           contentDescription = "View QR Code",
                           tint = MaterialTheme.colorScheme.primary
                       )
                   }
               }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Totals
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Subtotal: $${String.format("%.2f", orderDetail.baseTotal)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "Final: $${String.format("%.2f", orderDetail.finalTotal)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
private fun OrderStatusChip(status: OrderStatus) {
    val (backgroundColor, textColor, text) = when (status) {
        OrderStatus.PENDING -> Triple(
            MaterialTheme.colorScheme.secondaryContainer,
            MaterialTheme.colorScheme.onSecondaryContainer,
            "Pending"
        )
        OrderStatus.COMPLETED -> Triple(
            MaterialTheme.colorScheme.tertiaryContainer,
            MaterialTheme.colorScheme.onTertiaryContainer,
            "Completed"
        )
        OrderStatus.FAILED -> Triple(
            MaterialTheme.colorScheme.errorContainer,
            MaterialTheme.colorScheme.onErrorContainer,
            "Failed"
        )
    }

    Box(
        modifier = Modifier
            .background(
                color = backgroundColor,
                shape = RoundedCornerShape(12.dp)
            )
            .padding(horizontal = 12.dp, vertical = 6.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium,
            fontWeight = FontWeight.Medium,
            color = textColor
        )
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("SetJavaScriptEnabled")
@Composable
private fun FullScreenPaymentWebView(
    url: String,
    onClose: () -> Unit
) {
    // Full screen overlay covering the entire screen
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Top app bar with close button
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = "Complete Payment",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Medium
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Close",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )

            // WebView taking the rest of the screen
            AndroidView(
                factory = { context ->
                    WebView(context).apply {
                        webViewClient = object : WebViewClient() {
                            override fun onPageFinished(view: WebView?, url: String?) {
                                super.onPageFinished(view, url)
                                // Check if payment is complete based on URL patterns
                                url?.let { currentUrl ->
                                    when {
                                        currentUrl.contains("success") || 
                                        currentUrl.contains("complete") ||
                                        currentUrl.contains("payment_status=success") ||
                                        currentUrl.contains("status=success") -> {
                                            // Payment successful, close WebView
                                            onClose()
                                        }
                                        currentUrl.contains("failed") || 
                                        currentUrl.contains("error") ||
                                        currentUrl.contains("payment_status=failed") ||
                                        currentUrl.contains("status=failed") ||
                                        currentUrl.contains("cancel") -> {
                                            // Payment failed, close WebView
                                            onClose()
                                        }
                                    }
                                }
                            }
                            
                            override fun shouldOverrideUrlLoading(view: WebView?, request: WebResourceRequest?): Boolean {
                                request?.url?.toString()?.let { currentUrl ->
                                    when {
                                        currentUrl.startsWith("metroll://") -> {
                                            // Handle app deep link for payment completion
                                            onClose()
                                            return true
                                        }
                                    }
                                }
                                return false
                            }
                        }
                        settings.javaScriptEnabled = true
                        settings.domStorageEnabled = true
                        settings.loadWithOverviewMode = true
                        settings.useWideViewPort = true
                        settings.setSupportZoom(true)
                        settings.builtInZoomControls = true
                        settings.displayZoomControls = false
                        loadUrl(url)
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}