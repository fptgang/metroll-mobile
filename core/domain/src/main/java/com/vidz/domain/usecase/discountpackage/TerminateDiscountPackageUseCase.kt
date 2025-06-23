package com.vidz.domain.usecase.discountpackage

import com.vidz.domain.Result
import com.vidz.domain.repository.DiscountPackageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class TerminateDiscountPackageUseCase @Inject constructor(
    private val discountPackageRepository: DiscountPackageRepository
) {
    operator fun invoke(id: String): Flow<Result<Unit>> {
        return discountPackageRepository.terminateDiscountPackage(id)
    }
} 