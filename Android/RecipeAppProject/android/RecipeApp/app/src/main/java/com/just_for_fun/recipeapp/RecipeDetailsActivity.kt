package com.just_for_fun.recipeapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.just_for_fun.recipeapp.adapter.IngredientsAdapter
import com.just_for_fun.recipeapp.adapter.InstructionsAdapter
import com.just_for_fun.recipeapp.model.Recipe

class RecipeDetailsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.recipe_details)

        // Handle window insets for toolbar
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.toolbar_layout)) { view: View, insets: WindowInsetsCompat ->
            val statusBarInsets = insets.getInsets(WindowInsetsCompat.Type.statusBars())
            view.updatePadding(top = statusBarInsets.top)
            insets
        }

        val recipe: Recipe? = intent.getParcelableExtra("recipe_object")

        recipe?.let {
            // Initialize views
            val backButton: ImageButton = findViewById(R.id.back_button)
            val shareButton: ImageButton = findViewById(R.id.share_button)
            val toolbarTitle: TextView = findViewById(R.id.toolbar_title)
            val recipeImage: ImageView = findViewById(R.id.recipe_image)
            val recipeSaveButton: ImageButton = findViewById(R.id.recipe_save_button)
            val recipeTitle: TextView = findViewById(R.id.recipe_title)
            val recipeTime: TextView = findViewById(R.id.recipe_time)
            val recipeDifficulty: TextView = findViewById(R.id.recipe_difficulty)
            val recipeServings: TextView = findViewById(R.id.recipe_servings)
            val recipeRating: TextView = findViewById(R.id.recipe_rating)
            val recipeDescription: TextView = findViewById(R.id.recipe_description)
            val ingredientsSection: LinearLayout = findViewById(R.id.ingredients_section)
            val ingredientsRecyclerView: RecyclerView = findViewById(R.id.ingredients_recycler_view)
            val instructionsSection: LinearLayout = findViewById(R.id.instructions_section)
            val instructionsRecyclerView: RecyclerView = findViewById(R.id.instructions_recycler_view)

            // Set data to views
            toolbarTitle.text = it.name
            recipeImage.setImageResource(it.image)
            recipeTitle.text = it.name
            recipeTime.text = it.cookingTime
            recipeDifficulty.text = it.difficulty
            recipeServings.text = it.servings.toString()
            recipeRating.text = it.rating.toString()
            recipeDescription.text = it.description

            // Handle back button
            backButton.setOnClickListener {
                onBackPressed()
            }

            // Handle share button
            shareButton.setOnClickListener {
                val shareIntent = Intent(Intent.ACTION_SEND)
                shareIntent.type = "text/plain"
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Check out this recipe: ${recipe.name}")
                shareIntent.putExtra(Intent.EXTRA_TEXT, "Recipe: ${recipe.name}\nDescription: ${recipe.description}\n\nFind more recipes on our app!")
                startActivity(Intent.createChooser(shareIntent, "Share Recipe"))
            }

            // Update save button state
            updateSaveButtonState(recipeSaveButton, it)

            // Handle save/unsave click
            recipeSaveButton.setOnClickListener { view ->
                if (MainActivity.isRecipeSaved(it.id)) {
                    MainActivity.unsaveRecipe(it)
                    it.isSaved = false
                    Toast.makeText(view.context, "${it.name} removed from saved", Toast.LENGTH_SHORT).show()
                } else {
                    MainActivity.saveRecipe(it)
                    it.isSaved = true
                    Toast.makeText(view.context, "${it.name} saved successfully", Toast.LENGTH_SHORT).show()
                }
                updateSaveButtonState(recipeSaveButton, it)
            }

            // Set up Ingredients RecyclerView
            if (it.ingredients.isNotEmpty()) {
                ingredientsSection.visibility = View.VISIBLE
                ingredientsRecyclerView.layoutManager = LinearLayoutManager(this)
                ingredientsRecyclerView.adapter = IngredientsAdapter(it.ingredients)
            } else {
                ingredientsSection.visibility = View.GONE
            }

            // Set up Instructions RecyclerView
            if (it.instructions.isNotEmpty()) {
                instructionsSection.visibility = View.VISIBLE
                instructionsRecyclerView.layoutManager = LinearLayoutManager(this)
                instructionsRecyclerView.adapter = InstructionsAdapter(it.instructions)
            } else {
                instructionsSection.visibility = View.GONE
            }

        } ?: run {
            Toast.makeText(this, "Recipe not found!", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun updateSaveButtonState(button: ImageButton, recipe: Recipe) {
        if (MainActivity.isRecipeSaved(recipe.id)) {
            button.setImageResource(R.drawable.bookmark_added) // This should be your "saved" icon
            button.contentDescription = "Remove from saved"
        } else {
            button.setImageResource(R.drawable.ic_bookmark_filled) // This should be your "unsaved" icon
            button.contentDescription = "Save recipe"
        }
    }
}