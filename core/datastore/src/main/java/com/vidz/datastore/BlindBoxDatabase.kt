package com.vidz.datastore

import androidx.room.Database
import androidx.room.RoomDatabase
import com.vidz.datastore.entity.UserEntity

@Database(
    entities = [UserEntity::class], // Auth-related entity
    version = 1,
    exportSchema = true
)
abstract class BlindBoxDatabase : RoomDatabase() {
    
    // TODO: Add auth-related DAOs when needed
    
    companion object {
        const val DATABASE_NAME = "metroll_database"
    }
} 
