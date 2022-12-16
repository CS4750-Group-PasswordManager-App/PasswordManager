package com.bignerdranch.android.passwordmanager.database

import android.provider.ContactsContract.CommonDataKinds.Email
import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.bignerdranch.android.passwordmanager.UserInfo

@Dao
interface UserDao {
    @Insert
    suspend fun addUser(info: UserInfo)

    @Query("SELECT * FROM UserInfo ORDER BY userId DESC")
    fun getAllUsers(): LiveData<List<UserInfo>>

    @Query("SELECT * FROM UserInfo WHERE userEmail LIKE (:userEmail)")
    suspend fun getUseremail(userEmail: String): UserInfo?

}