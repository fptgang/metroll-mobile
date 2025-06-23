package com.vidz.domain.usecase.account

import com.vidz.domain.Result
import com.vidz.domain.model.Account
import com.vidz.domain.repository.AccountManagementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMeUseCase @Inject constructor(
    private val accountRepository: AccountManagementRepository
) {
    operator fun invoke(): Flow<Result<Account>> {
        return accountRepository.me()
    }
} 