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
    }

    fun storePassword(decrypted : Boolean) {
        if(decrypted){
            encryptPassword()
        }
        onCleared()
    }


    fun deletePassword() {
        password.value?.let { passwordRepository.deletePassword(it) }
        onCleared()
    }


    fun encryptPassword() {
        password.value?.let {
            var cipher = cryptoManager.encrypt(it.cipherText)
            //Store Ciphertext in password
            updatePassword { password -> password.copy(cipherText = String(cipher.first)) }

            //Store All values in database
            password.value?.let { passwordRepository.updatePassword(it)}

            //Store Cipher Vectors
            passwordRepository.updateVector(cipher.second, it.id)
            passwordRepository.updatePasswordVector(cipher.first, it.id)
            passwordRepository.storeEncryption(String(cipher.first), it.id)
        }

    }

    fun decryptPassword() : String {
        var plain = ""
        password.value?.let {

            var cipherFirst = it.password.toList()
            var cipherSecond = it.iv.toList()

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