package com.vidz.domain.usecase.accountdiscountpackage

import com.vidz.domain.Result
import com.vidz.domain.repository.AccountDiscountPackageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UnassignDiscountPackageUseCase @Inject constructor(
    private val repository: AccountDiscountPackageRepository
) {
    operator fun invoke(id: String): Flow<Result<Unit>> {
        return repository.unassignDiscountPackage(id)
    }
} 