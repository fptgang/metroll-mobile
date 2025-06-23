package com.vidz.domain.usecase.discountpackage

import com.vidz.domain.Result
import com.vidz.domain.model.DiscountPackage
import com.vidz.domain.model.DiscountPackageUpdateRequest
import com.vidz.domain.repository.DiscountPackageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateDiscountPackageUseCase @Inject constructor(
    private val discountPackageRepository: DiscountPackageRepository
) {
    operator fun invoke(id: String, request: DiscountPackageUpdateRequest): Flow<Result<DiscountPackage>> {
        return discountPackageRepository.updateDiscountPackage(id, request)
    }
} 