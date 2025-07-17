package com.vidz.data.repository

import com.vidz.data.flow.ServerFlow
import com.vidz.data.mapper.AccountMapper
import com.vidz.data.server.retrofit.RetrofitServer
import com.vidz.domain.Result
import com.vidz.domain.model.Account
import com.vidz.domain.model.AccountCreateRequest
import com.vidz.domain.model.AccountListParams
import com.vidz.domain.model.AccountUpdateRequest
import com.vidz.domain.model.PageDto
import com.vidz.domain.repository.AccountManagementRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AccountManagementRepositoryImpl @Inject constructor(
    private val retrofitServer: RetrofitServer,
    private val accountMapper: AccountMapper
) : AccountManagementRepository {

    override fun getAccountById(id: String): Flow<Result<Account>> {
        return ServerFlow(
            getData = { retrofitServer.accountApi.getAccountById(id) },
            convert = { response ->
                val body = response.body() ?: throw NullPointerException("Body is null")
                accountMapper.toDomain(body)
            }
        ).execute()
    }

    override fun listAccounts(params: AccountListParams): Flow<Result<PageDto<Account>>> {
        TODO("Not implemented")
    }

    override fun createAccount(request: AccountCreateRequest): Flow<Result<Account>> {
        TODO("Not implemented")
    }

    override fun updateAccount(
        id: String,
        request: AccountUpdateRequest
    ): Flow<Result<Account>> {
        return ServerFlow(
            getData = {
                val accountUpdate = AccountUpdateRequest(
                    fullName = request.fullName ?: "",
                    phoneNumber = request.phoneNumber ?: "",
                    role = request.role
                )
                retrofitServer.accountApi.updateAccount(id, accountUpdate)
            },
            convert = { response ->
                val body = response.body() ?: throw NullPointerException("Body is null")
                accountMapper.toDomain(body)
            }
        ).execute()
    }

    override fun deactivateAccount(id: String): Flow<Result<Unit>> {
        TODO("Not implemented")
    }

    override fun login(
        idToken: String
    ): Flow<Result<Account>> {
        return ServerFlow(
            getData = { retrofitServer.authApi.login() },
            convert = { response ->
                val body = response.body() ?: throw NullPointerException("Body is null")
                accountMapper.toDomain(body)
            }
        ).execute()
    }


    override fun me(): Flow<Result<Account>> {
        return ServerFlow(
            getData = { retrofitServer.authApi.getCurrentUser() },
            convert = { response ->
                val body = response.body() ?: throw NullPointerException("Body is null")
                accountMapper.toDomain(body)
            }
        ).execute()
    }

    override fun getMyDiscountPercentage(): Flow<Result<Float?>> {
        return ServerFlow(
            getData = { retrofitServer.accountApi.getMyDiscountPercentage() },
            convert = { response ->
                response.body()
            }
        ).execute()
    }
} 
