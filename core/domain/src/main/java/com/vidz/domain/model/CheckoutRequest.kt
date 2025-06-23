package com.vidz.domain.model

data class CheckoutRequest(
    val items: List<CheckoutItem>,
    val paymentMethod: String,
    val discountPackageId: String? = null,
    val voucherId: String? = null,
    val customerId: String? = null
) 