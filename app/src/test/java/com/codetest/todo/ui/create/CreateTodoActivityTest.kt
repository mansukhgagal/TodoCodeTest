package com.codetest.todo.ui.create

import com.codetest.todo.utils.Constants
import com.google.common.truth.Truth
import org.junit.Test

class CreateTodoActivityTest {

    @Test
    fun `invalid todo entry returns false`() {
        val case1 = TodoModel(null,"","","",null,1,0)
        val result = TodoModel.isValidInput(case1)
        Truth.assertThat(result).isFalse()
    }

    @Test
    fun `valid todo entry returns true`() {
        val todo = TodoModel(null,"Code test Mansukh","","11:11",null,Constants.TYPE_DAILY,0)
        val result = TodoModel.isValidInput(todo)
        Truth.assertThat(result).isTrue()
    }

}