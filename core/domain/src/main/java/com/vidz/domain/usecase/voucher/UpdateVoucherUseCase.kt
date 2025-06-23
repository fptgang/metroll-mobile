package com.vidz.domain.usecase.voucher

import com.vidz.domain.Result
import com.vidz.domain.model.Voucher
import com.vidz.domain.model.VoucherUpdateRequest
import com.vidz.domain.repository.VoucherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateVoucherUseCase @Inject constructor(
    private val voucherRepository: VoucherRepository
) {
    operator fun invoke(id: String, request: VoucherUpdateRequest): Flow<Result<Voucher>> {
        return voucherRepository.updateVoucher(id, request)
    }
} 