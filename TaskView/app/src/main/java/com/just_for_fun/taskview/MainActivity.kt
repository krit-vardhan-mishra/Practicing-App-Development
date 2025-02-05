package com.just_for_fun.taskview

import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageButton
import android.widget.Toast
import kotlinx.coroutines.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.content.ClipData
import android.provider.Settings
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.lifecycleScope

class MainActivity : AppCompatActivity() {

    companion object {
        private const val PERMISSION_REQUEST_CODE = 123
        private const val MAX_TASK_TITLE_LENGTH = 30
    }

    private lateinit var checkBox: CheckBox
    private lateinit var notesArea: EditText
    private lateinit var upArrow: ImageButton
    private lateinit var downArrow: ImageButton
    private lateinit var taskEditText: EditText
    private lateinit var uploadButton: ImageButton
    private lateinit var previewButton: ImageButton
    private lateinit var notesLayout: ConstraintLayout
    private lateinit var deleteButton: ImageButton

    private var currentTask: Task? = null
    private lateinit var taskDatabase: TaskDatabase

    private var taskUpdateJob: Job? = null
    private lateinit var filePickerLauncher: ActivityResultLauncher<Array<String>>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.task_input)

        taskDatabase = TaskDatabase(this)

        initializeViews()
        setupListeners()
        checkAndRequestPermissions()
        loadDatabase()

        checkBox.setOnCheckedChangeListener { _, isChecked ->
            currentTask?.let { task ->
                val updatedTask = task.copy(isChecked = isChecked)
                lifecycleScope.launch {
                    try {
                        taskDatabase.updateTask(updatedTask)
                        currentTask = updatedTask
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.d("Task Checkbox", "Error updating task: ${e.message}")
                            ToastUtil.showCustomToast(this@MainActivity, "Error updating task: ${e.message}")
                        }
                    }
                }
            }
        }

        setupFilePicker()
    }


    private fun setupFilePicker() {
        filePickerLauncher = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri: Uri? ->
            uri?.let { uri ->
                lifecycleScope.launch {
                    try {
                        contentResolver.takePersistableUriPermission(
                            uri, Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                        )

                        Log.d("File Picker", "URI: $uri")
                        ToastUtil.showCustomToast(this@MainActivity, "URI: $uri")
                        Log.d("File Picker", "Persisted Permission: ${contentResolver.persistedUriPermissions}")
                        Log.d("Task File Picker", "File selected: $uri")
                        
                        currentTask?.let { task ->
                            val updatedTask = task.copy(attachment = uri.toString())
                            taskDatabase.updateTask(updatedTask)
                            currentTask = updatedTask

                            withContext(Dispatchers.Main) {
                                updateAttachmentButtonsVisibility(uri.toString())
                                openDialogBox()
                            }
                        }
                    } catch (e: SecurityException) {
                        withContext(Dispatchers.Main) {
                            Log.e("Task File Picker", "Permission error: ${e.message}")
                            ToastUtil.showCustomToast(this@MainActivity, "Permission error: ${e.message}")
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.e("Task File Picker", "Error uploading file: ${e.message}")
                            ToastUtil.showCustomToast(this@MainActivity, "Error uploading file: ${e.message}")
                        }
                    }
                }
            } ?: ToastUtil.showCustomToast(this@MainActivity, "No file selected.")
        }
    }

    private fun loadDatabase() {
        lifecycleScope.launch {
            try {
                taskDatabase.getAllTask().collect { tasks ->
                    lifecycleScope.launch {
                        if (tasks.isEmpty()) {
                            createInitialTask()
                        } else {
                            currentTask = tasks[0]
                            currentTask?.let {
                                loadTaskDataIntoViews(currentTask!!)
                                updateAttachmentButtonsVisibility(currentTask?.attachment)
                            }
                        }
                        Log.d("Task Loading", "Tasks retrieved: $tasks")
                        // ToastUtil.showCustomToast(this@MainActivity, "Task Retrieved: $tasks")
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Log.d("Task Loading Database", "Error loading database: ${e.message}")
                    ToastUtil.showCustomToast(this@MainActivity, "Error loading database: ${e.message}")
                }
            }
        }
    }

    private suspend fun createInitialTask() {
        try {
            val newTask = Task(title = "", notes = "", isChecked = false, attachment = null)
            val insertedRowId = taskDatabase.insertTask(newTask)

            if (insertedRowId != -1L) {
                taskDatabase.getTaskById(insertedRowId).collect { task ->
                    task?.let {
                        currentTask = it
                        withContext(Dispatchers.Main) {
                            loadTaskDataIntoViews(it)
                            updateAttachmentButtonsVisibility(it.attachment)
                        }
                    }
                }
            } else {
                withContext(Dispatchers.Main) {
                    Log.d("Task Inserting", "Error inserting initial task.")
                    ToastUtil.showCustomToast(this@MainActivity, "Error inserting initial task.")
                }
            }
        } catch (e: Exception) {
            withContext(Dispatchers.Main) {
                Log.d("Task Creating", "Error creating initial task: ${e.message}")
                ToastUtil.showCustomToast(this@MainActivity, "Error creating initial task: ${e.message}")
            }
        }
    }

    private fun loadTaskDataIntoViews(task: Task) {
        taskEditText.setText(task.title)
        notesArea.setText(task.notes)
        checkBox.isChecked = task.isChecked
    }

    private fun initializeViews() {
        taskEditText = findViewById(R.id.taskEditText)
        notesArea = findViewById(R.id.notesArea)
        checkBox = findViewById(R.id.taskCheckbox)
        previewButton = findViewById(R.id.previewButton)
        uploadButton = findViewById(R.id.uploadButton)
        deleteButton = findViewById(R.id.deleteButton)
        notesLayout = findViewById(R.id.notesLayout)
        upArrow = findViewById(R.id.up_arrow)
        downArrow = findViewById(R.id.down_arrow)

        // Initial view setup
        notesLayout.visibility = GONE
        upArrow.visibility = GONE
        downArrow.isEnabled = false
        deleteButton.visibility = GONE
    }

    private fun setupListeners() {
        setupTaskEditTextListener()
        setupNotesAreaListener()
        setupNavigationArrowListeners()
        setupAttachmentListeners()
    }

    private fun setupTaskEditTextListener() {
        taskEditText.addTextChangedListener(object : TextWatcher {
            private var previousText: String? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                previousText = s?.toString()

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateDownArrowState(s)
                validateTaskTitle(s)
            }

            override fun afterTextChanged(s: Editable?) {
                val enteredText = s.toString().trim()

                taskUpdateJob?.cancel()

                taskUpdateJob = lifecycleScope.launch {
                    delay(2000)

                    try {
                        currentTask?.let { task ->
                            val updatedTask = task.copy(title = enteredText)
                            taskDatabase.updateTask(updatedTask)
                            currentTask = updatedTask
                            Log.d("Task Updating", "Task updated (EditText): $enteredText") // Log update
                        } ?: run {
                            if (currentTask == null) {
                                val newTask = Task(title = enteredText, notes = "", isChecked = false, attachment = null)
                                val insertedRowId = taskDatabase.insertTask(newTask)

                                if (insertedRowId != -1L) {
                                    taskDatabase.getTaskById(insertedRowId).collect { task ->
                                        task?.let {
                                            withContext(Dispatchers.Main) {
                                                currentTask = it
                                                loadTaskDataIntoViews(it)
                                            }
                                        }
                                    }
                                }
                            } else {
                                val updatedTask = currentTask!!.copy(title = enteredText)
                                taskDatabase.updateTask(updatedTask)
                                currentTask = updatedTask
                            }
                        }

                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.e("Task Updating", "Error updating task (EditText): ${e.message}")
                            ToastUtil.showCustomToast(this@MainActivity, "Error updating task: ${e.message}")
                        }
                    } finally {
                        taskUpdateJob = null // Clear the job after execution (important!)
                    }
                }
            }
        })
    }

    private fun setupNotesAreaListener() {
        notesArea.addTextChangedListener(object : TextWatcher {
            private var previousNotes: String? = null

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
                previousNotes = s?.toString()
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

            override fun afterTextChanged(s: Editable?) {
                val enteredNotes = s.toString().trim()

                taskUpdateJob?.cancel() // Cancel any pending updates

                taskUpdateJob = lifecycleScope.launch {
                    delay(2000) // Debounce

                    try {
                        currentTask?.let { task ->
                            val updatedTask = task.copy(notes = enteredNotes)
                            taskDatabase.updateTask(updatedTask)
                            currentTask = updatedTask
                            Log.d("Task Updating", "Notes updated: $enteredNotes") // Log update
                        }
                    } catch (e: Exception) {
                        withContext(Dispatchers.Main) {
                            Log.e("Task Updating", "Error updating notes: ${e.message}")
                            ToastUtil.showCustomToast(this@MainActivity, "Error updating notes: ${e.message}")
                        }
                    } finally {
                        taskUpdateJob = null // Clear the job
                    }
                }
            }
        })
    }

    private fun updateAttachmentButtonsVisibility(attachment: String?) {
        uploadButton.visibility = if (attachment == null) VISIBLE else GONE
        previewButton.visibility = if (attachment != null) VISIBLE else GONE
    }

    private fun updateDownArrowState(s: CharSequence?) {
        downArrow.isEnabled = s?.isNotEmpty() == true
    }

    private fun validateTaskTitle(s: CharSequence?) {
        s?.let {
            if (it.length > MAX_TASK_TITLE_LENGTH) {
                taskEditText.error = "Task title too long (max $MAX_TASK_TITLE_LENGTH characters)"
                taskEditText.setText(it.subSequence(0, MAX_TASK_TITLE_LENGTH))
                taskEditText.setSelection(MAX_TASK_TITLE_LENGTH)
            }
        }
    }

    private fun setupNavigationArrowListeners() {
        downArrow.setOnClickListener {
            if (taskEditText.text.toString().trim().isNotEmpty()) {
                notesLayout.visibility = VISIBLE
                downArrow.visibility = GONE
                upArrow.visibility = VISIBLE
            } else {
                ToastUtil.showCustomToast(this@MainActivity, "Please enter a task title first.")
            }
        }

        upArrow.setOnClickListener {
            notesLayout.visibility = GONE
            upArrow.visibility = GONE
            downArrow.visibility = VISIBLE
        }
    }

    private fun setupAttachmentListeners() {
        uploadButton.setOnClickListener { openFilePicker() }

        previewButton.setOnLongClickListener {
            deleteButton.visibility = VISIBLE
            true
        }

        previewButton.setOnClickListener { openFilePreview() }

        deleteButton.setOnClickListener {
            lifecycleScope.launch {
                try {
                    currentTask?.let { task ->
                        val updatedTask = task.copy(attachment = null)
                        taskDatabase.updateTask(updatedTask)
                        currentTask = updatedTask

                        withContext(Dispatchers.Main) {
                            deleteButton.visibility = GONE
                            previewButton.visibility = GONE
                            uploadButton.visibility = VISIBLE
                        }
                        ToastUtil.showCustomToast(this@MainActivity, "File deleted.")
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Log.d("Task Updating", "Error updating task(Delete Button): ${e.message}")
                        ToastUtil.showCustomToast(this@MainActivity, "Error updating task: ${e.message}")
                    }
                }
            }
        }
    }

    private fun openFilePicker() {
        try {
            filePickerLauncher.launch(arrayOf("*/*"))
        } catch (e: Exception) {
            Log.e("File Picker", "Error opening file picker: ${e.message}")
            ToastUtil.showCustomToast(this, "Error opening file picker: ${e.message}")
        }
    }
    private fun openDialogBox() {
        val alterDialogBox = AlertDialog.Builder(this)
        alterDialogBox.setTitle("Uploaded File")
        alterDialogBox.setMessage("The file path is stored as a reference. If you delete the file from storage after uploading, the preview button will throw an error.")
        alterDialogBox.setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
        alterDialogBox.show()
    }

    private fun openFilePreview() {
        val attachmentString = currentTask?.attachment

        Log.d("File Preview", "Current Task Attachment: $attachmentString")
        ToastUtil.showCustomToast(this@MainActivity, "Current Task Attachment: $attachmentString")

        if (attachmentString == null) {
            Log.d("Task File Preview", "No File Attached.")
            return
        }

        try {
            val fileUri = Uri.parse(attachmentString)
            contentResolver.takePersistableUriPermission(
                fileUri,
                Intent.FLAG_GRANT_READ_URI_PERMISSION
            )
            Log.d("File Preview", "File URI: $fileUri")
            Log.d("File Preview", "All Persisted Permissions: ${contentResolver.persistedUriPermissions}")

            val hasPermission = contentResolver.persistedUriPermissions.any {
                it.uri == fileUri && it.isReadPermission
            }

            Log.d("File Preview", "Has Permission: $hasPermission")

            if (!hasPermission) {
                throw SecurityException("Permission not found for URI")
            }

            val mimeType = contentResolver.getType(fileUri) ?: getMimeTypeFromExtension(fileUri)
            Log.d("File Preview", "Mime Type: $mimeType")

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                clipData = ClipData.newRawUri("", fileUri)
                addCategory(Intent.CATEGORY_DEFAULT)
            }

            val chooserIntent = Intent.createChooser(intent, "Open file using")
            if (chooserIntent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Log.d("File Preview", "No app found to open this file")
                Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show()
            }
        }
        catch (e: SecurityException) {
            Log.e("File Preview", "Security Exception: ${e.message}")
            lifecycleScope.launch {
                currentTask?.let { task ->
                    val updatedTask = task.copy(attachment = null)
                    taskDatabase.updateTask(updatedTask)
                    currentTask = updatedTask

                    withContext(Dispatchers.Main) {
                        Log.d("File Permission", "Permission lost, clearing attachment")
                        ToastUtil.showCustomToast(this@MainActivity, "Permission expired. Please re-upload the file.")
                        previewButton.visibility = GONE
                        uploadButton.visibility = VISIBLE
                    }
                }
            }
        }
        catch (e: Exception) {
            Log.e("File Preview Error", e.message.toString())
            Toast.makeText(this, "Error opening file: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getMimeTypeFromExtension(uri: Uri): String? {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension?.lowercase())
    }

    private fun checkAndRequestPermissions() {
        val permissionsToRequest = mutableListOf<String>()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // Android 13+ (Request all media permissions)
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_IMAGES)
            }
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_VIDEO) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_VIDEO)
            }
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_AUDIO) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_MEDIA_AUDIO)
            }
        } else {
            // Android 12 and below
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissionsToRequest.add(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }

        // Request permissions only if there are missing ones
        if (permissionsToRequest.isNotEmpty()) {
            requestPermissions(permissionsToRequest.toTypedArray(), PERMISSION_REQUEST_CODE)
        }
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

                if (!deniedPermissions.isEmpty()) {
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

    override fun onDestroy() {
        taskUpdateJob?.cancel()
        super.onDestroy()
    }
}