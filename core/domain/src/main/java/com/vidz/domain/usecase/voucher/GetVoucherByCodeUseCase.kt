package com.vidz.domain.usecase.voucher

import com.vidz.domain.Result
import com.vidz.domain.model.Voucher
import com.vidz.domain.repository.VoucherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetVoucherByCodeUseCase @Inject constructor(
    private val voucherRepository: VoucherRepository
) {
    operator fun invoke(code: String): Flow<Result<Voucher>> {
        return voucherRepository.getVoucherByCode(code)
    }
} 