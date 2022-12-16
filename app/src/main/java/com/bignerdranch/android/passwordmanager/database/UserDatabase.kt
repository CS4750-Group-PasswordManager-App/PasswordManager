package com.bignerdranch.android.passwordmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.bignerdranch.android.passwordmanager.UserInfo

@Database(entities = [UserInfo::class], version = 1, exportSchema = false)
abstract class UserDatabase : RoomDatabase(){

    abstract val userDao: UserDao

}