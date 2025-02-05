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
import android.provider.Settings
import android.util.Log
import android.view.View.GONE
import android.view.View.VISIBLE
import android.webkit.MimeTypeMap
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout

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

    private val ioScope = CoroutineScope(Dispatchers.IO)
    private var updateJob: Job? = null
    private lateinit var filePickerLauncher: ActivityResultLauncher<String>

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
                task.isChecked = isChecked
                taskDatabase.updateTask(task)
            }
        }

        filePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {
                contentResolver.takePersistableUriPermission(it, Intent.FLAG_GRANT_READ_URI_PERMISSION)
                currentTask?.attachment = it // Use the setter
                currentTask?.let { taskDatabase.updateTask(it) }
                uploadButton.visibility = GONE
                previewButton.visibility = VISIBLE
                openDialogBox()
            } ?: Toast.makeText(this, "No file uploaded.", Toast.LENGTH_SHORT).show()
        }
    }

    private fun loadDatabase() {
        val taskList = taskDatabase.getAllTask()

        if (taskList.isEmpty()) {
            currentTask = Task()
            val insertedRowId = taskDatabase.insertTask(currentTask!!)
            if (insertedRowId != -1L) {
                currentTask!!.id = insertedRowId.toInt() // Use the setter
                loadTaskDataIntoViews(currentTask!!)
                uploadButton.visibility = VISIBLE
                previewButton.visibility = GONE
            } else {
                Toast.makeText(this, "Error inserting initial task", Toast.LENGTH_SHORT).show()
            }
        } else {
            currentTask = taskList[0]
            loadTaskDataIntoViews(currentTask!!)
            if (currentTask?.attachment != null) {
                uploadButton.visibility = GONE
                previewButton.visibility = VISIBLE
            } else {
                uploadButton.visibility = VISIBLE
                previewButton.visibility = GONE
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
                if (currentTask == null) {
                    previousText = s?.toString()
                }
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                updateDownArrowState(s)
                validateTaskTitle(s)
            }

            override fun afterTextChanged(s: Editable?) {
                val enteredText = s.toString().trim()

                // Cancel any previous update job
                updateJob?.cancel()

                // Only proceed if text has actually changed
                if (previousText != enteredText) {
                    if (enteredText.isNotEmpty()) {
                        updateJob = ioScope.launch {
                            delay(5000) // 5 seconds delay
                            withContext(Dispatchers.Main) {
                                if (currentTask == null) {
                                    // Create new task if no current task exists
                                    currentTask = Task(enteredText, "", false, null)
                                    val insertedRowId = taskDatabase.insertTask(currentTask!!)

                                    if (insertedRowId != -1L) {
                                        currentTask?.id = insertedRowId.toInt()
                                        val taskFromDB =
                                            taskDatabase.getTaskById(insertedRowId.toInt())
                                        taskFromDB?.let {
                                            taskEditText.setText(it.title)
                                            notesArea.setText(it.notes)
                                            checkBox.isChecked = it.isChecked
                                            currentTask = it
                                        }
                                    } else {
                                        Toast.makeText(
                                            this@MainActivity,
                                            "Error inserting task.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                } else {
                                    // Update existing task
                                    currentTask?.title = enteredText
                                    currentTask?.let { taskDatabase.updateTask(it) }
                                }
                                previousText = enteredText
                            }
                        }
                    } else if (enteredText.isEmpty() && currentTask != null) {
                        // Delete task if text is empty
                        updateJob = ioScope.launch {
                            delay(5000)
                            withContext(Dispatchers.Main) {
                                taskDatabase.deleteTask(currentTask!!)
                                resetTaskFields()
                                currentTask = null
                                previousText = null
                            }
                        }
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

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                currentTask?.notes = s.toString()
            }

            override fun afterTextChanged(s: Editable?) {
                val enteredNotes = s.toString().trim()

                // Cancel any previous update job
                updateJob?.cancel()

                // Only update if notes have actually changed
                if (previousNotes != enteredNotes) {
                    updateJob = ioScope.launch {
                        delay(5000)
                        withContext(Dispatchers.Main) {
                            currentTask?.notes = enteredNotes
                            currentTask?.let { taskDatabase.updateTask(it) }
                            previousNotes = enteredNotes
                        }
                    }
                }
            }
        })
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

    private fun resetTaskFields() {
        currentTask = null
        notesArea.text.clear()
        checkBox.isChecked = false
        notesLayout.visibility = GONE
    }

    private fun setupNavigationArrowListeners() {
        downArrow.setOnClickListener {
            if (taskEditText.text.toString().trim().isNotEmpty()) {
                notesLayout.visibility = VISIBLE
                downArrow.visibility = GONE
                upArrow.visibility = VISIBLE
            } else {
                Toast.makeText(this@MainActivity, "Please enter a task title", Toast.LENGTH_SHORT).show()
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
            currentTask?.attachment = null // Use the setter
            currentTask?.let { taskDatabase.updateTask(it) }
            deleteButton.visibility = GONE
            previewButton.visibility = GONE
            uploadButton.visibility = VISIBLE
        }
    }

    private fun openFilePicker() {
        filePickerLauncher.launch("*/*")
    }

    private fun openDialogBox() {
        val alterDialogBox = AlertDialog.Builder(this)
        alterDialogBox.setTitle("Uploaded File")
        alterDialogBox.setMessage("The file path is stored as a reference. If you delete the file from storage after uploading, the preview button will throw an error.")
        alterDialogBox.setPositiveButton("ok") { dialog, _ -> dialog.dismiss() }
        alterDialogBox.show()
    }

    private fun openFilePreview() {
        val fileUri = currentTask?.attachment

        if (fileUri == null) {
            Toast.makeText(this, "No file attached", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val mimeType = contentResolver.getType(fileUri) ?: getMimeTypeFromExtension(fileUri)

            val intent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, mimeType)
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {
                Toast.makeText(this, "No app found to open this file", Toast.LENGTH_SHORT).show()
            }
        } catch (e: SecurityException) {
            Toast.makeText(this, "Permission denied. Re-upload the file.", Toast.LENGTH_SHORT).show()
            currentTask?.attachment = null
            currentTask?.let { taskDatabase.updateTask(it) }
            previewButton.visibility = GONE
            uploadButton.visibility = VISIBLE
        } catch (e: Exception) {
            Toast.makeText(this, "Error opening file: ${e.message}", Toast.LENGTH_SHORT).show()
            Log.e("File Preview Error", e.message.toString())
        }
    }

    private fun getMimeTypeFromExtension(uri: Uri): String? {
        val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension?.lowercase())
    }

    /*
    private fun openFilePreview() {
        val attachment = currentTask?.getAttachment()

        if (attachment == null)  {
            Toast.makeText(this, "No file attached", Toast.LENGTH_SHORT).show()
            return
        }

        val fileUri = Uri.parse(attachment)
        try {
            var mimeType = contentResolver.getType(fileUri)

            if (mimeType == null) {
                val fileException = MimeTypeMap.getFileExtensionFromUrl(fileUri.toString())
                if (fileException.isEmpty()) {
                    mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileException.lowercase())
                }
            }

            val intent = Intent(Intent.ACTION_VIEW)
            if (mimeType == null) {
                Toast.makeText(this, "Cannot determine file type", Toast.LENGTH_SHORT).show()
                intent.data = fileUri
            } else {
                intent.setDataAndType(fileUri, mimeType)
            }

            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            startActivity(intent)
        } catch (e: SecurityException) {
            try {
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
                contentResolver.takePersistableUriPermission(
                    fileUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION)
                openFilePreview()
            } catch (e: Exception) {
                Toast.makeText(this, "Permission denied to access file", Toast.LENGTH_SHORT).show()
            }
        } catch (e : Exception) {
            Toast.makeText(this, "Cannot open file: " + e.message, Toast.LENGTH_SHORT).show();
        }
    }
*/
    private fun checkAndRequestPermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            // For Android 13 and above
            if (checkSelfPermission(Manifest.permission.READ_MEDIA_IMAGES) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(
                    Manifest.permission.READ_MEDIA_IMAGES,
                    Manifest.permission.READ_MEDIA_VIDEO,
                    Manifest.permission.READ_MEDIA_AUDIO
                ), PERMISSION_REQUEST_CODE)
            }
        } else {
            // For Android 12 and below
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ), PERMISSION_REQUEST_CODE)
            }
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
        super.onDestroy()
        ioScope.cancel()
    }
}