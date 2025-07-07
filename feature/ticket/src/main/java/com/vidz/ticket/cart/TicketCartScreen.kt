package com.vidz.ticket.cart

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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.vidz.base.components.MetrollButton
import com.vidz.base.components.VoucherSelectionBottomSheet
import android.annotation.SuppressLint
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.ui.viewinterop.AndroidView
import com.vidz.ticket.purchase.CartItem
import com.vidz.ticket.purchase.TicketPurchaseViewModel
import kotlin.OptIn

@Composable
fun TicketCartScreenRoot(
    navController: NavController,
    onShowSnackbar: (String) -> Unit,
    viewModel: TicketPurchaseViewModel = hiltViewModel()
) {
    TicketCartScreen(
        navController = navController,
        onShowSnackbar = onShowSnackbar,
        viewModel = viewModel
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TicketCartScreen(
    navController: NavController,
    onShowSnackbar: (String) -> Unit,
    viewModel: TicketPurchaseViewModel
) {
    //region Define Var
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    //endregion

    //region Event Handler
    val onQuantityChange = { item: CartItem, quantity: Int ->
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.UpdateCartItemQuantity(item, quantity))
    }
    
    val onRemoveItem = { item: CartItem ->
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.RemoveFromCart(item))
        onShowSnackbar("Item removed from cart")
    }
    
    val onClearCart = {
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ClearCart)
        onShowSnackbar("Cart cleared")
    }
    
    val onCheckout = {
        if (uiState.cartItems.isNotEmpty()) {
            viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.Checkout)
        } else {
            onShowSnackbar("Cart is empty")
        }
    }

    val onClosePayment = {
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ClosePayment)
    }
    
    val onShowVoucherSheet = {
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ShowVoucherSheet(true))
    }
    
    val onHideVoucherSheet = {
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ShowVoucherSheet(false))
    }
    
    val onSelectVoucher = { voucher: com.vidz.domain.model.Voucher? ->
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.SelectVoucher(voucher))
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ShowVoucherSheet(false))
    }
    //endregion

    //region UI
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Cart (${uiState.cartItemCount} items)") },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.cartItems.isNotEmpty()) {
                        IconButton(onClick = onClearCart) {
                            Icon(Icons.Default.Delete, contentDescription = "Clear Cart")
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer
                )
            )
        },
        bottomBar = {
            if (uiState.cartItems.isNotEmpty()) {
                CartBottomBar(
                    subtotal = uiState.subtotal,
                    total = uiState.total,
                    selectedVoucher = uiState.selectedVoucher,
                    userDiscountPercentage = uiState.userDiscountPercentage,
                    voucherDiscount = uiState.voucherDiscount,
                    discountPackageDiscount = uiState.discountPackageDiscount,
                    isLoading = uiState.isCheckingOut,
                    onCheckout = onCheckout,
                    onShowVoucherSheet = onShowVoucherSheet
                )
            }
        }
    ) { paddingValues ->
        if (uiState.cartItems.isEmpty()) {
            EmptyCartState(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                onContinueShopping = { navController.popBackStack() }
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(uiState.cartItems, key = { "${it.id}-${it.ticketType}" }) { item ->
                    CartItemCard(
                        item = item,
                        onQuantityChange = { quantity -> onQuantityChange(item, quantity) },
                        onRemove = { onRemoveItem(item) }
                    )
                }
                
                // Add some bottom padding
                item {
                    Spacer(modifier = Modifier.height(100.dp))
                }
            }
        }
    }

    // Show error if any
    uiState.error?.let { error ->
        LaunchedEffect(error) {
            onShowSnackbar(error)
            viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ClearError)
        }
    }

    // Full-screen Payment WebView
    if (uiState.showPaymentWebView && uiState.paymentUrl != null) {
        LaunchedEffect(uiState.paymentUrl) {
            onShowSnackbar("Opening payment page...")
        }
        
        FullScreenPaymentWebView(
            url = uiState.paymentUrl!!,
            onClose = {
                onClosePayment()
                onShowSnackbar("Payment completed!")
                navController.popBackStack()
            }
        )
    }
    
    // Voucher Selection Bottom Sheet
    if (uiState.showVoucherSheet) {
        ModalBottomSheet(
            onDismissRequest = onHideVoucherSheet,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            VoucherSelectionBottomSheet(
                vouchers = uiState.vouchers,
                selectedVoucher = uiState.selectedVoucher,
                isLoading = uiState.isLoadingVouchers,
                cartTotal = uiState.subtotal,
                onVoucherSelected = onSelectVoucher,
                onDismiss = onHideVoucherSheet
            )
        }
    }
    //endregion
}

@Composable
private fun EmptyCartState(
    modifier: Modifier = Modifier,
    onContinueShopping: () -> Unit
) {
    Column(
        modifier = modifier.padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Default.ShoppingCart,
            contentDescription = null,
            modifier = Modifier.size(80.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Your cart is empty",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Medium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Add some tickets to get started!",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f),
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(24.dp))

        MetrollButton(
            text = "Continue Shopping",
            onClick = onContinueShopping,
            modifier = Modifier.fillMaxWidth()
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CartItemCard(
    item: CartItem,
    onQuantityChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    val isLoading = item.name.startsWith("Loading") || item.description == "Loading details..."
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Item Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (isLoading) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                        
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    if (item.description != null) {
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Quantity and Price Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = { onQuantityChange(item.quantity - 1) },
                        enabled = item.quantity > 1,
                        modifier = Modifier.size(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Decrease",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    
                    Surface(
                        modifier = Modifier.width(48.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text(
                            text = item.quantity.toString(),
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }
                    
                    OutlinedButton(
                        onClick = { onQuantityChange(item.quantity + 1) },
                        modifier = Modifier.size(36.dp),
                        contentPadding = PaddingValues(0.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Increase",
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
                
                // Price
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "$${String.format("%.2f", item.price)} each",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = "$${String.format("%.2f", item.price * item.quantity)}",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}

@Composable
private fun CartBottomBar(
    subtotal: Double,
    total: Double,
    selectedVoucher: com.vidz.domain.model.Voucher?,
    userDiscountPercentage: Float?,
    voucherDiscount: Double,
    discountPackageDiscount: Double,
    isLoading: Boolean,
    onCheckout: () -> Unit,
    onShowVoucherSheet: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Always show subtotal
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Subtotal:",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                
                Text(
                    text = "$${String.format("%.2f", subtotal)}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Show discount package discount if available
            if (userDiscountPercentage != null && userDiscountPercentage > 0f && discountPackageDiscount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Member Discount (${(userDiscountPercentage * 100).toInt()}%):",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "-$${String.format("%.2f", discountPackageDiscount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            // Show voucher discount if selected
            if (selectedVoucher != null && voucherDiscount > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Voucher Discount:",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    
                    Text(
                        text = "-$${String.format("%.2f", voucherDiscount)}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Total:",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
                
                Text(
                    text = "$${String.format("%.2f", total)}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Voucher Section
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                onClick = onShowVoucherSheet
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Voucher",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        if (selectedVoucher != null) {
                            Text(
                                text = "${selectedVoucher.code} • Save $${String.format("%.2f", selectedVoucher.discountAmount)}",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        } else {
                            Text(
                                text = "Tap to select voucher",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                    
                    Text(
                        text = ">",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            MetrollButton(
                text = if (isLoading) "Processing..." else "Proceed to Checkout",
                onClick = onCheckout,
                enabled = !isLoading,
                isLoading = isLoading,
                modifier = Modifier.fillMaxWidth()
            )
        }
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
                            imageVector = Icons.Default.Close,
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

                                        else -> {}
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