package com.example.todo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainActivity : AppCompatActivity() {

    private lateinit var dbHelper: DatabaseHelper
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: TaskAdapter
    private val tasks = mutableListOf<Task>()
    private lateinit var noTasksText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        dbHelper = DatabaseHelper(this)
        recyclerView = findViewById(R.id.recycler_view)
        noTasksText = findViewById(R.id.no_tasks_text)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = TaskAdapter(tasks, { task, isCompleted ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    dbHelper.updateTaskStatus(task.id, if (isCompleted) 1 else 0)
                }
                loadTasks()
            }
        }, { task ->
            lifecycleScope.launch {
                withContext(Dispatchers.IO) {
                    dbHelper.deleteTask(task.id)
                }
                loadTasks()
            }
        })

        recyclerView.adapter = adapter

        val fab: FloatingActionButton = findViewById(R.id.fab)
        fab.setOnClickListener {
            showAddTaskDialog()
        }

        loadTasks()
    }

    private fun loadTasks() {
        lifecycleScope.launch {
            val taskList = withContext(Dispatchers.IO) {
                dbHelper.getAllTasks()
            }
            tasks.clear()
            tasks.addAll(taskList)
            adapter.notifyDataSetChanged()
            noTasksText.visibility = if (tasks.isEmpty()) View.VISIBLE else View.GONE
        }
    }

    private fun showAddTaskDialog() {
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_add_task, null)
        val editText: EditText = dialogView.findViewById(R.id.edit_task_description)
        AlertDialog.Builder(this)
            .setTitle("Add Task")
            .setView(dialogView)
            .setPositiveButton("Add") { _, _ ->
                val description = editText.text.toString()
                if (description.isNotEmpty()) {
                    lifecycleScope.launch {
                        withContext(Dispatchers.IO) {
                            dbHelper.insertTask(description)
                        }
                        loadTasks()
                    }
                } else {
                    Toast.makeText(this, "Task cannot be empty", Toast.LENGTH_SHORT).show()
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}