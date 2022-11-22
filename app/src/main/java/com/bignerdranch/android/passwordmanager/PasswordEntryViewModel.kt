package com.bignerdranch.android.passwordmanager

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.material.tabs.TabLayout.TabGravity
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

        println(password.value)

        // Saving Database Values
        password.value?.let { passwordRepository.updatePassword(it)}


        var cipher = password.value?.let { cryptoManager.encrypt(it.password) }
        if (cipher != null) {
            println("Cipher: " + String(cipher.first))
        }

        //password.value.password = cipher.first;


        var decryptedText = cipher?.let { cryptoManager.decrypt(it.first, cipher.second) }

        println("Decrypted_Data: $decryptedText" )

        // If back button is used, it passes the entry to update if any values occurred
    }
}


class PasswordEntryViewModelFactory(private val passwordId: UUID) : ViewModelProvider.Factory {

    @RequiresApi(Build.VERSION_CODES.M)
    override fun <T : ViewModel> create(modelClass: Class<T>) : T {
        return PasswordEntryViewModel(passwordId) as T
    }

}