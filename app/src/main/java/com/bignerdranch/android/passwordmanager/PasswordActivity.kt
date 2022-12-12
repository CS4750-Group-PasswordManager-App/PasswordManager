package com.bignerdranch.android.passwordmanager

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity

class PasswordActivity : AppCompatActivity() {

    private lateinit var userName : String
    private lateinit var passWord : String
    private lateinit var enterButton: Button
    lateinit var userNameInput : EditText
    lateinit var passwordInput : EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login_main_activity)

        userNameInput = findViewById(R.id.userInput)
        passwordInput = findViewById(R.id.login_password_entry)

        enterButton = findViewById(R.id.enter_button)

        userName = userNameInput.toString()
        passWord = passwordInput.toString()

        if(userNameInput.toString().equals(userName) && passwordInput.toString().equals(passWord)
            && !TextUtils.isEmpty(userNameInput.toString()) && !TextUtils.isEmpty(passwordInput.toString())
        ) {
            enterButton.setEnabled(true)
            enterButton.setOnClickListener {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        } else {
            enterButton.setEnabled(false)
        }


    }


}