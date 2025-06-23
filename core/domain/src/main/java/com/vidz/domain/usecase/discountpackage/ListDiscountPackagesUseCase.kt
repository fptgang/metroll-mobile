package com.vidz.domain.usecase.discountpackage

import com.vidz.domain.Result
import com.vidz.domain.model.DiscountPackage
import com.vidz.domain.model.DiscountPackageListParams
import com.vidz.domain.model.PageDto
import com.vidz.domain.repository.DiscountPackageRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListDiscountPackagesUseCase @Inject constructor(
    private val discountPackageRepository: DiscountPackageRepository
) {
    operator fun invoke(params: DiscountPackageListParams): Flow<Result<PageDto<DiscountPackage>>> {
        return discountPackageRepository.listDiscountPackages(params)
    }
} 