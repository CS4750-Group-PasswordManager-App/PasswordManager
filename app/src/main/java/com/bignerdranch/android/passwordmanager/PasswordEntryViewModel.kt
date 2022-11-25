package com.bignerdranch.android.passwordmanager

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*
@RequiresApi(Build.VERSION_CODES.M)

class PasswordEntryViewModel(passwordId: UUID) : ViewModel() {

    private val passwordRepository = PasswordRepository.get()
    private var cryptoManager = CryptoManager()

    private val _password: MutableStateFlow<Password?> = MutableStateFlow(null)
    val password: StateFlow<Password?> = _password.asStateFlow()

    init {
        viewModelScope.launch {
            _password.value = passwordRepository.getPassword(passwordId)
        }
    }

    fun updatePassword(onUpdate: (Password) -> Password) {
        _password.update { oldPassword ->
            oldPassword?.let { onUpdate(it) }
        }
    }

    suspend fun addPassword(password: Password) {
        passwordRepository.addPassword(password)
    }


    override fun onCleared() {
        super.onCleared()


        password.value?.let {
            if(it.password == "" || it.title == "" || it.username == ""){
                passwordRepository.deletePassword(it)
            }
        }

        val recentDate = Date()
        password.value?.let {
                passwordRepository.updateLastOpened(recentDate, it.id) }


//
//        var cipher = password.value?.let { cryptoManager.encrypt(it.password) }
//        if (cipher != null) {
//            println("Cipher: " + String(cipher.first))
//        }
//
//        var decryptedText = cipher?.let { cryptoManager.decrypt(it.first, cipher.second) }
//
//        println("Decrypted_Data: $decryptedText" )

    }

    fun storePassword() {
        password.value?.let { passwordRepository.updatePassword(it)}
        encryptPassword()
        onCleared()
    }

    fun deletePassword() {
        password.value?.let { passwordRepository.deletePassword(it) }
        onCleared()
    }

    fun encryptPassword() {
        password.value?.let {
            var cipher = cryptoManager.encrypt(it.password)
            println("INITIAL IV: " + it.iv)
            println(cipher.first)
            println(cipher.first.toString())
            println(String(cipher.first))
            println(cipher.second)
            passwordRepository.updateVector(cipher.second, it.id)
        }


    }

    fun decryptPassword() {
        password.value?.let {
            var cipher = cryptoManager.decrypt(it.password.toByteArray(), it.iv)
            println(cipher)
        }
    }

}


class PasswordEntryViewModelFactory(private val passwordId: UUID) : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun <T : ViewModel> create(modelClass: Class<T>) : T {
        return PasswordEntryViewModel(passwordId) as T
    }

}