package com.vidz.domain.usecase.accountdiscountpackage

import com.vidz.domain.Result
import com.vidz.domain.model.AccountDiscountPackage
import com.vidz.domain.repository.AccountDiscountPackageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAccountDiscountPackageByIdUseCase @Inject constructor(
    private val repository: AccountDiscountPackageRepository
) {
    operator fun invoke(id: String): Flow<Result<AccountDiscountPackage>> {
        return repository.getAccountDiscountPackageById(id)
    }
} 