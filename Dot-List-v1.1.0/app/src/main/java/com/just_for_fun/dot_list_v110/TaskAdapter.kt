package com.just_for_fun.dot_list_v110

import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AlertDialog
import androidx.compose.material3.Snackbar
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar

class TaskAdapter(
    private val onCheckboxChanged: (Task, Boolean) -> Unit,
    private val onTextChanged: (Task, String) -> Unit,
    private val onArrowClicked: (Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(TaskDiffCallback()) {

    private var deletedTask: Task? = null
    private var deletedTaskPosition: Int = -1

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val checkbox: CheckBox = itemView.findViewById(R.id.taskCheckbox)
        private val editText: EditText = itemView.findViewById(R.id.taskEditText)
        private val arrowButton: ImageButton = itemView.findViewById(R.id.forward_arrow)

        fun bind(
            task: Task,
            onCheckboxChanged: (Task, Boolean) -> Unit,
            onTextChanged: (Task, String) -> Unit,
            onArrowClicked: (Task) -> Unit
        ) {

            checkbox.isChecked = task.isChecked
            editText.setText(task.title)

            checkbox.setOnCheckedChangeListener { _, isChecked ->
                onCheckboxChanged(task, isChecked)
            }

            val maxLength = 30
            editText.addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(
                    s: CharSequence?, start: Int, count: Int, after: Int) { }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    val text = s.toString()
                    if (text.length > maxLength) {
                        editText.error = "Title must be less than 30 characters"
                    } else {
                        editText.error = null
                    }
                }

                override fun afterTextChanged(s: Editable?) {
                    val text = s.toString()
                    onTextChanged(task, text)
                }
            })

            arrowButton.setOnClickListener {
                if (editText.text.toString().isNotEmpty()) {
                    onArrowClicked(task)
                }
            }

            checkbox.setOnLongClickListener {
                showDeleteConfirmationDialog(adapterPosition, task)
                true
            }
        }

        private fun showDeleteConfirmationDialog(position: Int, task: Task) {
            AlertDialog.Builder(itemView.context)
                .setTitle("Delete Task")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete") { _, _ -> deleteTask(position, task) }
                .setNegativeButton("Cancel", null)
                .show()
        }

        private fun deleteTask(position: Int, task: Task) {
            deletedTask = task
            deletedTaskPosition = position

            val newList = currentList.toMutableList()
            newList.removeAt(position)
            submitList(newList)

            showUndoSnackbar()
        }

        private fun showUndoSnackbar() {
            val snackbar = Snackbar.make(itemView, "Task Deleted", Snackbar.LENGTH_LONG)
                .setAction("Undo") {
                    undoDelete()
                }

            snackbar.duration = 5000
            snackbar.show()
        }

        private fun undoDelete() {
            deletedTask?.let { task ->
                val newList = currentList.toMutableList()
                newList.add(deletedTaskPosition, task)
                submitList(newList)

                deletedTask = null
                deletedTaskPosition = -1
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.task_view, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position), onCheckboxChanged, onTextChanged, onArrowClicked)
    }
}