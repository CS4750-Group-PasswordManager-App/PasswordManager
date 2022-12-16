package com.bignerdranch.android.passwordmanager

import com.bignerdranch.android.passwordmanager.database.UserDao

class UserRepository(private val dao: UserDao) {

    val users = dao.getAllUsers()

    suspend fun addUser(user: UserInfo) {
        return dao.addUser(user)
    }

    suspend fun getUserEmail(userEmail: String):UserInfo? {
        return dao.getUseremail(userEmail)
    }
}