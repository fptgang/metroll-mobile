package com.vidz.data.repository

import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.VoucherMapper
import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.domain.Result
import com.vidz.domain.model.PageDto
import com.vidz.domain.model.Voucher
import com.vidz.domain.model.VoucherCreateRequest
import com.vidz.domain.model.VoucherListParams
import com.vidz.domain.model.VoucherUpdateRequest
import com.vidz.domain.repository.VoucherRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoucherRepositoryImpl @Inject constructor(
    private val retrofitServer: RetrofitServer,
    private val voucherMapper: VoucherMapper
) : VoucherRepository {

    override fun getVoucherById(id: String): Flow<Result<Voucher>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.voucherApi.getVoucherById(id)
                response.body() ?: throw NullPointerException("Get voucher by ID response body is null")
            },
            convert = { voucherDto ->
                voucherMapper.toDomain(voucherDto)
            }
        ).execute()
    }

    override fun listVouchers(params: VoucherListParams): Flow<Result<PageDto<Voucher>>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.voucherApi.listVouchers(
                    page = params.page,
                    size = params.size,
                    userId = params.userId
                )
                response.body() ?: throw NullPointerException("List vouchers response body is null")
            },
            convert = { pageDto ->
                PageDto(
                    content = voucherMapper.toDomainList(pageDto.content),
                    pageNumber = pageDto.pageNumber,
                    pageSize = pageDto.pageSize,
                    totalElements = pageDto.totalElements,
                    totalPages = pageDto.totalPages,
                    last = pageDto.last
                )
            }
        ).execute()
    }

    override fun getMyVouchers(): Flow<Result<List<Voucher>>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.voucherApi.getMyVouchers()
                response.body() ?: throw NullPointerException("Get my vouchers response body is null")
            },
            convert = { voucherDtoList ->
                voucherMapper.toDomainList(voucherDtoList)
            }
        ).execute()
    }

    override fun getVoucherByCode(code: String): Flow<Result<Voucher>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.voucherApi.getVoucherByCode(code)
                response.body() ?: throw NullPointerException("Get voucher by code response body is null")
            },
            convert = { voucherDto ->
                voucherMapper.toDomain(voucherDto)
            }
        ).execute()
    }

    override fun createVoucher(request: VoucherCreateRequest): Flow<Result<List<Voucher>>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.voucherApi.createVoucher(
                    voucherMapper.createRequestToDto(request)
                )
                response.body() ?: throw NullPointerException("Create voucher response body is null")
            },
            convert = { voucherDtoList ->
                voucherMapper.toDomainList(voucherDtoList)
            }
        ).execute()
    }

    override fun updateVoucher(id: String, request: VoucherUpdateRequest): Flow<Result<Voucher>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.voucherApi.updateVoucher(
                    id = id,
                    request = voucherMapper.updateRequestToDto(request)
                )
                response.body() ?: throw NullPointerException("Update voucher response body is null")
            },
            convert = { voucherDto ->
                voucherMapper.toDomain(voucherDto)
            }
        ).execute()
    }

    override fun revokeVoucher(id: String): Flow<Result<Unit>> {
        return ServerFlow(
            getData = {
                val response = retrofitServer.voucherApi.revokeVoucher(id)
                response.body() ?: Unit
            },
            convert = { _ -> Unit }
        ).execute()
    }
} 