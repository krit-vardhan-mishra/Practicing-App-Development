package com.just_for_fun.listview

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.fragment.app.DialogFragment

class AddContactDialog(private val onContactAdded: (ListItem) -> Unit) : DialogFragment() {

    private var selectedImageUri: Uri? = null
    private lateinit var nameEditText : EditText
    private lateinit var numberEditText : EditText
    private lateinit var contactPhoto : ImageView
    private lateinit var saveButton : Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.add_list_layout, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        dialog?.window?.setBackgroundDrawableResource(android.R.color.transparent)

        nameEditText = view.findViewById<EditText>(R.id.enter_contact_name)
        numberEditText = view.findViewById<EditText>(R.id.enter_contact_number)
        contactPhoto = view.findViewById<ImageView>(R.id.enter_contact_photo)
        saveButton = view.findViewById<Button>(R.id.enter_contact_save)

        contactPhoto.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 100)
        }

        saveButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val number = numberEditText.text.toString().toLongOrNull()

            if (name.isNotEmpty() && number != null) {
                val newContact = ListItem(selectedImageUri, name, number)
                onContactAdded(newContact)
                dismiss()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100 && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            view?.findViewById<ImageView>(R.id.enter_contact_photo)?.setImageURI(selectedImageUri)
            contactPhoto.background = null
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, android.R.style.ThemeOverlay_Material_Dialog)
    }
}
