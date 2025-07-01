package com.just_for_fun.recipeapp.fragments

import android.Manifest
import android.app.AlertDialog
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AccelerateInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textfield.TextInputEditText
import com.just_for_fun.recipeapp.MainActivity
import com.just_for_fun.recipeapp.R
import com.just_for_fun.recipeapp.model.Recipe
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*
import android.os.Handler
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.fragment.app.Fragment

class AddRecipeLayout : Fragment(R.layout.add_recipe_layout) {

    private var selectedImageUri: Uri? = null
    private val CAMERA_PERMISSION_CODE = 1001

    // In your Fragment/Activity class
    private lateinit var addRecipeLayout: ConstraintLayout
    private lateinit var addRecipeCard: MaterialCardView
    private lateinit var blurBackground: View

    // UI Elements
    private lateinit var etRecipeName: TextInputEditText
    private lateinit var etCookingTime: TextInputEditText
    private lateinit var etServings: TextInputEditText
    private lateinit var etDifficulty: AutoCompleteTextView
    private lateinit var etDescription: TextInputEditText
    private lateinit var etIngredients: TextInputEditText
    private lateinit var etInstructions: TextInputEditText
    private lateinit var ivRecipeImagePreview: ImageView
    private lateinit var imagePlaceholder: LinearLayout
    private lateinit var btnAddImage: MaterialButton
    private lateinit var btnCloseAddRecipe: ImageButton
    private lateinit var btnCancel: MaterialButton
    private lateinit var btnSaveRecipe: MaterialButton

    private val imagePickerLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            selectedImageUri = it
            showImagePreview(it)
        }
    }

    private val cameraLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->
            if (success && selectedImageUri != null) {
                showImagePreview(selectedImageUri!!)
            }
        }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                openCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT)
                    .show()
            }
        }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.add_recipe_layout, container, false)

        // Initialize views
        initializeAddRecipeViews(view)

        return view
    }

    private fun initializeAddRecipeViews(view: View) {
        // Find the add recipe layout
        addRecipeLayout = view.findViewById(R.id.add_recipe_layout)
        addRecipeCard = view.findViewById(R.id.add_recipe_card)
        blurBackground = view.findViewById(R.id.blur_background)
        etRecipeName = view.findViewById(R.id.et_recipe_name)
        etCookingTime = view.findViewById(R.id.et_cooking_time)
        etServings = view.findViewById(R.id.et_servings)
        etDifficulty = view.findViewById(R.id.et_difficulty)
        etDescription = view.findViewById(R.id.et_description)
        etIngredients = view.findViewById(R.id.et_ingredients)
        etInstructions = view.findViewById(R.id.et_instructions)
        ivRecipeImagePreview = view.findViewById(R.id.iv_recipe_image_preview)
        imagePlaceholder = view.findViewById(R.id.image_placeholder)
        btnAddImage = view.findViewById(R.id.btn_add_image)
        btnCloseAddRecipe = view.findViewById(R.id.btn_close_add_recipe)
        btnCancel = view.findViewById(R.id.btn_cancel)
        btnSaveRecipe = view.findViewById(R.id.btn_save_recipe)

        setupAddRecipeListeners()
        setupDifficultyDropdown()
    }

    private fun setupAddRecipeListeners() {
        btnCloseAddRecipe.setOnClickListener {
            hideAddRecipeDialog()
        }

        btnCancel.setOnClickListener {
            hideAddRecipeDialog()
        }

        btnSaveRecipe.setOnClickListener {
            btnSaveRecipe.isEnabled = false
            btnSaveRecipe.text = "Saving..."
            saveNewRecipe()
        }

        btnAddImage.setOnClickListener {
            showImagePickerOptions()
        }

        blurBackground.setOnClickListener {
            hideAddRecipeDialog()
        }
    }

    private fun setupDifficultyDropdown() {
        val difficulties = arrayOf("Easy", "Medium", "Hard")
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_dropdown_item_1line, difficulties)
        etDifficulty.setAdapter(adapter)
    }

    private fun showAddRecipeDialog() {
        clearForm()

        addRecipeLayout.visibility = View.VISIBLE

        blurBackground.alpha = 0f
        blurBackground.animate()
            .alpha(1f)
            .setDuration(200)
            .start()

        addRecipeCard.translationY = 100f
        addRecipeCard.scaleX = 0.9f
        addRecipeCard.scaleY = 0.9f
        addRecipeCard.alpha = 0f

        addRecipeCard.animate()
            .translationY(0f)
            .scaleX(1f)
            .scaleY(1f)
            .alpha(1f)
            .setDuration(300)
            .setInterpolator(OvershootInterpolator(1.1f))
            .start()
    }

    private fun hideAddRecipeDialog() {
        addRecipeCard.animate()
            .translationY(100f)
            .scaleX(0.9f)
            .scaleY(0.9f)
            .alpha(0f)
            .setDuration(250)
            .setInterpolator(AccelerateInterpolator())
            .withEndAction {
                addRecipeLayout.visibility = View.GONE
                parentFragmentManager.popBackStack()
            }
            .start()

        blurBackground.animate()
            .alpha(0f)
            .setDuration(200)
            .start()
    }

    private fun showImagePreview(uri: Uri) {
        ivRecipeImagePreview.setImageURI(uri)
        ivRecipeImagePreview.visibility = View.VISIBLE
        imagePlaceholder.visibility = View.GONE
    }

    private fun clearForm() {
        etRecipeName.text?.clear()
        etCookingTime.text?.clear()
        etServings.text?.clear()
        etDifficulty.text?.clear()
        etDescription.text?.clear()
        etIngredients.text?.clear()
        etInstructions.text?.clear()

        selectedImageUri = null
        ivRecipeImagePreview.visibility = View.GONE
        imagePlaceholder.visibility = View.VISIBLE

        etRecipeName.error = null
        etCookingTime.error = null
        etServings.error = null
        etDifficulty.error = null
        etIngredients.error = null
        etInstructions.error = null
    }

    private fun saveNewRecipe() {
        if (!validateForm()) {
            resetSaveButton()
            return
        }

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                val newRecipe = createRecipeFromForm()
                MainActivity.allRecipes.add(newRecipe)
                Toast.makeText(requireContext(), "Recipe saved successfully!", Toast.LENGTH_SHORT).show()
                hideAddRecipeDialog()
            } catch (e: Exception) {
                Toast.makeText(
                    requireContext(),
                    "Error saving recipe: ${e.message}",
                    Toast.LENGTH_SHORT
                ).show()
            } finally {
                resetSaveButton()
            }
        }, 1000)
    }

    private fun resetSaveButton() {
        btnSaveRecipe.isEnabled = true
        btnSaveRecipe.text = "Save Recipe"
    }

    private fun validateForm(): Boolean {
        var isValid = true

        etRecipeName.error = null
        etCookingTime.error = null
        etServings.error = null
        etDifficulty.error = null
        etIngredients.error = null
        etInstructions.error = null

        if (etRecipeName.text.toString().trim().isEmpty()) {
            etRecipeName.error = "Recipe name is required"
            isValid = false
        }

        if (etCookingTime.text.toString().trim().isEmpty()) {
            etCookingTime.error = "Cooking time is required"
            isValid = false
        } else {
            val time = etCookingTime.text.toString().toIntOrNull()
            if (time == null || time <= 0) {
                etCookingTime.error = "Please enter a valid cooking time"
                isValid = false
            }
        }

        if (etServings.text.toString().trim().isEmpty()) {
            etServings.error = "Servings is required"
            isValid = false
        } else {
            val servings = etServings.text.toString().toIntOrNull()
            if (servings == null || servings <= 0) {
                etServings.error = "Please enter a valid number of servings"
                isValid = false
            }
        }

        if (etDifficulty.text.toString().trim().isEmpty()) {
            etDifficulty.error = "Please select difficulty level"
            isValid = false
        }

        if (etIngredients.text.toString().trim().isEmpty()) {
            etIngredients.error = "Ingredients are required"
            isValid = false
        }

        if (etInstructions.text.toString().trim().isEmpty()) {
            etInstructions.error = "Instructions are required"
            isValid = false
        }

        return isValid
    }

    private fun createRecipeFromForm(): Recipe {
        val ingredients = etIngredients.text.toString()
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val instructions = etInstructions.text.toString()
            .split("\n")
            .map { it.trim() }
            .filter { it.isNotEmpty() }

        val currentDate = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault()).format(Date())

        return Recipe(
            id = generateRecipeId(),
            name = etRecipeName.text.toString().trim(),
            image = saveImageAndGetResourceId(),
            cookingTime = "${etCookingTime.text.toString().trim()} min",
            difficulty = etDifficulty.text.toString().trim(),
            rating = 0f,
            description = etDescription.text.toString().trim(),
            ingredients = ingredients,
            instructions = instructions,
            servings = etServings.text.toString().toIntOrNull() ?: 1,
            isSaved = false,
            savedDate = null,
            createdDate = currentDate
        )
    }

    private fun saveImageAndGetResourceId(): Int {
        selectedImageUri?.let { uri ->
            try {
                val inputStream = requireContext().contentResolver.openInputStream(uri)
                val fileName = "recipe_${System.currentTimeMillis()}.jpg"
                val file = File(requireContext().filesDir, fileName)
                val outputStream = FileOutputStream(file)

                inputStream?.copyTo(outputStream)
                inputStream?.close()
                outputStream.close()

                return R.drawable.lava_cake
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        return R.drawable.lava_cake
    }


    private fun generateRecipeId(): Int {
        return System.currentTimeMillis().toInt()
    }

    private fun showImagePickerOptions() {
        val options = arrayOf("Choose from Gallery", "Take Photo")

        AlertDialog.Builder(requireContext())
            .setTitle("Select Image")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> imagePickerLauncher.launch("image/*")
                    1 -> checkCameraPermissionAndOpen()
                }
            }
            .show()
    }

    private fun checkCameraPermissionAndOpen() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                openCamera()
            }

            else -> {
                permissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun openCamera() {
        val imageFile =
            File(requireContext().externalCacheDir, "recipe_${System.currentTimeMillis()}.jpg")
        selectedImageUri = FileProvider.getUriForFile(
            requireContext(),
            "${requireContext().packageName}.fileprovider",
            imageFile
        )
        cameraLauncher.launch(selectedImageUri)
    }
}