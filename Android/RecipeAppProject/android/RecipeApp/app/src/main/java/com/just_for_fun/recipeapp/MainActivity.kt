package com.just_for_fun.recipeapp

import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.AutoCompleteTextView
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.fragment.app.Fragment
import com.just_for_fun.recipeapp.fragments.RecipeFragment
import com.just_for_fun.recipeapp.fragments.SavedFragment
import com.just_for_fun.recipeapp.model.Recipe
import java.time.LocalDate
import java.time.format.DateTimeFormatter

class MainActivity : AppCompatActivity() {

    private lateinit var searchView: SearchView
    private lateinit var searchIcon: ImageView
    private lateinit var titleText: TextView
    private lateinit var topBar: View
    private lateinit var recipesTab: TextView
    private lateinit var savedTab: TextView

    // Static list to manage saved recipes (in real app, use database)
    companion object {
        val allRecipes = Recipe.getSampleRecipes().toMutableList()
        val savedRecipes = mutableListOf<Recipe>()

        fun saveRecipe(recipe: Recipe) {
            if (!savedRecipes.any { it.id == recipe.id }) {
                val currentDate = LocalDate.now().format(DateTimeFormatter.ofPattern("MMM dd, yyyy"))
                val savedRecipe = recipe.copy(isSaved = true, savedDate = currentDate)
                savedRecipes.add(savedRecipe)
                allRecipes.find { it.id == recipe.id }?.isSaved = true
            }
        }

        fun unsaveRecipe(recipe: Recipe) {
            savedRecipes.removeAll { it.id == recipe.id }
            // Update the recipe in allRecipes
            allRecipes.find { it.id == recipe.id }?.isSaved = false
        }

        fun isRecipeSaved(recipeId: Int): Boolean {
            return savedRecipes.any { it.id == recipeId }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Handle window insets for status bar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.top_bar)) { view, insets ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBarInsets.top)
            insets
        }

        initializeViews()
        setupSearchView()
        setupTabNavigation()

        // Set default fragment
        loadFragment(RecipeFragment())
        updateTabSelection(recipesTab)
    }

    private fun initializeViews() {
        searchView = findViewById(R.id.search_view)
        searchIcon = findViewById(R.id.search_icon)
        titleText = findViewById(R.id.title_text)
        topBar = findViewById(R.id.top_bar)
        recipesTab = findViewById(R.id.recipes_tab)
        savedTab = findViewById(R.id.saved_tab)
    }

    private fun setupSearchView() {
        // Customize SearchView appearance
        val searchAutoComplete = searchView.findViewById<AutoCompleteTextView>(
            androidx.appcompat.R.id.search_src_text
        )
        searchAutoComplete.hint = getString(R.string.search_hint)
        searchAutoComplete.setHintTextColor(Color.GRAY)
        searchAutoComplete.setTextColor(Color.BLACK)

        // Handle search icon click
        searchIcon.setOnClickListener {
            showSearchView()
        }

        // Handle close button in SearchView
        searchView.setOnCloseListener {
            hideSearchView()
            false
        }

        // Search functionality
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                performSearch(query)
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    // Reset search when text is cleared
                    resetSearch()
                } else {
                    performSearch(newText)
                }
                return true
            }
        })
    }

    private fun setupTabNavigation() {
        recipesTab.setOnClickListener {
            loadFragment(RecipeFragment())
            updateTabSelection(recipesTab)
        }

        savedTab.setOnClickListener {
            loadFragment(SavedFragment())
            updateTabSelection(savedTab)
        }
    }

    private fun showSearchView() {
        topBar.visibility = View.GONE
        searchView.visibility = View.VISIBLE
        searchView.requestFocus()
    }

    private fun hideSearchView() {
        searchView.visibility = View.GONE
        topBar.visibility = View.VISIBLE
        searchView.setQuery("", false)
        resetSearch()
    }

    private fun performSearch(query: String?) {
        query?.let {
            // Get current fragment and perform search
            val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment)
            when (currentFragment) {
                is RecipeFragment -> currentFragment.searchRecipes(it)
                is SavedFragment -> currentFragment.searchSavedRecipes(it)
            }
        }
    }

    fun switchToRecipesTab() {
        loadFragment(RecipeFragment())
        updateTabSelection(recipesTab)
    }

    private fun resetSearch() {
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment)
        when (currentFragment) {
            is RecipeFragment -> currentFragment.resetSearch()
            is SavedFragment -> currentFragment.resetSearch()
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment, fragment)
            .commit()
    }

    private fun updateTabSelection(selectedTab: TextView) {
        // Reset all tabs
        recipesTab.isSelected = false
        savedTab.isSelected = false

        // Set selected tab
        selectedTab.isSelected = true
    }

    override fun onBackPressed() {
        if (searchView.visibility == View.VISIBLE) {
            hideSearchView()
        } else {
            super.onBackPressed()
        }
    }
}
