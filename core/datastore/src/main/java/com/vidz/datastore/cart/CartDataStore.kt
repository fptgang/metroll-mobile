package com.vidz.datastore.cart

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.vidz.domain.model.CheckoutItem
import com.vidz.domain.model.TicketType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CartDataStore @Inject constructor(
    private val dataStore: DataStore<Preferences>
) {
    private val cartItemsKey = stringPreferencesKey("cart_items")
    
    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
    }

    suspend fun addToCart(item: CheckoutItem) {
        dataStore.edit { preferences ->
            val currentCartJson = preferences[cartItemsKey] ?: "[]"
            val currentCart = try {
                json.decodeFromString<List<CheckoutItem>>(currentCartJson)
            } catch (e: Exception) {
                emptyList()
            }.toMutableList()

            // Check if item already exists
            val existingIndex = currentCart.indexOfFirst { existingItem ->
                existingItem.ticketType == item.ticketType &&
                existingItem.p2pJourneyId == item.p2pJourneyId &&
                existingItem.timedTicketPlanId == item.timedTicketPlanId
            }

            if (existingIndex >= 0) {
                // Update quantity of existing item
                currentCart[existingIndex] = currentCart[existingIndex].copy(
                    quantity = currentCart[existingIndex].quantity + item.quantity
                )
            } else {
                // Add new item
                currentCart.add(item)
            }

            preferences[cartItemsKey] = json.encodeToString(currentCart)
        }
    }

    suspend fun removeFromCart(item: CheckoutItem) {
        dataStore.edit { preferences ->
            val currentCartJson = preferences[cartItemsKey] ?: "[]"
            val currentCart = try {
                json.decodeFromString<List<CheckoutItem>>(currentCartJson)
            } catch (e: Exception) {
                emptyList()
            }.toMutableList()

            currentCart.removeAll { existingItem ->
                existingItem.ticketType == item.ticketType &&
                existingItem.p2pJourneyId == item.p2pJourneyId &&
                existingItem.timedTicketPlanId == item.timedTicketPlanId
            }

            preferences[cartItemsKey] = json.encodeToString(currentCart)
        }
    }

    suspend fun updateCartItemQuantity(item: CheckoutItem, quantity: Int) {
        if (quantity <= 0) {
            removeFromCart(item)
            return
        }

        dataStore.edit { preferences ->
            val currentCartJson = preferences[cartItemsKey] ?: "[]"
            val currentCart = try {
                json.decodeFromString<List<CheckoutItem>>(currentCartJson)
            } catch (e: Exception) {
                emptyList()
            }.toMutableList()

            val existingIndex = currentCart.indexOfFirst { existingItem ->
                existingItem.ticketType == item.ticketType &&
                existingItem.p2pJourneyId == item.p2pJourneyId &&
                existingItem.timedTicketPlanId == item.timedTicketPlanId
            }

            if (existingIndex >= 0) {
                currentCart[existingIndex] = currentCart[existingIndex].copy(quantity = quantity)
                preferences[cartItemsKey] = json.encodeToString(currentCart)
            }
        }
    }

    suspend fun clearCart() {
        dataStore.edit { preferences ->
            preferences[cartItemsKey] = "[]"
        }
    }

    fun getCartItems(): Flow<List<CheckoutItem>> {
        return dataStore.data.map { preferences ->
            val cartJson = preferences[cartItemsKey] ?: "[]"
            try {
                json.decodeFromString<List<CheckoutItem>>(cartJson)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }

    fun getCartItemCount(): Flow<Int> {
        return getCartItems().map { items ->
            items.sumOf { it.quantity }
        }
    }
} 