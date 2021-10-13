package com.codetest.todo.ui.login

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class LoginActivityTest {
    @Test
    fun `empty email or password returns false`() {
        val result = UserData.validateLoginInput("", "")
        assertThat(result).isFalse()
    }

    @Test
    fun `invalid email pattern returns false`() {
        val result1 = UserData.isValidEmailPattern("mansukhgagalgmail.com")
        val result2 = UserData.isValidEmailPattern("mansukhgagal@gmail")
        val result3 = UserData.isValidEmailPattern("mansukhgagal@.com")
        val result4 = UserData.isValidEmailPattern("mansukhgagal")
        val finalResult = result1 || result2 || result3 || result4
        assertThat(finalResult).isFalse()
    }

    @Test
    fun `valid email pattern returns true`() {
        val result1 = UserData.isValidEmailPattern("mansukhgagal@gmail.com")
        val result2 = UserData.isValidEmailPattern("mansukhgagal@yahoo.com")
        val result3 = UserData.isValidEmailPattern("mansukhgagal@mansukhgagal.com")
        val finalResult = result1 || result2 || result3
        assertThat(finalResult).isTrue()
    }

    @Test
    fun `less then 6 digit password returns false`() {
        val result = UserData.isValidPasswordLength("mansu")
        assertThat(result).isFalse()
    }
}