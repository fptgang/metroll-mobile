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
import androidx.compose.material.icons.filled.CheckCircle
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.viewinterop.AndroidView
import com.vidz.ticket.purchase.CartItem
import com.vidz.ticket.purchase.PaymentMethod
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
        onShowSnackbar("Mục đã được xóa khỏi giỏ hàng")
    }
    
    val onClearCart = {
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ClearCart)
        onShowSnackbar("Giỏ hàng đã được xóa")
    }
    
    val onCheckout = {
        if (uiState.cartItems.isNotEmpty()) {
            viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.Checkout)
        } else {
            onShowSnackbar("Giỏ hàng trống")
        }
    }

    val onClosePayment = {
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ClosePayment)
        // Navigate to ticket management screen after payment
        navController.navigate(com.vidz.base.navigation.DestinationRoutes.MY_TICKETS_SCREEN_ROUTE) {
            popUpTo(com.vidz.base.navigation.DestinationRoutes.ROOT_TICKET_SCREEN_ROUTE) { inclusive = true }
        }
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
    
    val onShowPaymentMethodSheet = {
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ShowPaymentMethodSheet(true))
    }
    
    val onHidePaymentMethodSheet = {
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ShowPaymentMethodSheet(false))
    }
    
    val onSelectPaymentMethod = { paymentMethod: PaymentMethod ->
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.SelectPaymentMethod(paymentMethod))
        viewModel.onTriggerEvent(TicketPurchaseViewModel.TicketPurchaseEvent.ShowPaymentMethodSheet(false))
    }
    //endregion

    //region UI
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = "Giỏ hàng (${uiState.cartItemCount} mục)",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            Icons.Default.ArrowBack, 
                            contentDescription = "Quay lại",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                actions = {
                    if (uiState.cartItems.isNotEmpty()) {
                        IconButton(
                            onClick = onClearCart,
                            modifier = Modifier
                                .size(48.dp)
                                .clip(RoundedCornerShape(12.dp))
                        ) {
                            Icon(
                                Icons.Default.Delete, 
                                contentDescription = "Xóa giỏ hàng",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
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
                    isCustomer = uiState.isCustomer,
                    selectedPaymentMethod = uiState.selectedPaymentMethod,
                    onCheckout = onCheckout,
                    onShowVoucherSheet = onShowVoucherSheet,
                    onShowPaymentMethodSheet = onShowPaymentMethodSheet
                )
            }
        },
        containerColor = MaterialTheme.colorScheme.surface
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.surface)
        ) {
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
                    contentPadding = PaddingValues(20.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(uiState.cartItems, key = { "${it.id}-${it.ticketType}" }) { item ->
                        CartItemCard(
                            item = item,
                            onQuantityChange = { quantity -> onQuantityChange(item, quantity) },
                            onRemove = { onRemoveItem(item) }
                        )
                    }
                    
                    // Add some bottom padding for the bottom bar
                    item {
                        Spacer(modifier = Modifier.height(120.dp))
                    }
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
    
    // Handle successful checkout for CASH payments (no WebView)
    LaunchedEffect(uiState.checkoutResult) {
        uiState.checkoutResult?.let { order ->
            if (order.paymentMethod == "CASH") {
                onShowSnackbar("Đơn hàng đã được tạo thành công!")
                // Navigate to ticket management screen
                navController.navigate(com.vidz.base.navigation.DestinationRoutes.MY_TICKETS_SCREEN_ROUTE) {
                    popUpTo(com.vidz.base.navigation.DestinationRoutes.ROOT_TICKET_SCREEN_ROUTE) { inclusive = true }
                }
            }
        }
    }

    // Full-screen Payment WebView
    if (uiState.showPaymentWebView && uiState.paymentUrl != null) {
        LaunchedEffect(uiState.paymentUrl) {
            onShowSnackbar("Đang mở trang thanh toán...")
        }
        
        FullScreenPaymentWebView(
            url = uiState.paymentUrl!!,
            onClose = {
                onClosePayment()
//                onShowSnackbar("Thanh toán hoàn tất!")
            }
        )
    }
    
    // Voucher Selection Bottom Sheet (for customers only)
    if (uiState.showVoucherSheet && uiState.isCustomer) {
        ModalBottomSheet(
            onDismissRequest = onHideVoucherSheet,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            VoucherSelectionBottomSheet(
                vouchers = uiState.vouchers,
                selectedVoucher = uiState.selectedVoucher,
                isLoading = uiState.isLoadingVouchers,
                cartTotal = uiState.subtotal - uiState.subtotal * (uiState.userDiscountPercentage?:0.0f),
                onVoucherSelected = onSelectVoucher,
                onDismiss = onHideVoucherSheet
            )
        }
    }
    
    // Payment Method Selection Bottom Sheet (for staff only)
    if (uiState.showPaymentMethodSheet && !uiState.isCustomer) {
        ModalBottomSheet(
            onDismissRequest = onHidePaymentMethodSheet,
            sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ) {
            PaymentMethodSelectionBottomSheet(
                availablePaymentMethods = uiState.availablePaymentMethods,
                selectedPaymentMethod = uiState.selectedPaymentMethod,
                onPaymentMethodSelected = onSelectPaymentMethod,
                onDismiss = onHidePaymentMethodSheet
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
        modifier = modifier.padding(40.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        // Modern empty state illustration
        Box(
            modifier = Modifier
                .size(120.dp)
                .background(
                    MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f),
                    RoundedCornerShape(60.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f)
            )
        }

        Spacer(modifier = Modifier.height(32.dp))

        Text(
            text = "Giỏ hàng trống",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "Bạn chưa có vé nào trong giỏ hàng.\nHãy thêm vé để bắt đầu hành trình!",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            textAlign = TextAlign.Center,
            lineHeight = MaterialTheme.typography.bodyLarge.lineHeight
        )

        Spacer(modifier = Modifier.height(40.dp))

        MetrollButton(
            text = "Tiếp tục mua vé",
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
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        ),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Item Header with modern styling
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Text(
                            text = item.name,
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = if (isLoading) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onSurface
                        )
                        
                        if (isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }
                    
                    if (item.description != null) {
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = item.description,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                
                IconButton(
                    onClick = onRemove,
                    modifier = Modifier
                        .size(40.dp)
                        .background(
                            MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.1f),
                            RoundedCornerShape(12.dp)
                        )
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Xóa",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Quantity and Price Row with modern controls
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Modern Quantity Controls
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    IconButton(
                        onClick = { onQuantityChange(item.quantity - 1) },
                        enabled = item.quantity > 1,
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                if (item.quantity > 1) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Remove,
                            contentDescription = "Giảm số lượng",
                            tint = if (item.quantity > 1) MaterialTheme.colorScheme.onPrimaryContainer
                                   else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .width(56.dp)
                            .height(44.dp)
                            .background(
                                MaterialTheme.colorScheme.surface,
                                RoundedCornerShape(12.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.quantity.toString(),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    IconButton(
                        onClick = { onQuantityChange(item.quantity + 1) },
                        modifier = Modifier
                            .size(44.dp)
                            .background(
                                MaterialTheme.colorScheme.primaryContainer,
                                RoundedCornerShape(12.dp)
                            )
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "Tăng số lượng",
                            tint = MaterialTheme.colorScheme.onPrimaryContainer,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                
                // Modern Price Display
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    Text(
                        text = "${String.format("%,.0f", item.price)}₫ mỗi vé",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${String.format("%,.0f", item.price * item.quantity)}₫",
                        style = MaterialTheme.typography.headlineSmall,
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
    isCustomer: Boolean,
    selectedPaymentMethod: PaymentMethod,
    onCheckout: () -> Unit,
    onShowVoucherSheet: () -> Unit,
    onShowPaymentMethodSheet: () -> Unit
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 16.dp
    ) {
        Column(
            modifier = Modifier.padding(20.dp)
        ) {
            // Modern Price Breakdown
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceContainer.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    // Subtotal
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Tạm tính",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        
                        Text(
                            text = "${String.format("%,.0f", subtotal)}₫",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    
                    // Discount Package
                    if (userDiscountPercentage != null && userDiscountPercentage > 0f && discountPackageDiscount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Giảm giá thành viên (${(userDiscountPercentage * 100).toInt()}%)",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Text(
                                text = "-${String.format("%,.0f", discountPackageDiscount)}₫",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    
                    // Voucher Discount
                    if (selectedVoucher != null && voucherDiscount > 0) {
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Giảm giá voucher",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            
                            Text(
                                text = "-${String.format("%,.0f", voucherDiscount)}₫",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Total Amount
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Tổng cộng",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                Text(
                    text = "${String.format("%,.0f", total)}₫",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Role-based Selection Section with modern styling
            if (isCustomer) {
                // Modern Voucher Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    onClick = onShowVoucherSheet
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Voucher giảm giá",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            if (selectedVoucher != null) {
                                Text(
                                    text = "${selectedVoucher.code} • Tiết kiệm ${String.format("%,.0f", selectedVoucher.discountAmount)}₫",
                                    style = MaterialTheme.typography.bodyLarge,
                                    fontWeight = FontWeight.Medium,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                            } else {
                                Text(
                                    text = "Chọn voucher để tiết kiệm thêm",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                        
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Chọn voucher",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier
                                .size(24.dp)
                                .then(Modifier.graphicsLayer { rotationZ = 180f })
                        )
                    }
                }
            } else {
                // Modern Payment Method Card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer.copy(alpha = 0.1f)
                    ),
                    shape = RoundedCornerShape(16.dp),
                    onClick = onShowPaymentMethodSheet
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Phương thức thanh toán",
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.secondary,
                                fontWeight = FontWeight.Medium
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = selectedPaymentMethod.displayName,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Medium,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                        
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Chọn phương thức",
                            tint = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier
                                .size(24.dp)
                                .then(Modifier.graphicsLayer { rotationZ = 180f })
                        )
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(20.dp))
            
            // Modern Checkout Button
            MetrollButton(
                text = if (isLoading) "Đang xử lý..." else "Tiến hành thanh toán",
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
    // Modern full screen overlay
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Modern top app bar
            CenterAlignedTopAppBar(
                title = { 
                    Text(
                        text = "Hoàn tất thanh toán",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onClose,
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(12.dp))
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Đóng",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )

            // WebView with modern styling
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp, vertical = 4.dp)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            ) {
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
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun PaymentMethodSelectionBottomSheet(
    availablePaymentMethods: List<PaymentMethod>,
    selectedPaymentMethod: PaymentMethod,
    onPaymentMethodSelected: (PaymentMethod) -> Unit,
    onDismiss: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(20.dp)
    ) {
        Text(
            text = "Chọn phương thức thanh toán",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.padding(bottom = 20.dp)
        )
        
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(availablePaymentMethods) { paymentMethod ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = if (paymentMethod == selectedPaymentMethod) {
                            MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
                        } else {
                            MaterialTheme.colorScheme.surfaceContainer
                        }
                    ),
                    shape = RoundedCornerShape(16.dp),
                    onClick = { onPaymentMethodSelected(paymentMethod) }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = paymentMethod.displayName,
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = if (paymentMethod == selectedPaymentMethod) {
                                    MaterialTheme.colorScheme.primary
                                } else {
                                    MaterialTheme.colorScheme.onSurface
                                }
                            )
                            
                            Spacer(modifier = Modifier.height(4.dp))
                            
                            Text(
                                text = when (paymentMethod) {
                                    PaymentMethod.CASH -> "Thanh toán bằng tiền mặt tại quầy"
                                    PaymentMethod.PAYOS -> "Thanh toán trực tuyến qua PayOS"
                                },
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (paymentMethod == selectedPaymentMethod) {
                                    MaterialTheme.colorScheme.onPrimaryContainer
                                } else {
                                    MaterialTheme.colorScheme.onSurfaceVariant
                                }
                            )
                        }
                        
                        if (paymentMethod == selectedPaymentMethod) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Đã chọn",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(28.dp)
                            )
                        }
                    }
                }
            }
        }
        
        Spacer(modifier = Modifier.height(20.dp))
    }
} 