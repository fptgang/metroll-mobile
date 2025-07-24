package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.Voucher
import com.vidz.domain.model.VoucherCreateRequest
import com.vidz.domain.model.VoucherListParams
import com.vidz.domain.model.VoucherUpdateRequest
import kotlinx.coroutines.flow.Flow

interface VoucherRepository {
    fun getVoucherById(id: String): Flow<Result<Voucher>>
    fun listVouchers(params: VoucherListParams): Flow<Result<PageDto<Voucher>>>
    fun getMyVouchers(): Flow<Result<List<Voucher>>>
    fun getVoucherByCode(code: String): Flow<Result<Voucher>>
    fun createVoucher(request: VoucherCreateRequest): Flow<Result<List<Voucher>>>
    fun updateVoucher(id: String, request: VoucherUpdateRequest): Flow<Result<Voucher>>
    fun revokeVoucher(id: String): Flow<Result<Unit>>
} 