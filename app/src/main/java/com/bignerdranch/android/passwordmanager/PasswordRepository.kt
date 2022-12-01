package com.bignerdranch.android.passwordmanager

import android.content.Context
import androidx.room.Room
import com.bignerdranch.android.passwordmanager.database.PasswordDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import java.util.*


private const val DATABASE_NAME = "password-database"

class PasswordRepository private constructor(context: Context,
    private val coroutineScope: CoroutineScope = GlobalScope) {

    private val database: PasswordDatabase = Room.databaseBuilder(
        context.applicationContext,
        PasswordDatabase::class.java,
        DATABASE_NAME
    ).build()

    fun getPasswords(): Flow<List<Password>> = database.passwordDao().getPasswords()

    suspend fun getPassword(id: UUID) : Password = database.passwordDao().getPassword(id)

    fun getPasswordEntry(title: String) : Flow<List<Password>> = database.passwordDao().getPasswordEntry(title)

    fun updatePassword(password: Password) {
        coroutineScope.launch {
            database.passwordDao().updatePassword(password)
        }
    }

    fun updateLastOpened(accessDate : Date, id : UUID){
        coroutineScope.launch{
            database.passwordDao().updateLastOpened(accessDate, id)
        }
    }

    suspend fun getPasswordVector(id : UUID) : ByteArray = database.passwordDao().getPasswordVector(id)


    fun updateVector(iv : ByteArray, id : UUID){
        coroutineScope.launch{
            database.passwordDao().updateVector(iv, id)
        }
    }

    fun updatePasswordVector(pass : ByteArray, id : UUID){
        coroutineScope.launch{
            database.passwordDao().updatePasswordVector(pass, id)
        }
    }



    fun storeEncryption(password: String, id : UUID){
        coroutineScope.launch{
            database.passwordDao().storeEncryption(password, id)
        }
    }

    suspend fun addPassword(password: Password){
        database.passwordDao().addPassword(password)
    }

    fun deletePassword(password: Password) {
        coroutineScope.launch {
            database.passwordDao().deletePassword(password)
        }
    }


    companion object {
        private var INSTANCE: PasswordRepository? = null

        fun initialize(context: Context) {
            if (INSTANCE == null){
                INSTANCE = PasswordRepository(context)
            }
        }

        fun get(): PasswordRepository {
            return INSTANCE ?: throw IllegalStateException("PasswordRepository must be initialized")
        }
    }
}