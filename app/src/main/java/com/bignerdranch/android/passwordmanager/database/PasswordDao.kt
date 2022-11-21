package com.bignerdranch.android.passwordmanager.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bignerdranch.android.passwordmanager.Password
import kotlinx.coroutines.flow.Flow
import java.util.UUID

//Queries will be created here. Data Access Object
@Dao
interface PasswordDao {

    @Query("SELECT * From Password Order By accessDate DESC") //Order Descending by accessdate
    fun getPasswords(): Flow<List<Password>>

    @Query("SELECT * From Password Where id=(:id)")
    suspend fun getPassword(id: UUID) : Password

    @Query("SELECT * From PASSWORD Where title LIKE (:title)")
    fun getPasswordEntry(title: String): Flow<List<Password>>

    @Update
    suspend fun updatePassword(password: Password)

    @Insert
    suspend fun addPassword(password: Password)

    //Delete Query

}