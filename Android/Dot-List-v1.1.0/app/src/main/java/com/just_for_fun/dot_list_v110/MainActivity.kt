package com.just_for_fun.dot_list_v110

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    private val taskList = mutableListOf<Task>()
    private lateinit var adapter: TaskAdapter
    private lateinit var emptyTextView: TextView
    private lateinit var recyclerView: RecyclerView

    private val notesActivityResultLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val data = result.data
            val taskId = data?.getStringExtra("taskId")
            val newTitle = data?.getStringExtra("title")
            val newContent = data?.getStringExtra("content")

            taskList.find { it.id == taskId }?.let { task ->
                task.title = newTitle ?: task.title
                task.content = newContent ?: task.content
                adapter.submitList(taskList.toList())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        recyclerView = findViewById(R.id.recycler_view)
        emptyTextView = findViewById(R.id.emptyTextView)
        val addTaskButton = findViewById<FloatingActionButton>(R.id.add_task)

        // Setup RecyclerView
        adapter = TaskAdapter(
            onCheckboxChanged = { task, isChecked ->
                task.isChecked = isChecked
            },
            onTextChanged = { task, newText ->
                task.title = newText
            },
            onArrowClicked = { task ->
                openNotesActivity(task)
            }
        )

        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter

        // Update empty view visibility
        adapter.registerAdapterDataObserver(object : RecyclerView.AdapterDataObserver() {
            override fun onChanged() {
                super.onChanged()
                checkEmptyState()
            }
        })

        addTaskButton.setOnClickListener {
            addNewTask()
        }

        checkEmptyState()
    }

    private fun addNewTask() {
        val newTask = Task()
        taskList.add(newTask)
        adapter.submitList(taskList.toList())
        checkEmptyState()
    }

    private fun openNotesActivity(task: Task) {
        val intent = Intent(this, NotesActivity::class.java).apply {
            putExtra("taskId", task.id)
            putExtra("title", task.title)
            putExtra("content", task.content)
        }
        notesActivityResultLauncher.launch(intent)
    }

    private fun checkEmptyState() {
        emptyTextView.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
    }
}