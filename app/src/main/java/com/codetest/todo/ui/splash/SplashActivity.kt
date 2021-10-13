package com.codetest.todo.ui.splash

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.app.AppCompatDelegate
import com.codetest.todo.ui.login.LoginActivity
import com.codetest.todo.ui.main.MainActivity
import com.codetest.todo.utils.SecurePreference

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        if (SecurePreference.isLogin()) {
            val mainIntent = Intent(this, MainActivity::class.java)
            startActivity(mainIntent)
        } else {
            val loginIntent = Intent(this, LoginActivity::class.java)
            startActivity(loginIntent)
        }
        finish()
    }
}