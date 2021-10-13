package com.codetest.todo.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.codetest.todo.ui.create.TodoModel

@Database(
    entities = [TodoModel::class],
    version = 2
)
//@TypeConverters(Converters::class)
abstract class TodoDatabase : RoomDatabase() {
    abstract fun getTodoDao(): TodoDAO

    companion object {
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(database: SupportSQLiteDatabase) {
                //Adding status column, status can be active or inactive like 1=active,0=inactive.
                database.execSQL("ALTER TABLE todo ADD COLUMN status INTEGER NOT NULL DEFAULT 1")
            }
        }
    }
}