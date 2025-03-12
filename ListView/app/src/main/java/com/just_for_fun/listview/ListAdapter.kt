package com.just_for_fun.listview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.net.toUri
import androidx.core.view.marginBottom
import androidx.core.view.marginEnd
import androidx.core.view.marginStart
import androidx.core.view.marginTop

class ListAdapter(private val context: Context, private val contactList: MutableList<ListItem>) : BaseAdapter() {

    private lateinit var cardView: CardView
    override fun getCount(): Int = contactList.size
    override fun getItem(position: Int): Any = contactList[position]
    override fun getItemId(position: Int): Long = position.toLong()

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View? {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.list_item, parent, false)

        val contact = contactList[position]

        val photoView = view.findViewById<ImageView>(R.id.list_contact_photo)
        val nameView = view.findViewById<TextView>(R.id.list_contact_name)
        val numberView = view.findViewById<TextView>(R.id.list_contact_number)
        val deleteButton = view.findViewById<ImageButton>(R.id.list_contact_delete)
        cardView = view.findViewById<CardView>(R.id.item_card_view)

        nameView.text = contact.name
        numberView.text = contact.number.toString()

        if (contact.photo != null) {
            photoView.setImageURI(contact.photo)
        } else {
            val defaultUri =
                "android.resource://${context.packageName}/${R.drawable.default_user_icon}".toUri()
            photoView.setImageURI(defaultUri)
        }

        deleteButton.setOnClickListener {
            contactList.removeAt(position)
            notifyDataSetChanged()
            Toast.makeText(context, "Contact deleted", Toast.LENGTH_SHORT).show()
        }

        return view
    }
}