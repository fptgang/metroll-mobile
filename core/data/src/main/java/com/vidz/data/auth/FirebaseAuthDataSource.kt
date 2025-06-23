package com.vidz.data.auth

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.UserProfileChangeRequest
import com.vidz.domain.model.LoginCredentials
import com.vidz.domain.model.RegisterCredentials
import com.vidz.domain.model.User
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthDataSource @Inject constructor(
    private val firebaseAuth: FirebaseAuth
) {
    
    suspend fun loginWithEmailAndPassword(credentials: LoginCredentials): User {
        val result = firebaseAuth.signInWithEmailAndPassword(
            credentials.email,
            credentials.password
        ).await()
        
        return result.user?.let { firebaseUser ->
            mapFirebaseUserToUser(firebaseUser)
        } ?: throw Exception("Login failed: User is null")
    }
    
    suspend fun registerWithEmailAndPassword(credentials: RegisterCredentials): User {
        if (credentials.password != credentials.confirmPassword) {
            throw Exception("Passwords do not match")
        }
        
        val result = firebaseAuth.createUserWithEmailAndPassword(
            credentials.email,
            credentials.password
        ).await()
        
        val firebaseUser = result.user ?: throw Exception("Registration failed: User is null")
        
        // Update profile if display name is provided
        credentials.displayName?.let { displayName ->
            val profileUpdates = UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build()
            firebaseUser.updateProfile(profileUpdates).await()
        }
        
        return mapFirebaseUserToUser(firebaseUser)
    }
    
    suspend fun logout() {
        firebaseAuth.signOut()
    }
    
    fun getCurrentUser(): User? {
        return firebaseAuth.currentUser?.let { firebaseUser ->
            mapFirebaseUserToUser(firebaseUser)
        }
    }
    
    suspend fun sendPasswordResetEmail(email: String) {
        firebaseAuth.sendPasswordResetEmail(email).await()
    }
    
    suspend fun deleteAccount() {
        firebaseAuth.currentUser?.delete()?.await()
            ?: throw Exception("No user logged in")
    }
    
    fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }
    
    private fun mapFirebaseUserToUser(firebaseUser: FirebaseUser): User {
        return User(
            uid = firebaseUser.uid,
            email = firebaseUser.email,
            displayName = firebaseUser.displayName,
            photoUrl = firebaseUser.photoUrl?.toString(),
            isEmailVerified = firebaseUser.isEmailVerified,
            createdAt = firebaseUser.metadata?.creationTimestamp
        )
    }
} 