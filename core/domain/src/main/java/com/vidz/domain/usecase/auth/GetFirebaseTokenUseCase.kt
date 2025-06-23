package com.vidz.domain.usecase.auth

import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class GetFirebaseTokenUseCase @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    suspend operator fun invoke(forceRefresh: Boolean = false): String? {
        return try {
            val user = firebaseAuth.currentUser
            if (user != null) {
                val tokenResult = user.getIdToken(forceRefresh).await()
                tokenResult.token
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
} 