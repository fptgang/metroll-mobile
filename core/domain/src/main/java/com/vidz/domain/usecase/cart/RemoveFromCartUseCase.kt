package com.vidz.domain.usecase.cart

import com.vidz.domain.model.CheckoutItem
import com.vidz.domain.repository.CartRepository
import javax.inject.Inject

class RemoveFromCartUseCase @Inject constructor(
    private val cartRepository: CartRepository
) {
    suspend operator fun invoke(item: CheckoutItem) {
        cartRepository.removeFromCart(item)
    }
} 