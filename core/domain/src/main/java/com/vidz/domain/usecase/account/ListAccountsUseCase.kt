package com.vidz.domain.usecase.account

import com.vidz.domain.Result
import com.vidz.domain.model.Account
import com.vidz.domain.model.AccountListParams
import com.vidz.domain.model.PageDto
import com.vidz.domain.repository.AccountManagementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ListAccountsUseCase @Inject constructor(
    private val accountRepository: AccountManagementRepository
) {
    operator fun invoke(params: AccountListParams): Flow<Result<PageDto<Account>>> {
        return accountRepository.listAccounts(params)
    }
} 