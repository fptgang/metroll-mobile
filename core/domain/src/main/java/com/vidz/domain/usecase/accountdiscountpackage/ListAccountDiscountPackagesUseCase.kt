package com.vidz.domain.usecase.accountdiscountpackage

import com.vidz.domain.Result
import com.vidz.domain.model.AccountDiscountPackage
import com.vidz.domain.model.AccountDiscountPackageListParams
import com.vidz.domain.model.PageDto
import com.vidz.domain.repository.AccountDiscountPackageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListAccountDiscountPackagesUseCase @Inject constructor(
    private val repository: AccountDiscountPackageRepository
) {
    operator fun invoke(params: AccountDiscountPackageListParams): Flow<Result<PageDto<AccountDiscountPackage>>> {
        return repository.listAccountDiscountPackages(params)
    }
} 