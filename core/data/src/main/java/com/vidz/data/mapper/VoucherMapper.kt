package com.vidz.data.mapper

import com.vidz.data.server.dto.VoucherCreateRequestDto
import com.vidz.data.server.dto.VoucherDto
import com.vidz.data.server.dto.VoucherUpdateRequestDto
import com.vidz.domain.model.Voucher
import com.vidz.domain.model.VoucherCreateRequest
import com.vidz.domain.model.VoucherStatus
import com.vidz.domain.model.VoucherUpdateRequest
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VoucherMapper @Inject constructor() : BaseRemoteMapper<Voucher, VoucherDto> {

    override fun toDomain(external: VoucherDto): Voucher {
        return Voucher(
            id = external.id,
//            ownerId = external.ownerId,
            code = external.code,
            discountAmount = external.discountAmount,
            minTransactionAmount = external.minTransactionAmount,
            validFrom = external.validFrom,
            validUntil = external.validUntil,
            status = try {
                VoucherStatus.valueOf(external.status)
            } catch (e: IllegalArgumentException) {
                VoucherStatus.PRESERVED
            },
            createdAt = external.createdAt,
            updatedAt = external.updatedAt
        )
    }

    override fun toRemote(domain: Voucher): VoucherDto {
        return VoucherDto(
            id = domain.id,
//            ownerId = domain.ownerId,
            code = domain.code,
            discountAmount = domain.discountAmount,
            minTransactionAmount = domain.minTransactionAmount,
            validFrom = domain.validFrom,
            validUntil = domain.validUntil,
            status = domain.status.name,
            createdAt = domain.createdAt,
            updatedAt = domain.updatedAt
        )
    }

    fun createRequestToDto(domain: VoucherCreateRequest): VoucherCreateRequestDto {
        return VoucherCreateRequestDto(
            discountAmount = domain.discountAmount,
            minTransactionAmount = domain.minTransactionAmount,
            validFrom = domain.validFrom,
            validUntil = domain.validUntil,
//            ownerIds = domain.ownerIds
        )
    }

    fun updateRequestToDto(domain: VoucherUpdateRequest): VoucherUpdateRequestDto {
        return VoucherUpdateRequestDto(
            discountAmount = domain.discountAmount,
            minTransactionAmount = domain.minTransactionAmount,
            validFrom = domain.validFrom,
            validUntil = domain.validUntil
        )
    }
} 