package com.vidz.domain.repository

import com.vidz.domain.Result
import kotlinx.coroutines.flow.Flow

interface AppConfigRepository {
    fun getAppVersion(): Flow<Result<String>>
    
    fun isMaintenanceMode(): Flow<Result<Boolean>>
    
    fun getMaintenanceMessage(): Flow<Result<String>>
    
    fun checkForUpdates(): Flow<Result<UpdateInfo>>
}

data class UpdateInfo(
    val isUpdateRequired: Boolean,
    val currentVersion: String,
    val latestVersion: String,
    val updateUrl: String? = null
)