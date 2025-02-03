package com.just_for_fun.taskview

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import android.Manifest
import android.provider.Settings
import android.webkit.MimeTypeMap
import androidx.appcompat.app.AlertDialog

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
    }

    private lateinit var checkBox: CheckBox
    private lateinit var notesArea: EditText
    private lateinit var upArrow: ImageButton
    private lateinit var downArrow: ImageButton
    private lateinit var taskEditText: EditText
    private lateinit var uploadButton: ImageButton
    private lateinit var previewButton: ImageButton
    private lateinit var notesLayout: ConstraintLayout
    private lateinit var deleteButton : ImageButton

    private lateinit var taskDatabase: TaskDatabase
    private var task: Task = Task()
    private var currentTask: Task? = null
    private var taskId: Long = -1L
    private lateinit var filePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_input)

        taskDatabase = TaskDatabase(this)

        taskEditText = findViewById(R.id.taskEditText)
        notesArea = findViewById(R.id.notesArea)
        checkBox = findViewById(R.id.taskCheckbox)
        previewButton = findViewById(R.id.previewButton)
        uploadButton = findViewById(R.id.uploadButton)
        deleteButton = findViewById(R.id.deleteButton)
        notesLayout = findViewById(R.id.notesLayout)
        upArrow = findViewById(R.id.up_arrow)
        downArrow = findViewById(R.id.down_arrow)

        notesLayout.visibility = GONE
        upArrow.visibility = GONE
        downArrow.isEnabled = false
        deleteButton.visibility = GONE

        checkAndRequestPermissions()

        taskEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                downArrow.isEnabled = s?.isNotEmpty() == true
            }

            override fun afterTextChanged(s: Editable?) {
                val enteredText = s.toString()
                var taskTitles : List<String> = taskDatabase.getAllTaskTitles()

                val taskExists = taskTitles.contains(enteredText)

                if (!taskExists && enteredText.isNotEmpty()) {
                    currentTask = Task()
                    currentTask!!.setTitle(enteredText)
                    taskId = taskDatabase.insertTask(currentTask!!)

                    if (taskId != -1L) {
                        currentTask!!.setId(taskId.toInt())
                        taskTitles = taskDatabase.getAllTaskTitles()
                    } else {
                        Toast.makeText(this@MainActivity, "Error creating task", Toast.LENGTH_SHORT).show()
                    }
                } else if (taskExists && enteredText.isNotEmpty()) {
                    val existingTask =
                        taskDatabase.getAllTask().find { it.getTitle() == enteredText }
                    if (existingTask != null) {
                        currentTask = existingTask
                        taskId = currentTask!!.getId().toLong()
                        notesArea.setText(currentTask!!.getNotes())
                        checkBox.isChecked = currentTask!!.isChecked()
                        if (currentTask!!.getAttachment() != null) {
                            try {
                                previewButton.visibility = VISIBLE
                                val uri = Uri.parse(currentTask!!.getAttachment())
                                previewButton.setImageURI(uri)
                                uploadButton.visibility = GONE
                            } catch (e: Exception) {
                                Log.e("MainActivity", "Error loading image: ${e.message}")
                                Toast.makeText(this@MainActivity, "Error loading image", Toast.LENGTH_SHORT)
                                    .show()
                                previewButton.visibility = GONE
                                currentTask!!.setAttachment(null)
                                taskDatabase.updateTask(currentTask!!)
                            }
                        }
                    }
                } else {
                    currentTask = null
                    taskId = -1L
                    notesArea.text.clear()
                    checkBox.isChecked = false
                    previewButton.visibility = GONE
                    previewButton.setImageURI(null)
                    uploadButton.visibility = VISIBLE
                }
            }
        })

        downArrow.setOnClickListener {
            try {
                val taskText = taskEditText.text.toString()
                if (taskText.isNotEmpty()) {
                    notesLayout.visibility = VISIBLE
                    downArrow.visibility = GONE
                    upArrow.visibility = VISIBLE
                } else {
                    throw IllegalArgumentException("Task text cannot be empty")
                }
            } catch (e : Exception) {
                Toast.makeText(this, e.message, Toast.LENGTH_SHORT).show()
            }
        }

        upArrow.setOnClickListener {
            notesLayout.visibility = GONE
            downArrow.visibility = VISIBLE
            upArrow.visibility = GONE
        }

        taskEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val taskTitle = taskEditText.text.toString()
                if (taskTitle.isNotEmpty()) {
                    task.setTitle(taskTitle)
                    task.setNotes(notesArea.text.toString())
                    task.setChecked(checkBox.isChecked)
                    taskId = taskDatabase.insertTask(task)
                    task.setId(taskId.toInt())
                    taskEditText.text.clear()
                    notesArea.text.clear()
                    checkBox.isChecked = false
                    previewButton.visibility = GONE
                    previewButton.setImageURI(null)
                }
            }
        }

        notesArea.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus && taskId != -1L) { // Only update if a task exists
                task.setNotes(notesArea.text.toString())
                taskDatabase.updateTask(task)
            }
        }

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            if (taskId != -1L) {
                task.setChecked(isChecked)
                taskDatabase.updateTask(task)
            }
        }

        taskEditText.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                saveCurrentTask()
            }
        }

        notesArea.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                saveCurrentTask()
            }
        }

        checkBox.setOnCheckedChangeListener { _, _ ->
            saveCurrentTask()
        }

        uploadButton.setOnClickListener {
            openFilePicker()
        }

        previewButton.setOnLongClickListener{
            deleteButton.visibility = VISIBLE
            return@setOnLongClickListener true
        }

        previewButton.setOnClickListener {
            openFilePreview()
        }

        deleteButton.setOnClickListener {
            currentTask?.setAttachment(null)
            currentTask?.let { taskDatabase.updateTask(it) }
            previewButton.visibility = GONE
            uploadButton.visibility = VISIBLE
            deleteButton.visibility = GONE
            previewButton.setImageURI(null)
        }

        filePickerLauncher =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
                if (result.resultCode == Activity.RESULT_OK) {
                    val uri: Uri? = result.data?.data
                    uri?.let {
                        previewButton.visibility = VISIBLE
                        previewButton.setImageURI(uri)
                        currentTask?.setAttachment(uri.toString())
                        currentTask?.let { taskDatabase.updateTask(it) }
                    }
                }
            }

        loadTaskData()
    }

    private fun saveCurrentTask() {
        val taskTitle =  taskEditText.text.toString()
        if (taskTitle.isNotEmpty()) {
            if (currentTask == null) {
                task = Task()
                task.setTitle(taskTitle)
                task.setNotes( notesArea.text.toString())
                task.setChecked( checkBox.isChecked)
                taskId = taskDatabase.insertTask(task)
                if (taskId == -1L) {
                    Toast.makeText(this, "Error inserting task", Toast.LENGTH_SHORT).show()
                    return
                }
                task.setId(taskId.toInt())
                currentTask = task
            } else { // Update existing
                currentTask?.setTitle(taskTitle) // Update title if changed
                currentTask?.setNotes( notesArea.text.toString())
                currentTask?.setChecked(checkBox.isChecked)
                currentTask?.let { taskDatabase.updateTask(it) }
            }
        } else if (currentTask != null) { // Handle title cleared
            currentTask?.setNotes( notesArea.text.toString())
            currentTask?.setChecked( checkBox.isChecked)
            currentTask?.let { taskDatabase.updateTask(it) }
            if ( notesArea.text.toString().isEmpty() && ! checkBox.isChecked) { // If notes and checkbox are also cleared, delete the task
                taskDatabase.deleteTask(currentTask!!)
                currentTask = null
                taskId = -1L
                 previewButton.visibility = GONE
                 previewButton.setImageURI(null)
            }
        }
    }

    private fun loadTaskData() {
        val tasks = taskDatabase.getAllTask()
        if (tasks.isNotEmpty()) {
            val lastTask = tasks.last()
            currentTask = lastTask
            taskId = lastTask.getId().toLong()
             taskEditText.setText(lastTask.getTitle())
             notesArea.setText(lastTask.getNotes())
             checkBox.isChecked = lastTask.isChecked()

            try {
                if (lastTask.getAttachment() != null) {
                     previewButton.visibility = VISIBLE
                    // Safely try to set the image
                    val uri = Uri.parse(lastTask.getAttachment())
                     previewButton.setImageURI(uri)
                } else {
                     previewButton.visibility = GONE
                     previewButton.setImageURI(null)
                }
            } catch (e: SecurityException) {
                // Handle permission error gracefully
                 previewButton.visibility = GONE
                Toast.makeText(this, "Permission denied loading attachment", Toast.LENGTH_SHORT).show()
                Log.e("MainActivity", "Permission denied loading attachment: ${e.message}")
            }
        }
    }

    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            if (checkSelfPermission(android.Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(
                    android.Manifest.permission.READ_MEDIA_IMAGES,
                    android.Manifest.permission.READ_MEDIA_VIDEO,
                    android.Manifest.permission.READ_MEDIA_AUDIO
                ), PERMISSION_REQUEST_CODE)
            }
        } else {
            // For Android 12 and below
            if (checkSelfPermission(android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(
                    android.Manifest.permission.READ_EXTERNAL_STORAGE
                ), PERMISSION_REQUEST_CODE)
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_GET_CONTENT)
        intent.type = "*/*"
        filePickerLauncher.launch(intent)
        uploadButton.visibility = GONE
        previewButton.visibility = VISIBLE
    }

    private fun openFilePreview() {
        val attachment = currentTask?.getAttachment()
        if (attachment == null) {
            Toast.makeText(this, "No file attached", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val fileUri = Uri.parse(attachment)
            if (fileUri == null) {
                Toast.makeText(this, "Invalid file URI", Toast.LENGTH_SHORT).show();
                return;
            }

            val mimeType = contentResolver.getType(fileUri) ?: run {
                val fileExtension = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString())
                MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension)
            }

            if (mimeType == null) {
                Toast.makeText(this, "Cannot determine file type", Toast.LENGTH_SHORT).show()
                return
            }

            val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
                setDataAndType(fileUri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
                addCategory(Intent.CATEGORY_OPENABLE)
            };

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent);
            } else {
                Toast.makeText(this, "No app found to open this file type", Toast.LENGTH_SHORT).show();
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Permission denied to access file", Toast.LENGTH_SHORT).show()
        } catch (e : Exception) {
            Toast.makeText(this, "Cannot open file: " + e.message, Toast.LENGTH_SHORT).show();
        }
    }

    override fun onPause() {
        super.onPause()
        saveCurrentTask()
    }

    override fun onResume() {
        super.onResume()
        loadTaskData()
    }

    override fun onDestroy() {
        super.onDestroy()
        taskDatabase.updateTask(task)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                val deniedPermissions = mutableListOf<String>()

                // Check which permissions were granted/denied
                for (i in permissions.indices) {
                    if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                        deniedPermissions.add(permissions[i])
                    }
                }

                if (deniedPermissions.isEmpty()) {
                    // All permissions granted
                    loadTaskData()
                } else {
                    // Some permissions were denied
                    var message = "Required permissions not granted: "

                    // Create user-friendly permission names
                    val friendlyNames = deniedPermissions.map { permission ->
                        when (permission) {
                            Manifest.permission.READ_EXTERNAL_STORAGE -> "Storage"
                            Manifest.permission.READ_MEDIA_IMAGES -> "Photos"
                            Manifest.permission.READ_MEDIA_VIDEO -> "Videos"
                            Manifest.permission.READ_MEDIA_AUDIO -> "Audio"
                            else -> permission
                        }
                    }

                    message += friendlyNames.joinToString(", ")

                    // Check if user clicked "Don't ask again" for any permission
                    val shouldShowRationale = deniedPermissions.any { permission ->
                        shouldShowRequestPermissionRationale(permission)
                    }

                    if (shouldShowRationale) {
                        AlertDialog.Builder(this)
                            .setTitle("Permissions Required")
                            .setMessage("These permissions are needed to properly save and display your task attachments. Would you like to grant them now?")
                            .setPositiveButton("Yes") { _, _ ->
                                // Request permissions again
                                checkAndRequestPermissions()
                            }
                            .setNegativeButton("No") { dialog, _ ->
                                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    } else {
                        // User denied permission AND checked "Don't ask again"
                        AlertDialog.Builder(this)
                            .setTitle("Permissions Required")
                            .setMessage("Required permissions have been permanently denied. Please enable them in Settings to use all app features.")
                            .setPositiveButton("Open Settings") { _, _ ->
                                // Open app settings
                                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                val uri = Uri.fromParts("package", packageName, null)
                                intent.data = uri
                                startActivity(intent)
                            }
                            .setNegativeButton("Cancel") { dialog, _ ->
                                Toast.makeText(this, message, Toast.LENGTH_LONG).show()
                                dialog.dismiss()
                            }
                            .create()
                            .show()
                    }
                }
            }
        }
    }
}