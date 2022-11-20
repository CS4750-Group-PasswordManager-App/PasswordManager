package com.bignerdranch.android.passwordmanager

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date
import java.util.UUID


@Entity
data class Password(
    @PrimaryKey val id: UUID,
    val title: String,
    val email: String,
    val username: String, //Optional or if empty copy email to it?
    val password: String,
    val iv: ByteArray,
    val accessDate: Date,
)
