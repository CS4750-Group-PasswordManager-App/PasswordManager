package com.bignerdranch.android.passwordmanager

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.*

class PasswordEntryViewModel(passwordId: UUID) : ViewModel() {

    private val passwordRepository = PasswordRepository.get()

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
        password.value?.let { passwordRepository.updatePassword(it)}
        // If back button is used, it passes the entry to update if any values occurred
    }

}


class PasswordEntryViewModelFactory(private val passwordId: UUID) : ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>) : T {
        return PasswordEntryViewModel(passwordId) as T
    }

}