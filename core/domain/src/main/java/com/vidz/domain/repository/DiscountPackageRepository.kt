package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.DiscountPackage
import com.vidz.domain.model.DiscountPackageCreateRequest
import com.vidz.domain.model.DiscountPackageListParams
import com.vidz.domain.model.DiscountPackageUpdateRequest
import com.vidz.domain.model.PageDto
import kotlinx.coroutines.flow.Flow

interface DiscountPackageRepository {
    fun getDiscountPackageById(id: String): Flow<Result<DiscountPackage>>
    fun listDiscountPackages(params: DiscountPackageListParams): Flow<Result<PageDto<DiscountPackage>>>
    fun createDiscountPackage(request: DiscountPackageCreateRequest): Flow<Result<DiscountPackage>>
    fun updateDiscountPackage(id: String, request: DiscountPackageUpdateRequest): Flow<Result<DiscountPackage>>
    fun terminateDiscountPackage(id: String): Flow<Result<Unit>>
} 