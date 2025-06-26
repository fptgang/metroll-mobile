package com.vidz.domain.usecase.account

import com.vidz.domain.model.Account
import com.vidz.domain.repository.UserLocalRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveLocalAccountInfoUseCase @Inject constructor(
    private val userLocalRepository: UserLocalRepository
) {
    operator fun invoke(): Flow<Account?> {
        return userLocalRepository.observeUserData()
    }
} 
