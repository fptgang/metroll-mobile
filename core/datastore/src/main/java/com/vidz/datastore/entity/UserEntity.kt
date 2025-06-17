package com.vidz.datastore.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: Long = 0L,
    val email: String = "",
    val firstName: String = "",
    val lastName: String = "",
    val isEmailVerified: Boolean = false,
    val createdAt: String = "",
    val updatedAt: String = ""
) 