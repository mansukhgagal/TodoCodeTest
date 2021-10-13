package com.codetest.todo.utils

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.codetest.todo.app.BaseApplication
import com.codetest.todo.ui.login.UserData
import com.google.gson.Gson


object SecurePreference {

    private const val KEY_USER_DATA = "user_data"
    private const val KEY_IS_LOGIN = "is_login"

    @RequiresApi(Build.VERSION_CODES.M)
    private fun getMasterKey(): MasterKey {
        val masterKey =
            MasterKey.Builder(BaseApplication.instance, MasterKey.DEFAULT_MASTER_KEY_ALIAS)
                .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                .build()
        return masterKey
    }

    private val securePref: SharedPreferences by lazy {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            EncryptedSharedPreferences.create(
                BaseApplication.instance,
                "secure_app_pref",
                getMasterKey(),
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        } else {
            EncryptedSharedPreferences.create(
                "secure_app_pref",
                "todoAlias",
                BaseApplication.instance,
                EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
            )
        }
    }

    fun setString(key: String, value: String) {
        with(securePref.edit()) {
            putString(key, value)
            apply()
        }
    }

    fun getString(key: String, def: String? = null): String? {
        return securePref.getString(key, def)
    }

    fun setBoolean(key: String, value: Boolean) {
        with(securePref.edit()) {
            putBoolean(key, value)
            apply()
        }
    }

    fun getBoolean(key: String, def: Boolean = false): Boolean {
        return securePref.getBoolean(key, def)
    }

    fun storeUserData(userData: UserData) {
        setBoolean(KEY_IS_LOGIN, true)
        val userJson = Gson().toJson(userData)
        setString(KEY_USER_DATA, userJson)
    }

    fun getUserData(): UserData? {
        val userJson = getString(KEY_USER_DATA) ?: return null
        return Gson().fromJson(userJson, UserData::class.java)
    }

    fun isLogin(): Boolean {
        return getBoolean(KEY_IS_LOGIN)
    }
}