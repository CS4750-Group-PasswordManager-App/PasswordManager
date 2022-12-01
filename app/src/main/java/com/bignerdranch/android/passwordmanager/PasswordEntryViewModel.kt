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
            if( it.cipherText == "" || it.title == "" || it.username == ""){
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
            var cipher = cryptoManager.encrypt(it.cipherText)
            println("INITIAL IV: " + it.iv)
            println(cipher.first)
            println(String(cipher.first))
            println(cipher.second)
            println(String(cipher.second))

//            println(cryptoManager.decrypt(cipher.first, cipher.second))
//
//            var cipherFirst = cipher.first.toList()
//            var cipherSecond = cipher.second.toList()
//
//            println("${cipherFirst} : ${cipherSecond}")
//
//            println("Conversion")
//
//            println("${cipherFirst.toByteArray()} : ${cipherSecond.toByteArray()}")
//
//            println("${String(cipherFirst.toByteArray())}")
//
//
//            println("DECRYPTING")
//            println(cryptoManager.decrypt(cipherFirst.toByteArray(), cipherSecond.toByteArray()))


            passwordRepository.updateVector(cipher.second, it.id)
            passwordRepository.updatePasswordVector(cipher.first, it.id)

            passwordRepository.storeEncryption(String(cipher.first), it.id)

        }

    }

    fun decryptPassword() : String {
        var plain = ""
        password.value?.let {
            println(it.password)

            println("DECRYPT")
            println("${it.password} : ${it.iv}")

            var cipherFirst = it.password.toList()
            var cipherSecond = it.iv.toList()

            println("${cipherFirst} : ${cipherSecond}")
            plain = cryptoManager.decrypt(cipherFirst.toByteArray(), cipherSecond.toByteArray())
        }

        return plain
    }

}


class PasswordEntryViewModelFactory(private val passwordId: UUID) : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun <T : ViewModel> create(modelClass: Class<T>) : T {
        return PasswordEntryViewModel(passwordId) as T
    }

}