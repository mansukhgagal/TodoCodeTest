package com.codetest.todo.ui.create

import com.codetest.todo.db.TodoDAO
import javax.inject.Inject

class TodoRepository @Inject constructor(val todoDao: TodoDAO) {
    suspend fun insertTodo(todo: TodoModel) :Int = todoDao.insertTodo(todo).toInt()
    suspend fun getAllTodoList(offset:Int) : List<TodoModel> =  todoDao.getAllTodoList(offset)
    suspend fun getTodoById(id:Int) : TodoModel =  todoDao.getTodoById(id)
    suspend fun updateTodo(todo:TodoModel) = todoDao.updateTodo(todo)
    suspend fun deleteTodo(todo:TodoModel) = todoDao.deleteTodo(todo)
}