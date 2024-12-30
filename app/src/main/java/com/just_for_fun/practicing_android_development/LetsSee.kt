package com.just_for_fun.practicing_android_development

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet.Constraint

class LetsSee : AppCompatActivity() {

    private lateinit var checkBox: CheckBox
    private lateinit var notesArea : EditText
    private lateinit var upArrow : ImageButton
    private lateinit var downArrow : ImageButton
    private lateinit var taskEditText : EditText
    private lateinit var uploadButton : ImageButton
    private lateinit var previewButton : ImageButton
    private lateinit var notesLayout : ConstraintLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.task_input)

        upArrow = findViewById(R.id.up_arrow)
        notesArea = findViewById(R.id.notesArea)
        downArrow = findViewById(R.id.down_arrow)
        checkBox = findViewById(R.id.taskCheckbox)
        notesLayout = findViewById(R.id.notesLayout)
        taskEditText = findViewById(R.id.taskEditText)
        uploadButton = findViewById(R.id.uploadButton)
        previewButton = findViewById(R.id.previewButton)

        // Initially hide the notes layout
        notesLayout.visibility = GONE
        upArrow.visibility = GONE

        // Show notes layout when the down arrow button is clicked
        downArrow.setOnClickListener {
            notesLayout.visibility = VISIBLE
            downArrow.visibility = GONE
            upArrow.visibility = VISIBLE

            val layoutParams = taskEditText.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.endToStart = upArrow.id
            layoutParams.startToEnd = checkBox.id
            taskEditText.layoutParams = layoutParams
        }

        upArrow.setOnClickListener {
            notesLayout.visibility = GONE
            downArrow.visibility = VISIBLE
            upArrow.visibility = GONE

            val layoutParams = taskEditText.layoutParams as ConstraintLayout.LayoutParams
            layoutParams.endToStart = downArrow.id
            layoutParams.startToEnd = checkBox.id
            taskEditText.layoutParams = layoutParams
        }

        // Handle file upload
        uploadButton.setOnClickListener {
            openFilePicker()
        }

    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }

        filePickerLauncher.launch(intent)
    }

    private val filePickerLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {

            val uri: Uri? = result.data?.data
            uri?.let {
                previewButton.visibility = VISIBLE
                previewButton.setImageURI(uri)
            }
        }
    }
}