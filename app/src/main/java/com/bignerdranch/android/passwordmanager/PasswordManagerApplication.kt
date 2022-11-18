package com.bignerdranch.android.passwordmanager

import android.app.Application


//One time initialization when the application is first loaded into memory.
//Destoryed when app process is destroyed. Not recreated when configuration changes
class PasswordManagerApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        PasswordRepository.initialize(this)
    }

}