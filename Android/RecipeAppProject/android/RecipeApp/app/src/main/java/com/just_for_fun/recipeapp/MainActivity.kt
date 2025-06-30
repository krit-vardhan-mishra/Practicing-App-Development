package com.just_for_fun.recipeapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import com.just_for_fun.recipeapp.fragments.RecipeFragment
import com.just_for_fun.recipeapp.fragments.SavedFragment

class MainActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var searchIcon: ImageView
    private lateinit var titleText: TextView
    private lateinit var topBar: View
    private lateinit var recipesTab: TextView
    private lateinit var savedTab: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize views
        searchView = findViewById(R.id.search_view)
        searchIcon = findViewById(R.id.search_icon)
        titleText = findViewById(R.id.title_text)
        topBar = findViewById(R.id.top_bar)
        recipesTab = findViewById(R.id.recipes_tab)
        savedTab = findViewById(R.id.saved_tab)

        // Customize SearchView hint and text color
        val searchAutoComplete = searchView.findViewById<AutoCompleteTextView>(
            androidx.appcompat.R.id.search_src_text
        )
        searchAutoComplete.hint = "Search Recipe"
        searchAutoComplete.setHintTextColor(Color.BLACK)
        searchAutoComplete.setTextColor(Color.BLACK)

        // Handle search icon click
        searchIcon.setOnClickListener {
            topBar.visibility = View.GONE
            searchView.visibility = View.VISIBLE
            searchView.requestFocus()
        }

        // Handle close button in SearchView
        searchView.setOnCloseListener {
            searchView.visibility = View.GONE
            topBar.visibility = View.VISIBLE
            false
        }

        // Search text listener (optional logic)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean = false
        })

        // Set default fragment
        loadFragment(RecipeFragment())

        // Set tab click listeners
        recipesTab.setOnClickListener {
            loadFragment(RecipeFragment())
        }

        savedTab.setOnClickListener {
            loadFragment(SavedFragment())
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }
}
