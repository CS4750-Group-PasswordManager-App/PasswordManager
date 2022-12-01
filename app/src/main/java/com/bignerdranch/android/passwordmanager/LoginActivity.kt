package com.bignerdranch.android.passwordmanager

import android.os.Bundle
import android.os.PersistableBundle
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class LoginActivity : AppCompatActivity() {

    private lateinit var editName: EditText
    private lateinit var editPassword: EditText

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setContentView(R.layout.login_main_activity)

        editName = findViewById(R.id.etName)
        editPassword = findViewById(R.id.etPassword)
    }
}