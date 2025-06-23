package com.vidz.domain.usecase.accountdiscountpackage

import com.vidz.domain.Result
import com.vidz.domain.model.AccountDiscountAssignRequest
import com.vidz.domain.model.AccountDiscountPackage
import com.vidz.domain.repository.AccountDiscountPackageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AssignDiscountPackageUseCase @Inject constructor(
    private val repository: AccountDiscountPackageRepository
) {
    operator fun invoke(request: AccountDiscountAssignRequest): Flow<Result<AccountDiscountPackage>> {
        return repository.assignDiscountPackage(request)
    }
} 