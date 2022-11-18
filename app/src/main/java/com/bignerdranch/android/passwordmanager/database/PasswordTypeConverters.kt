package com.bignerdranch.android.passwordmanager.database

import androidx.room.TypeConverter
import java.util.*

class PasswordTypeConverters {

    @TypeConverter
    fun fromDate(date: Date): Long {
        return date.time
    }

    @TypeConverter
    fun toDate(millisSinceEpoch: Long) : Date {
        return Date(millisSinceEpoch)
    }


}