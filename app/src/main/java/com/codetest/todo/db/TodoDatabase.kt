package com.codetest.todo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.codetest.todo.ui.create.TodoModel

@Database(
    entities = [TodoModel::class],
    version = 1
)
//@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun getTodoDao(): TodoDAO
}