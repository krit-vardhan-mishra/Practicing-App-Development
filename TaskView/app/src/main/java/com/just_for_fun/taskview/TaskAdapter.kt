package com.just_for_fun.taskview

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.net.Uri.*
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleCoroutineScope
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class TaskAdapter(
    private val context: Context,
    private val taskDatabase: TaskDatabase,
    private val lifecycleScope: LifecycleCoroutineScope,
    private val onFilePickerLauncher: (taskId: Task) -> Unit
) : ListAdapter<Task, TaskAdapter.TaskViewHolder>(DiffCallBack()) {

    class DiffCallBack : DiffUtil.ItemCallback<Task>() {
        override fun areItemsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: Task, newItem: Task): Boolean {
            return oldItem == newItem
        }
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val checkBox: CheckBox = itemView.findViewById(R.id.taskCheckbox)
        private val notesArea: EditText = itemView.findViewById(R.id.notesArea)
        private val upArrow: ImageButton = itemView.findViewById(R.id.up_arrow)
        private val downArrow: ImageButton = itemView.findViewById(R.id.down_arrow)
        private val taskEditText: EditText = itemView.findViewById(R.id.taskEditText)
        private val uploadButton: ImageButton = itemView.findViewById(R.id.uploadButton)
        private val previewButton: ImageButton = itemView.findViewById(R.id.previewButton)
        private val notesLayout: View = itemView.findViewById(R.id.notesLayout)
        private val deleteButton: ImageButton = itemView.findViewById(R.id.deleteButton)

        private var currentTask: Task? = null
        private var taskUpdateJob: Job? = null

        fun bind(task: Task) {
            currentTask = task
            loadTaskDataIntoViews(task)
            setupListeners()
        }

        private fun loadTaskDataIntoViews(task: Task) {
            taskEditText.setText(task.title)
            notesArea.setText(task.notes)
            checkBox.isChecked = task.isChecked
            updateAttachmentButtonsVisibility(task.attachment)
            updateArrowVisibility(task.title.isNotEmpty())
            notesLayout.visibility = if (task.isExpanded) View.VISIBLE else View.GONE
        }

        private fun setupListeners() {
            setupTextWatchers()
            setupCheckboxListener()
            setupArrowListeners()
            setupAttachmentListeners()
        }

        private fun setupTextWatchers() {
            taskEditText.addTextChangedListener(createTitleTextWatcher())
            notesArea.addTextChangedListener(createNotesTextWatcher())
        }

        private fun createTitleTextWatcher() = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateArrowVisibility(s?.isNotEmpty() == true)
            }
            override fun afterTextChanged(s: Editable) {
                taskUpdateJob?.cancel()
                taskUpdateJob = lifecycleScope.launch {
                    delay(2000)
                    currentTask?.let {
                        val updatedTask = it.copy(title = s.toString())
                        taskDatabase.updateTask(updatedTask)
                    }
                }
            }
        }

        private fun createNotesTextWatcher() = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable) {
                taskUpdateJob?.cancel()
                taskUpdateJob = lifecycleScope.launch {
                    delay(2000)
                    currentTask?.let {
                        val updatedTask = it.copy(notes = s.toString())
                        taskDatabase.updateTask(updatedTask)
                    }
                }
            }
        }

        private fun setupCheckboxListener() {
            checkBox.setOnCheckedChangeListener { _, isChecked ->
                currentTask?.let {
                    val updatedTask = it.copy(isChecked = isChecked)
                    lifecycleScope.launch {
                        taskDatabase.updateTask(updatedTask)
                    }
                }
            }

            checkBox.setOnLongClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    showDeleteConfirmationDialog(position)
                }
                true
            }
        }

        private fun showDeleteConfirmationDialog(position: Int) {
            AlertDialog.Builder(itemView.context)
                .setTitle("Delete Task")
                .setMessage("Are you sure?")
                .setPositiveButton("Delete") { _, _ -> deleteTask(position) }
                .setNegativeButton("Cancel", null)
                .show()
        }


        private fun deleteTask(position: Int) {
            if (position == RecyclerView.NO_POSITION) return

            val taskToDelete = getItem(position)
            lifecycleScope.launch {
                taskDatabase.deleteTask(taskToDelete)
                val updatedList = currentList.toMutableList().apply {
                    removeAt(position)
                }
                submitList(updatedList)
            }
        }


        private fun setupArrowListeners() {
            downArrow.setOnClickListener {
                if (downArrow.isEnabled) {
                    notesLayout.visibility = View.VISIBLE
                    downArrow.visibility = View.GONE
                    upArrow.visibility = View.VISIBLE

                    currentTask?.let { task ->
                        val updatedTask = task.copy(isExpanded = true)
                        lifecycleScope.launch {
                            taskDatabase.updateTask(updatedTask)
                        }
                    }
                } else {
                    ToastUtil.showCustomToast(context, "Please enter a task title first.")
                }
            }

            upArrow.setOnClickListener {
                notesLayout.visibility = View.GONE
                upArrow.visibility = View.GONE
                downArrow.visibility = View.VISIBLE

                currentTask?.let { task ->
                    val updatedTask = task.copy(isExpanded = false)
                    lifecycleScope.launch {
                        taskDatabase.updateTask(updatedTask)
                    }
                }
            }
        }

        private fun setupAttachmentListeners() {
            uploadButton.setOnClickListener {
                currentTask?.let { task -> onFilePickerLauncher(task) }
            }


            previewButton.setOnClickListener {
                currentTask?.attachment?.let { uri ->
                    openFilePreview(uri)
                }
            }

            previewButton.setOnLongClickListener {
                deleteButton.visibility = View.VISIBLE
                true
            }

            deleteButton.setOnClickListener {
                currentTask?.let { task ->
                    val updatedTask = task.copy(attachment = null)
                    lifecycleScope.launch {
                        taskDatabase.updateTask(updatedTask)
                        withContext(Dispatchers.Main) {
                            updateAttachmentButtonsVisibility(null)
                        }
                    }
                }
            }
        }

        private fun updateAttachmentButtonsVisibility(attachment: String?) {
            uploadButton.visibility = if (attachment == null) View.VISIBLE else View.GONE
            previewButton.visibility = if (attachment != null) View.VISIBLE else View.GONE
            deleteButton.visibility = View.GONE
        }

        private fun updateArrowVisibility(isTaskTitleNotEmpty: Boolean) {
            downArrow.isEnabled = isTaskTitleNotEmpty
        }

        private fun openFilePreview(uriString: String) {
            val uri = parse(uriString)
            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(uri, context.contentResolver.getType(uri))
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }
            context.startActivity(intent)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_view, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}