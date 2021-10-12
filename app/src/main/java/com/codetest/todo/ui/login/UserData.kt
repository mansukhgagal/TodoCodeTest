package com.codetest.todo.ui.login

import android.util.Patterns
import com.google.gson.annotations.SerializedName

data class UserData(
    @SerializedName("email")
    var email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("token")
    val token: String? = null
) {
    companion object {
        fun isValidEmailPattern(email: CharSequence): Boolean =
            Patterns.EMAIL_ADDRESS.matcher(email).matches()

        fun isValidPassword(password: String): Boolean {
            return password.length > 6
        }
    }
}