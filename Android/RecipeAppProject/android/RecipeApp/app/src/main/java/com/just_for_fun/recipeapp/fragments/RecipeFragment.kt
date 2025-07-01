package com.just_for_fun.recipeapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.just_for_fun.recipeapp.MainActivity
import com.just_for_fun.recipeapp.R
import com.just_for_fun.recipeapp.adapter.RecipeRecyclerView
import com.just_for_fun.recipeapp.model.Recipe

class RecipeFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: RecipeRecyclerView
    private lateinit var fabAddRecipe: FloatingActionButton
    private lateinit var emptyState: LinearLayout

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_recipe, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initViews(view)
        setupRecyclerView()
        setupFab()
        loadRecipes()
    }

    fun searchRecipes(query: String) {
        val filteredList = MainActivity.allRecipes.filter {
            it.name.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
        adapter.submitList(filteredList)
        updateEmptyState(filteredList.isEmpty())
    }

    fun resetSearch() {
        adapter.submitList(MainActivity.allRecipes)
        updateEmptyState(MainActivity.allRecipes.isEmpty())
    }

    private fun initViews(view: View) {
        recyclerView = view.findViewById(R.id.recipe_recycler_view)
        fabAddRecipe = view.findViewById(R.id.fab_add_recipe)
        emptyState = view.findViewById(R.id.empty_state)
    }

    private fun setupRecyclerView() {
        adapter = RecipeRecyclerView()
        recyclerView.adapter = adapter
        recyclerView.layoutManager = GridLayoutManager(requireContext(), 1)

        // Add spacing between items
        val spacing = resources.getDimensionPixelSize(R.dimen.grid_spacing)
        recyclerView.addItemDecoration(GridSpacingItemDecoration(2, spacing, true))
    }

    private fun setupFab() {
        fabAddRecipe.setOnClickListener {
            // Handle add recipe action
            // You can navigate to add recipe screen here
            // For now, we'll just add a sample recipe
            addSampleRecipe()
        }
    }

    private fun loadRecipes() {
        val sampleRecipes = listOf(
            Recipe(
                id = 1,
                name = "Chocolate Lava Cake",
                image = R.drawable.lava_cake,
                cookingTime = "30 mins",
                difficulty = "Medium",
                rating = 4.8f,
                description = "Indulgent chocolate lava cakes with a molten center.",
                ingredients = listOf("Dark Chocolate", "Butter", "Eggs", "Sugar", "Flour"),
                instructions = listOf("Melt chocolate and butter.", "Whisk eggs and sugar.", "Combine mixtures.", "Add flour.", "Bake until edges are set."),
                servings = 2,
                isSaved = false,
                createdDate = "2023-10-26" // Example date
            ),
            Recipe(
                id = 2,
                name = "Pasta Carbonara",
                image = R.drawable.pasta_carbonara,
                cookingTime = "25 mins",
                difficulty = "Easy",
                rating = 4.5f,
                description = "Classic Italian pasta dish with eggs, cheese, pancetta, and pepper.",
                ingredients = listOf("Spaghetti", "Eggs", "Pecorino Romano Cheese", "Pancetta", "Black Pepper"),
                instructions = listOf("Cook pasta.", "Fry pancetta.", "Whisk eggs and cheese.", "Combine all ingredients."),
                servings = 4,
                isSaved = false,
                createdDate = "2023-10-25" // Example date
            ),
            Recipe(
                id = 3,
                name = "Caesar Salad",
                image = R.drawable.caesar_salad,
                cookingTime = "20 mins",
                difficulty = "Easy",
                rating = 4.2f,
                description = "A fresh and crisp Caesar salad with homemade dressing.",
                ingredients = listOf("Romaine Lettuce", "Croutons", "Parmesan Cheese", "Caesar Dressing"),
                instructions = listOf("Wash and chop lettuce.", "Prepare dressing.", "Toss lettuce with dressing and croutons.", "Top with Parmesan."),
                servings = 2,
                isSaved = false,
                createdDate = "2023-10-24" // Example date
            ),
            Recipe(
                id = 4,
                name = "Beef Steak",
                image = R.drawable.beef_steak,
                cookingTime = "15 mins",
                difficulty = "Medium",
                rating = 4.9f,
                description = "Perfectly seared beef steak, juicy and flavorful.",
                ingredients = listOf("Beef Steak", "Salt", "Pepper", "Olive Oil", "Garlic", "Rosemary"),
                instructions = listOf("Season steak.", "Sear in a hot pan.", "Add garlic and rosemary.", "Cook to desired doneness."),
                servings = 1,
                isSaved = false,
                createdDate = "2023-10-23" // Example date
            ),
            Recipe(
                id = 5,
                name = "Chicken Curry",
                image = R.drawable.chicken_curry,
                cookingTime = "45 mins",
                difficulty = "Medium",
                rating = 4.6f,
                description = "Aromatic and flavorful chicken curry.",
                ingredients = listOf("Chicken Breast", "Onion", "Garlic", "Ginger", "Curry Powder", "Coconut Milk", "Tomatoes"),
                instructions = listOf("Sauté onions, garlic, and ginger.", "Add chicken and spices.", "Stir in tomatoes and coconut milk.", "Simmer until chicken is cooked."),
                servings = 4,
                isSaved = false,
                createdDate = "2023-10-22" // Example date
            ),
            Recipe(
                id = 6,
                name = "Fish Tacos",
                image = R.drawable.fish_tacos,
                cookingTime = "35 mins",
                difficulty = "Easy",
                rating = 4.7f,
                description = "Delicious and fresh fish tacos with a zesty slaw.",
                ingredients = listOf("White Fish Fillets", "Corn Tortillas", "Cabbage", "Lime", "Cilantro", "Spices"),
                instructions = listOf("Season and cook fish.", "Prepare slaw.", "Warm tortillas.", "Assemble tacos."),
                servings = 3,
                isSaved = false,
                createdDate = "2023-10-21" // Example date
            )
        )

        adapter.submitList(sampleRecipes)
        updateEmptyState(sampleRecipes.isEmpty())
    }

    private fun addSampleRecipe() {
        val currentList = adapter.currentList.toMutableList()
        val newRecipeId = (currentList.maxOfOrNull { it.id } ?: 0) + 1

        val newRecipe = Recipe(
            id = newRecipeId,
            name = "New Recipe ${currentList.size + 1}",
            image = R.drawable.lava_cake,
            cookingTime = "30 mins",
            difficulty = "Easy",
            rating = 4.0f,
            description = "A newly added delicious recipe.",
            ingredients = listOf("Ingredient 1", "Ingredient 2"),
            instructions = listOf("Step 1", "Step 2"),
            servings = 2,
            isSaved = false,
            createdDate = java.time.LocalDate.now().toString()
        )
        currentList.add(newRecipe)
        adapter.submitList(currentList.toList())
        updateEmptyState(currentList.isEmpty())
    }

    private fun updateEmptyState(isEmpty: Boolean) {
        if (isEmpty) {
            emptyState.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            emptyState.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
        }
    }

    // Custom ItemDecoration for grid spacing
    inner class GridSpacingItemDecoration(
        private val spanCount: Int,
        private val spacing: Int,
        private val includeEdge: Boolean
    ) : RecyclerView.ItemDecoration() {

        override fun getItemOffsets(
            outRect: android.graphics.Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view)
            val column = position % spanCount

            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column + 1) * spacing / spanCount

                if (position < spanCount) {
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        }
    }
}