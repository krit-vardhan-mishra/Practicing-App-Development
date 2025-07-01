package com.just_for_fun.recipeapp.adapter

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.just_for_fun.recipeapp.MainActivity
import com.just_for_fun.recipeapp.R
import com.just_for_fun.recipeapp.RecipeDetailsActivity
import com.just_for_fun.recipeapp.model.Recipe

class RecipeRecyclerView : ListAdapter<Recipe, RecipeRecyclerView.RecipeViewHolder>(DIFF_CALLBACK) {

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<Recipe>() {
            override fun areItemsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Recipe, newItem: Recipe): Boolean {
                return oldItem == newItem
            }
        }
    }

    inner class RecipeViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val recipeImageView: ImageView = itemView.findViewById(R.id.recipe_image)
        private val recipeTitleView: TextView = itemView.findViewById(R.id.recipe_title)
        private val recipeTimeView: TextView = itemView.findViewById(R.id.recipe_time)
        private val recipeDifficultyView: TextView = itemView.findViewById(R.id.recipe_difficulty)
        private val recipeRatingView: TextView = itemView.findViewById(R.id.recipe_rating)
        private val recipeSaveButton: ImageButton = itemView.findViewById(R.id.recipe_save) // Assuming this is the save button in recipe_layout

        fun bind(recipe: Recipe) {
            // Set recipe data
            recipeImageView.setImageResource(recipe.image)
            recipeTitleView.text = recipe.name
            recipeTimeView.text = recipe.cookingTime
            recipeDifficultyView.text = recipe.difficulty
            recipeRatingView.text = recipe.rating.toString()

            // Update save button state based on whether the recipe is saved
            updateSaveButtonState(recipe)

            // Handle save/unsave click
            recipeSaveButton.setOnClickListener {
                if (MainActivity.isRecipeSaved(recipe.id)) { // Check if saved using MainActivity's state
                    // Unsave recipe
                    MainActivity.unsaveRecipe(recipe)
                    recipe.isSaved = false // Update local state for immediate UI feedback
                    updateSaveButtonState(recipe)
                } else {
                    // Save recipe
                    MainActivity.saveRecipe(recipe)
                    recipe.isSaved = true // Update local state for immediate UI feedback
                    updateSaveButtonState(recipe)
                }
            }

            // Handle recipe card click to open RecipeDetailsActivity
            itemView.setOnClickListener {
                val intent = Intent(itemView.context, RecipeDetailsActivity::class.java)
                intent.putExtra("recipe_object", recipe) // Pass the Recipe object
                itemView.context.startActivity(intent)
            }
        }

        private fun updateSaveButtonState(recipe: Recipe) {
            val isSaved = MainActivity.isRecipeSaved(recipe.id)
            if (isSaved) {
                recipeSaveButton.setImageResource(R.drawable.bookmark_added) // Ensure this icon exists and represents "saved"
                recipeSaveButton.contentDescription = "Remove from saved"
            } else {
                recipeSaveButton.setImageResource(R.drawable.ic_bookmark_filled) // Ensure this icon exists and represents "unsaved"
                recipeSaveButton.contentDescription = "Save recipe"
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecipeViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.recipe_layout, parent, false)
        return RecipeViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecipeViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}