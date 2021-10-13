package com.codetest.todo.db

import androidx.room.*
import com.codetest.todo.ui.create.TodoModel

@Dao
interface TodoDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoModel) : Long

    @Update
    suspend fun updateTodo(todo:TodoModel)

    @Delete
    suspend fun deleteTodo(todo:TodoModel)

    @Query("SELECT * FROM todo ORDER BY date DESC LIMIT 50 OFFSET :offset")
    suspend fun getAllTodoList(offset:Int=0) : List<TodoModel>
}










