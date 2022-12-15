package com.bignerdranch.android.passwordmanager.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.bignerdranch.android.passwordmanager.Password
import kotlinx.coroutines.flow.Flow
import java.util.Date
import java.util.UUID

//Queries will be created here. Data Access Object
@Dao
interface PasswordDao {

    @Query("SELECT * From Password Order By accessDate DESC") //Order Descending by accessdate
    fun getPasswords(): Flow<List<Password>>

    @Query("SELECT * From Password Where id=(:id)")
    suspend fun getPassword(id: UUID) : Password

    @Query("SELECT * From PASSWORD Where title LIKE (:title) Order By accessDate DESC")
    fun getPasswordEntry(title: String): Flow<List<Password>>

    @Query("Update PASSWORD Set accessDate = (:accessDate) WHERE id = (:id)")
    suspend fun updateLastOpened(accessDate : Date, id:UUID)

    @Query("SELECT iv From Password Where id=(:id)")
    suspend fun getPasswordVector(id: UUID) : ByteArray

    @Query("Update PASSWORD Set iv = (:iv) WHERE id = (:id)")
    suspend fun updateVector(iv : ByteArray, id:UUID)


    @Query("Update Password Set password=(:password) Where id=(:id)")
    suspend fun  updatePasswordVector(password: ByteArray, id: UUID)

    @Query("Update PASSWORD Set cipherText = (:cipherText) WHERE id = (:id)")
    suspend fun storeEncryption(cipherText: String, id:UUID)

    @Update
    suspend fun updatePassword(password: Password)

    @Insert
    suspend fun addPassword(password: Password)

    @Delete
    suspend fun deletePassword(password: Password)

}