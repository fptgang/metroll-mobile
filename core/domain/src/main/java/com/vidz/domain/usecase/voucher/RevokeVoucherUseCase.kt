package com.vidz.domain.usecase.voucher

import com.vidz.domain.Result
import com.vidz.domain.repository.VoucherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class RevokeVoucherUseCase @Inject constructor(
    private val voucherRepository: VoucherRepository
) {
    operator fun invoke(id: String): Flow<Result<Unit>> {
        return voucherRepository.revokeVoucher(id)
    }
} 