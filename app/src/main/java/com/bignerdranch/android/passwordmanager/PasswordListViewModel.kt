package com.bignerdranch.android.passwordmanager

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "PasswordListViewModel"

class PasswordListViewModel : ViewModel() {

    private val passwordRepository = PasswordRepository.get()

    private val _passwords: MutableStateFlow<List<Password>> = MutableStateFlow(emptyList())
    val passwords: StateFlow<List<Password>>
        get() = _passwords.asStateFlow()

    init {
        viewModelScope.launch{
            passwordRepository.getPasswords().collect {
                _passwords.value = it
            }
        }
    }


    override fun onCleared() {
        super.onCleared()
        Log.d(TAG, "LIST VIEW MODEL CLEARED")

    }

    suspend fun addPassword(password: Password) {
        passwordRepository.addPassword(password)
    }

    fun setQuery(query: String) {
        val searchQuery = "%$query%"
        viewModelScope.launch {
            retrievePasswordEntries(searchQuery).collect {
                _passwords.value = it
            }
        }
    }

    private fun retrievePasswordEntries(query: String) : Flow<List<Password>> {
        return if(query != "%%" && query != "" && query != null && query != " "){
            passwordRepository.getPasswordEntry(query)
        }
        else {
            passwordRepository.getPasswords()
        }
    }
}