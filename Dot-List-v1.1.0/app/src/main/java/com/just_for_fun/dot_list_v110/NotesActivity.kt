package com.just_for_fun.dot_list_v110

import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity


class NotesActivity : AppCompatActivity() {
    private lateinit var titleEditText: EditText
    private lateinit var notesEditText: EditText
    private lateinit var backButton : ImageButton
    private var currentTaskId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        titleEditText = findViewById(R.id.notes_title)
        notesEditText = findViewById(R.id.notes_area)
        backButton = findViewById(R.id.back_arrow)

        currentTaskId = intent.getStringExtra("taskId")
        titleEditText.setText(intent.getStringExtra("title"))
        notesEditText.setText(intent.getStringExtra("content"))

        backButton.setOnClickListener {
            saveAndExit()
        }
    }

    @Deprecated("This method has been deprecated in favor of using the\n      {@link OnBackPressedDispatcher} via {@link #getOnBackPressedDispatcher()}.\n      The OnBackPressedDispatcher controls how back button events are dispatched\n      to one or more {@link OnBackPressedCallback} objects.")
    override fun onBackPressed() {
        saveAndExit()
        super.onBackPressed()
    }

    private fun saveAndExit() {
        val resultIntent = Intent().apply {
            putExtra("taskId", currentTaskId)
            putExtra("title", titleEditText.text.toString())
            putExtra("content", notesEditText.text.toString())
        }

        setResult(RESULT_OK, resultIntent)
        finish()
    }
}