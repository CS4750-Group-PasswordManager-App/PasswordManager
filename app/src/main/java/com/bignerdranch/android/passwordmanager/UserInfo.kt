package com.bignerdranch.android.passwordmanager

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserInfo(
    @PrimaryKey(autoGenerate = true)
    val userId: Int = 0,
    val userEmail: String,
    val userPassword: String,
) {
}