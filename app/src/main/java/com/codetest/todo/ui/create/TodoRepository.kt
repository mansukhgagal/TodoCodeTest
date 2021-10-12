package com.codetest.todo.ui.create

import com.codetest.todo.db.TodoDAO
import javax.inject.Inject

class TodoRepository @Inject constructor(val todoDao: TodoDAO) {

    suspend fun insertTodo(todo: TodoModel) :Int = todoDao.insertTodo(todo).toInt()
    suspend fun getAllTodoList(offset:Int) : List<TodoModel> {
        return todoDao.getAllTodoList(offset)
    }
}