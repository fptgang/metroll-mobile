package com.vidz.domain.usecase.discountpackage

import com.vidz.domain.Result
import com.vidz.domain.model.DiscountPackage
import com.vidz.domain.model.DiscountPackageCreateRequest
import com.vidz.domain.repository.DiscountPackageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CreateDiscountPackageUseCase @Inject constructor(
    private val discountPackageRepository: DiscountPackageRepository
) {
    operator fun invoke(request: DiscountPackageCreateRequest): Flow<Result<DiscountPackage>> {
        return discountPackageRepository.createDiscountPackage(request)
    }
} 