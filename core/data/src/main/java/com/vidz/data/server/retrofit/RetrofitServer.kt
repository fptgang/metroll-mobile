package com.vidz.data.server.retrofit

import com.vidz.data.server.retrofit.api.AccountApi
import com.vidz.data.server.retrofit.api.AuthApi
import com.vidz.data.server.retrofit.api.OrderApi
import com.vidz.data.server.retrofit.api.P2PJourneyApi
import com.vidz.data.server.retrofit.api.PaymentApi
import com.vidz.data.server.retrofit.api.TicketApi
import com.vidz.data.server.retrofit.api.TicketValidationApi
import com.vidz.data.server.retrofit.api.TimedTicketPlanApi
import retrofit2.Retrofit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RetrofitServer @Inject constructor(private val retrofit: Retrofit) {
    val authApi: AuthApi by lazy { retrofit.create(AuthApi::class.java) }
    val accountApi: AccountApi by lazy { retrofit.create(AccountApi::class.java) }
    val timedTicketPlanApi: TimedTicketPlanApi by lazy { retrofit.create(TimedTicketPlanApi::class.java) }
    val p2pJourneyApi: P2PJourneyApi by lazy { retrofit.create(P2PJourneyApi::class.java) }
    val ticketApi: TicketApi by lazy { retrofit.create(TicketApi::class.java) }
    val ticketValidationApi: TicketValidationApi by lazy { retrofit.create(TicketValidationApi::class.java) }
    val orderApi: OrderApi by lazy { retrofit.create(OrderApi::class.java) }
    val paymentApi: PaymentApi by lazy { retrofit.create(PaymentApi::class.java) }
}
