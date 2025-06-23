package com.vidz.domain.usecase.account

import com.vidz.domain.Result
import com.vidz.domain.repository.AccountManagementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class DeactivateAccountUseCase @Inject constructor(
    private val accountRepository: AccountManagementRepository
) {
    operator fun invoke(id: String): Flow<Result<Unit>> {
        return accountRepository.deactivateAccount(id)
    }
} 