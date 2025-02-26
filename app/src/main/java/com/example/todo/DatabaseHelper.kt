package com.example.todo

import android.content.ContentValues
import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class DatabaseHelper(context: Context) : SQLiteOpenHelper(context, "todo.db", null, 1) {

    override fun onCreate(db: SQLiteDatabase) {
        db.execSQL("CREATE TABLE tasks (id INTEGER PRIMARY KEY AUTOINCREMENT, description TEXT, status INTEGER)")
    }

    override fun onUpgrade(db: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Handle database upgrades if needed (e.g., drop and recreate table)
    }

    fun insertTask(description: String) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("description", description)
            put("status", 0) // 0 = not completed
        }
        db.insert("tasks", null, values)
        db.close()
    }

    fun updateTaskStatus(id: Int, status: Int) {
        val db = writableDatabase
        val values = ContentValues().apply {
            put("status", status) // 0 or 1
        }
        db.update("tasks", values, "id = ?", arrayOf(id.toString()))
        db.close()
    }

    fun deleteTask(id: Int) {
        val db = writableDatabase
        db.delete("tasks", "id = ?", arrayOf(id.toString()))
        db.close()
    }

    fun getAllTasks(): List<Task> {
        val db = readableDatabase
        val cursor = db.query("tasks", null, null, null, null, null, null)
        val tasks = mutableListOf<Task>()
        while (cursor.moveToNext()) {
            val id = cursor.getInt(cursor.getColumnIndexOrThrow("id"))
            val description = cursor.getString(cursor.getColumnIndexOrThrow("description"))
            val status = cursor.getInt(cursor.getColumnIndexOrThrow("status"))
            tasks.add(Task(id, description, status == 1))
        }
        cursor.close()
        db.close()
        return tasks
    }
}