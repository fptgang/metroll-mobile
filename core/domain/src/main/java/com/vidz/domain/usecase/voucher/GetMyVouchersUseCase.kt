package com.vidz.domain.usecase.voucher

import com.vidz.domain.Result
import com.vidz.domain.model.Voucher
import com.vidz.domain.repository.VoucherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMyVouchersUseCase @Inject constructor(
    private val voucherRepository: VoucherRepository
) {
    operator fun invoke(): Flow<Result<List<Voucher>>> {
        return voucherRepository.getMyVouchers()
    }
} 