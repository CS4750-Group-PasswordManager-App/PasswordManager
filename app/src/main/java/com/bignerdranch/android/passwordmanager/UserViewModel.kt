package com.bignerdranch.android.passwordmanager

import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import java.util.Observable

class UserViewModel(private val repository: UserRepository, application: PasswordManagerApplication): AndroidViewModel(application) {

    val users = repository.users
    val inputUserEmail = MutableLiveData<String>()
    val inputUserPassword = MutableLiveData<String>()
    private val navigatetoSignup = MutableLiveData<Boolean>()

    fun signUp() {
        navigatetoSignup.value = true
    }
}