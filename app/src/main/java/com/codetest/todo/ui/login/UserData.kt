package com.codetest.todo.ui.login

import androidx.core.util.PatternsCompat
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

        /**
         * the input is not valid if...
         * ...the email/password is empty
         */
        fun validateLoginInput(
            email: String,
            password: String
        ): Boolean {
            if(email.isEmpty() || password.isEmpty()) {
                return false
            }
            return true
        }

        fun isEmailEmpty(email:String) : Boolean = email.isEmpty()

        fun isPasswordEmpty(password:String) : Boolean = password.isEmpty()

        fun isValidEmailPattern(email: CharSequence): Boolean =
            PatternsCompat.EMAIL_ADDRESS.matcher(email).matches()

        fun isValidPasswordLength(password: String): Boolean {
            return password.length >= 6
        }
    }
}