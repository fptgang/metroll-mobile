package com.vidz.domain.usecase.voucher

import com.vidz.domain.Result
import com.vidz.domain.model.Voucher
import com.vidz.domain.model.VoucherCreateRequest
import com.vidz.domain.repository.VoucherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateVoucherUseCase @Inject constructor(
    private val voucherRepository: VoucherRepository
) {
    operator fun invoke(request: VoucherCreateRequest): Flow<Result<List<Voucher>>> {
        return voucherRepository.createVoucher(request)
    }
} 