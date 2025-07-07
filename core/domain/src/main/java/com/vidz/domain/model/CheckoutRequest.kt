package com.vidz.domain.model

data class CheckoutRequest(
    val items: List<CheckoutItem>,
    val paymentMethod: String,
    val voucherId: String? = null,
//    val discountPackage: String? = null,
    val customerId: String? = null
) 