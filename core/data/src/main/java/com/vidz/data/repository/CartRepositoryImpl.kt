package com.vidz.data.repository

import com.vidz.datastore.cart.CartDataStore
import com.vidz.domain.model.CheckoutItem
import com.vidz.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartRepositoryImpl @Inject constructor(
    private val cartDataStore: CartDataStore
) : CartRepository {

    override suspend fun addToCart(item: CheckoutItem) {
        cartDataStore.addToCart(item)
    }

    override suspend fun removeFromCart(item: CheckoutItem) {
        cartDataStore.removeFromCart(item)
    }

    override suspend fun updateCartItemQuantity(item: CheckoutItem, quantity: Int) {
        cartDataStore.updateCartItemQuantity(item, quantity)
    }

    override suspend fun clearCart() {
        cartDataStore.clearCart()
    }

    override fun getCartItems(): Flow<List<CheckoutItem>> {
        return cartDataStore.getCartItems()
    }

    override fun getCartItemCount(): Flow<Int> {
        return cartDataStore.getCartItemCount()
    }
} 