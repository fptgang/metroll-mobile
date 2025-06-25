package com.vidz.domain.repository

import com.vidz.domain.model.CheckoutItem
import kotlinx.coroutines.flow.Flow

interface CartRepository {
    suspend fun addToCart(item: CheckoutItem)
    suspend fun removeFromCart(item: CheckoutItem)
    suspend fun updateCartItemQuantity(item: CheckoutItem, quantity: Int)
    suspend fun clearCart()
    fun getCartItems(): Flow<List<CheckoutItem>>
    fun getCartItemCount(): Flow<Int>
} 