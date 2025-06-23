package com.vidz.domain.usecase.voucher

import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.Voucher
import com.vidz.domain.model.VoucherListParams
import com.vidz.domain.repository.VoucherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListVouchersUseCase @Inject constructor(
    private val voucherRepository: VoucherRepository
) {
    operator fun invoke(params: VoucherListParams): Flow<Result<PageDto<Voucher>>> {
        return voucherRepository.listVouchers(params)
    }
} 