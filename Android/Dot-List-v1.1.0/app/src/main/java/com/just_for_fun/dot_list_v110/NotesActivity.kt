package com.just_for_fun.dot_list_v110

import android.app.AlertDialog
import android.animation.Animator
import android.animation.ObjectAnimator
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.Color.*
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StrikethroughSpan
import android.text.style.StyleSpan
import android.text.style.UnderlineSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Typeface
import android.net.Uri
import android.provider.OpenableColumns
import android.text.SpannableStringBuilder
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ImageSpan
import android.util.Log
import android.view.GestureDetector
import android.view.LayoutInflater
import android.widget.Toast
import com.google.android.material.floatingactionbutton.FloatingActionButton


class NotesActivity : AppCompatActivity(), View.OnCreateContextMenuListener {
    private lateinit var titleEditText: TextView
    private lateinit var notesEditText: EditText
    private lateinit var backButton : ImageButton
    private var currentTaskId: String? = null

    private val fileUris = mutableListOf<Uri>()
    private var lastSelectedPosition: Int = 0
    private lateinit var gestureDetector: GestureDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notes)

        titleEditText = findViewById(R.id.notes_title)
        notesEditText = findViewById(R.id.notes_area)
        backButton = findViewById(R.id.back_arrow)

        notesEditText.movementMethod = LinkMovementMethod.getInstance()
        notesEditText.setText(SpannableStringBuilder(""), TextView.BufferType.EDITABLE)

        val shareButton = findViewById<FloatingActionButton>(R.id.share_button)
        val shareLayout = findViewById<LinearLayout>(R.id.share_layout)

        shareButton.setOnClickListener {
            vibrateDevice()
            showShareLayout(shareLayout)
        }

        currentTaskId = intent.getStringExtra("taskId")
        titleEditText.setText(intent.getStringExtra("title"))
        notesEditText.setText(intent.getStringExtra("content"))

        backButton.setOnClickListener {
            saveAndExit()
        }

        val rootLayout = findViewById<View>(R.id.notes_main)
        rootLayout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                val rect = android.graphics.Rect()
                shareLayout.getGlobalVisibleRect(rect)
                if (!rect.contains(event.rawX.toInt(), event.rawY.toInt())) {
                    hideShareLayout(shareLayout)
                }
            }
            false
        }

        notesEditText.customSelectionActionModeCallback  = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                mode?.menuInflater?.inflate(R.menu.task_actions, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return when (item?.itemId) {
                    R.id.action_bold -> {
                        applyFormatting(TextStyle.BOLD)
                        mode?.finish()
                        true
                    }
                    R.id.action_italic -> {
                        applyFormatting(TextStyle.ITALIC)
                        mode?.finish()
                        true
                    }
                    R.id.action_underline -> {
                        applyFormatting(TextStyle.UNDERLINE)
                        mode?.finish()
                        true
                    }
                    R.id.action_strikethrough -> {
                        applyFormatting(TextStyle.STRIKETHROUGH)
                        mode?.finish()
                        true
                    }
                    R.id.action_color -> {
                        applyFormatting(TextStyle.COLOR)
                        mode?.finish()
                        true
                    }
                    R.id.action_normal -> {
                        removeFormattingFromSelection()
                        mode?.finish()
                        true
                    }
                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode?) { }
        }

        notesEditText.customInsertionActionModeCallback = object : ActionMode.Callback {
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                mode?.menuInflater?.inflate(R.menu.insertion_actions, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean = false

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return when (item?.itemId) {
                    R.id.action_upload -> {
                        lastSelectedPosition = notesEditText.selectionStart
                        openFilePicker()
                        mode?.finish()
                        true
                    }

                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode?) {}
        }

        gestureDetector = GestureDetector(this, object : GestureDetector.SimpleOnGestureListener() {
            override fun onLongPress(e: MotionEvent) {
                val x = e.x.toInt()
                val y = e.y.toInt()
                val layout = notesEditText.layout ?: return
                val line = layout.getLineForVertical(y)
                val offset = layout.getOffsetForHorizontal(line, e.x)
                val spannable = notesEditText.text as Spannable
                val spans = spannable.getSpans(offset, offset, FileAttachmentClickableSpan::class.java)
                if (spans.isNotEmpty()) {
                    showDeleteAttachmentDialog(spans[0])
                }
            }
        })

        notesEditText.setOnTouchListener {_, event ->
            gestureDetector.onTouchEvent(event)
            false
        }
    }

    private fun applyFormatting(style: TextStyle) {
        val start = notesEditText.selectionStart
        val end = notesEditText.selectionEnd

        if (start < 0 || end < 0 || start == end) {
            Toast.makeText(this, "Please select some text first", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            val spannable = if (notesEditText.text is SpannableStringBuilder) {
                notesEditText.text as SpannableStringBuilder
            } else {
                SpannableStringBuilder(notesEditText.text)
            }

            when (style) {
                TextStyle.BOLD -> spannable.setSpan(StyleSpan(Typeface.BOLD), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                TextStyle.ITALIC -> spannable.setSpan(StyleSpan(Typeface.ITALIC), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                TextStyle.UNDERLINE -> spannable.setSpan(UnderlineSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                TextStyle.STRIKETHROUGH -> spannable.setSpan(StrikethroughSpan(), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                TextStyle.COLOR -> showColorPicker { selectedColor ->
                    spannable.setSpan(ForegroundColorSpan(selectedColor), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
                }
                TextStyle.NORMAL -> removeFormattingFromSelection()
            }

            // Make sure to update the EditText
            notesEditText.text = spannable
            notesEditText.setSelection(end)
        } catch (e: Exception) {
            Log.e("NotesActivity", "Error applying text style", e)
            Toast.makeText(this, "Error applying text style", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showColorPicker(onColorSelected: (Int) -> Unit) {
        val colors = listOf(
            RED to "Red",
            GREEN to "Green",
            BLUE to "Blue",
            YELLOW to "Yellow",
            BLACK to "Black",
            WHITE to "White",
            CYAN to "Cyan"
        )

        val colorNames = colors.map { it.second }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Choose a Color")
            .setItems(colorNames) { _, which ->
                val selectedColor = colors[which].first
                onColorSelected(selectedColor)
            }
            .show()
    }

    private fun removeFormattingFromSelection() {
        val spannable = notesEditText.text as Spannable
        val selStart = notesEditText.selectionStart
        val selEnd = notesEditText.selectionEnd

        val spans = spannable.getSpans(selStart, selEnd, Any::class.java)
        for (span in spans) {
            if (span is StyleSpan || span is UnderlineSpan || span is StrikethroughSpan || span is ForegroundColorSpan) {
                val spanStart = spannable.getSpanStart(span)
                val spanEnd = spannable.getSpanEnd(span)
                val flags = spannable.getSpanFlags(span)

                if (spanStart < selEnd && spanEnd > selStart) {
                    spannable.removeSpan(span)
                }

                if (spanStart < selStart) {
                    when (span) {
                        is StyleSpan -> spannable.setSpan(StyleSpan(span.style), spanStart, selStart, flags)
                        is UnderlineSpan -> spannable.setSpan(UnderlineSpan(), spanStart, selStart, flags)
                        is StrikethroughSpan -> spannable.setSpan(StrikethroughSpan(), spanStart, selStart, flags)
                        is ForegroundColorSpan -> spannable.setSpan(ForegroundColorSpan(span.foregroundColor), spanStart, selStart, flags)
                    }
                }

                if (spanEnd > selEnd) {
                    when (span) {
                        is StyleSpan -> spannable.setSpan(StyleSpan(span.style), selEnd, spanEnd, flags)
                        is UnderlineSpan -> spannable.setSpan(UnderlineSpan(), selEnd, spanEnd, flags)
                        is StrikethroughSpan -> spannable.setSpan(StrikethroughSpan(), selEnd, spanEnd, flags)
                        is ForegroundColorSpan -> spannable.setSpan(ForegroundColorSpan(span.foregroundColor), selEnd, spanEnd, flags)
                    }
                }
            }
        }
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
        intent.addCategory(Intent.CATEGORY_OPENABLE)
        intent.type = "*/*"
        intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(intent, 1)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            data?.let { intentData ->
                if (intentData.clipData != null) {
                    val count = intentData.clipData!!.itemCount
                    for (i in 0 until count) {
                        val fileUri = intentData.clipData!!.getItemAt(i).uri
                        fileUris.add(fileUri)
                        insertFilePreview(fileUri)
                        lastSelectedPosition = notesEditText.selectionStart
                    }
                } else if (intentData.data != null) {
                    val fileUri = intentData.data!!
                    fileUris.add(fileUri)
                    insertFilePreview(fileUri)
                    lastSelectedPosition = notesEditText.selectionStart
                }
            }
        }
    }

    private fun insertFilePreview(fileUri: Uri) {
        val inflater = LayoutInflater.from(this)
        val filePreviewView = inflater.inflate(R.layout.file_preview, null)

        val fileNameTextView = filePreviewView.findViewById<TextView>(R.id.file_type)
        fileNameTextView.text = getFileName(fileUri)

        val previewBitmap = getBitmapFromView(filePreviewView)
        val imageSpan = ImageSpan(this, previewBitmap)

        val clickableSpan = FileAttachmentClickableSpan(fileUri)

        val currentText = SpannableStringBuilder(notesEditText.text)
        val insertionIndex = if (lastSelectedPosition <= currentText.length) {
            lastSelectedPosition
        } else {
            currentText.length
        }

        if (insertionIndex > 0 && currentText[insertionIndex - 1] != '\n') {
            currentText.insert(insertionIndex, "\n")
            lastSelectedPosition++
        }

        currentText.insert(insertionIndex, " ")
        currentText.setSpan(imageSpan, insertionIndex, insertionIndex + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        currentText.setSpan(clickableSpan, insertionIndex, insertionIndex + 1, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        currentText.insert(insertionIndex + 1, "\n")

        notesEditText.setText(currentText)
        notesEditText.setSelection(insertionIndex + 2)
    }

    private fun openFile(fileUri: Uri) {
        val intent = Intent(Intent.ACTION_VIEW)
        val type = contentResolver.getType(fileUri) ?: "*/*"
        intent.setDataAndType(fileUri, type)
        intent.flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
        startActivity(intent)

    }

    private fun getFileName(uri: Uri): String {
        var name = "Unknown File"
        val cursor: Cursor? = contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            val nameIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (it.moveToFirst() && nameIndex >= 0) {
                name = it.getString(nameIndex)
            }
        }
        return name
    }

    private fun getBitmapFromView(view: View): Bitmap {
        view.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED)
        val bitmap = Bitmap.createBitmap(view.measuredWidth, view.measuredHeight, Bitmap.Config.ARGB_8888)
        val canvas = android.graphics.Canvas(bitmap)
        view.layout(0, 0, view.measuredWidth, view.measuredHeight)
        view.draw(canvas)
        return bitmap
    }

    private fun showShareLayout(shareLayout: LinearLayout) {
        if (shareLayout.visibility == View.GONE) {
            shareLayout.visibility = View.VISIBLE

            val slideUp = ObjectAnimator.ofFloat(shareLayout, "translationY", shareLayout.height.toFloat(), 0f)
            slideUp.duration = 500
            slideUp.start()
        }
    }

    private fun hideShareLayout(shareLayout: LinearLayout) {
        val animation = ObjectAnimator.ofFloat(shareLayout, "translationY", 0f, shareLayout.height.toFloat())
        animation.duration = 500

        animation.addListener(object : android.animation.AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator) {
                shareLayout.visibility = View.GONE
            }
        })
        animation.start()
    }

    private fun vibrateDevice() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator

        if (vibrator.hasVibrator()) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                val vibrationEffect = VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE)
                vibrator.vibrate(vibrationEffect)
            } else {
                vibrator.vibrate(50)
            }
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

    private fun showDeleteAttachmentDialog(clickableSpan: FileAttachmentClickableSpan) {
        AlertDialog.Builder(this)
            .setTitle("Delete Attachment")
            .setMessage("Do you want to delete this attachment?")
            .setPositiveButton("Delete") { _, _ ->
                deleteAttachment(clickableSpan)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun deleteAttachment(clickableSpan: FileAttachmentClickableSpan) {
        val spannableBuilder = SpannableStringBuilder(notesEditText.text)
        val start = spannableBuilder.getSpanStart(clickableSpan)
        val end = spannableBuilder.getSpanEnd(clickableSpan)

        if (start >= 0 && end >= 0) {
            spannableBuilder.delete(start, end)
        }

        fileUris.remove(clickableSpan.fileUri)

        notesEditText.setText(spannableBuilder)
    }
    
    private inner class FileAttachmentClickableSpan(val fileUri: Uri) : ClickableSpan() {
        override fun onClick(widget: View) {
            openFile(fileUri)
        }
    }
}