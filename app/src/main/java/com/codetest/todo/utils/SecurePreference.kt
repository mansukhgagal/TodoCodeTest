package com.codetest.todo.utils

import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import com.codetest.todo.app.BaseApplication
import com.codetest.todo.ui.login.UserData
import com.google.gson.Gson

object SecurePreference {

    const val KEY_USER_DATA = "user_data"
    const val KEY_IS_LOGIN = "is_login"

    val securePref: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            "secure_app_pref",
            "todo_preference",
            BaseApplication.instance,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun setString(key: String, value: String) {
        with(securePref.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String, def: String?=null): String? {
        return securePref.getString(key, def)
    }

    fun setBoolean(key: String, value: Boolean) {
        with(securePref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    fun getBoolean(key: String, def: Boolean=false): Boolean {
        return securePref.getBoolean(key, def)
    }

    fun storeUserData(userData:UserData) {
        setBoolean(KEY_IS_LOGIN,true)
        val userJson = Gson().toJson(userData)
        setString(KEY_USER_DATA,userJson)
    }
    fun getUserData() : UserData? {
        val userJson = getString(KEY_USER_DATA) ?: return null
        val userData:UserData = Gson().fromJson(userJson,UserData::class.java)
        return userData
    }
    fun isLogin() : Boolean {
        return getBoolean(KEY_IS_LOGIN)
    }
}