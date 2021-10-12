package com.codetest.todo.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.codetest.todo.ui.create.TodoModel

@Dao
interface TodoDAO {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: TodoModel) : Long

    @Query("SELECT * FROM todo ORDER BY date DESC LIMIT 50 OFFSET :offset")
    suspend fun getAllTodoList(offset:Int=0) : List<TodoModel>
}










