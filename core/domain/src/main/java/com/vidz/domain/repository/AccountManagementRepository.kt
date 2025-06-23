package com.vidz.domain.repository

import com.vidz.domain.Result
import com.vidz.domain.model.Account
import com.vidz.domain.model.AccountCreateRequest
import com.vidz.domain.model.AccountListParams
import com.vidz.domain.model.AccountUpdateRequest
import com.vidz.domain.model.PageDto
import kotlinx.coroutines.flow.Flow

interface AccountManagementRepository {
    fun getAccountById(id: String): Flow<Result<Account>>
    fun listAccounts(params: AccountListParams): Flow<Result<PageDto<Account>>>
    fun createAccount(request: AccountCreateRequest): Flow<Result<Account>>
    fun updateAccount(id: String, request: AccountUpdateRequest): Flow<Result<Account>>
    fun deactivateAccount(id: String): Flow<Result<Unit>>
    fun login(idToken : String): Flow<Result<Account>>
    fun me(): Flow<Result<Account>>
} 
