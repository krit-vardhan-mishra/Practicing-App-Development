package com.just_for_fun.listview

import android.os.Bundle
import android.widget.ImageButton
import android.widget.ListView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var listView: ListView
    private lateinit var addButton: ImageButton
    private val contactList = mutableListOf<ListItem>()
    private lateinit var adapter: ListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        listView = findViewById<ListView>(R.id.list_view)
        addButton = findViewById<ImageButton>(R.id.add_button)

        adapter = ListAdapter(this, contactList)
        listView.adapter = adapter

        addButton.setOnClickListener {
            val dialog = AddContactDialog { contact ->
                contactList.add(contact)
                adapter.notifyDataSetChanged()
            }
            dialog.show(supportFragmentManager, "AddContactDialog")
        }
    }
}