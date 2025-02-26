package com.example.todo

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class TaskAdapter(
    private val tasks: List<Task>,
    private val onStatusChanged: (Task, Boolean) -> Unit,
    private val onDeleteClicked: (Task) -> Unit
) : RecyclerView.Adapter<TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_item, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.textView.text = task.description
        holder.checkBox.isChecked = task.isCompleted
        holder.checkBox.setOnCheckedChangeListener { _, isChecked ->
            onStatusChanged(task, isChecked)
        }
        holder.deleteButton.setOnClickListener {
            onDeleteClicked(task)
        }
    }

    override fun getItemCount() = tasks.size
}

class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textView: TextView = itemView.findViewById(R.id.task_description)
    val checkBox: CheckBox = itemView.findViewById(R.id.task_checkbox)
    val deleteButton: ImageButton = itemView.findViewById(R.id.delete_button)
}