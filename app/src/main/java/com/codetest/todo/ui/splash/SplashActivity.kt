package com.codetest.todo.ui.splash

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import com.codetest.todo.app.BaseActivity
import com.codetest.todo.ui.login.LoginActivity
import com.codetest.todo.ui.main.MainActivity
import com.codetest.todo.utils.SecurePreference

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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