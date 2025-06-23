package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.AccountDiscountAssignRequest
import com.vidz.domain.model.AccountDiscountPackage
import com.vidz.domain.model.AccountDiscountPackageListParams
import com.vidz.domain.model.PageDto
import kotlinx.coroutines.flow.Flow

interface AccountDiscountPackageRepository {
    fun getAccountDiscountPackageById(id: String): Flow<Result<AccountDiscountPackage>>
    fun listAccountDiscountPackages(params: AccountDiscountPackageListParams): Flow<Result<PageDto<AccountDiscountPackage>>>
    fun assignDiscountPackage(request: AccountDiscountAssignRequest): Flow<Result<AccountDiscountPackage>>
    fun unassignDiscountPackage(id: String): Flow<Result<Unit>>
} 