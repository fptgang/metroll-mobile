package com.vidz.data.server.dto

import kotlinx.serialization.Serializable

@Serializable
data class CheckoutRequestDto(
    val items: List<CheckoutItemRequestDto>,
    val paymentMethod: String,
    val voucherId: String? = null,
//    val discountPackage: String? = null,
    val customerId: String? = null
) 