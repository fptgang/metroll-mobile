package com.vidz.domain.usecase.cart

import com.vidz.domain.model.CheckoutItem
import com.vidz.domain.repository.CartRepository
import javax.inject.Inject

class UpdateCartItemQuantityUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(item: CheckoutItem, quantity: Int) {
        cartRepository.updateCartItemQuantity(item, quantity)
    }
} 