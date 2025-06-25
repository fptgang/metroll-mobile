package com.vidz.domain.usecase.cart

import com.vidz.domain.model.CheckoutItem
import com.vidz.domain.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetCartItemsUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    operator fun invoke(): Flow<List<CheckoutItem>> {
        return cartRepository.getCartItems()
    }
} 