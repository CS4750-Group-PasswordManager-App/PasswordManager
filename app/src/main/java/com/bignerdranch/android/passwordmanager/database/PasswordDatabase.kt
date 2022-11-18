package com.bignerdranch.android.passwordmanager.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.bignerdranch.android.passwordmanager.Password


@Database(entities = [Password::class], version=1, exportSchema = false)
@TypeConverters(PasswordTypeConverters::class)
abstract class PasswordDatabase : RoomDatabase() {

    abstract fun passwordDao(): PasswordDao

}