package com.just_for_fun.justforfun.data.models.tmdb.award

// Represents an award in the entertainment industry
data class Award(
    val id: String = "",                     // Unique identifier
    val name: String = "",                   // Award name (e.g., "Academy Award for Best Actor")
    val category: String = "",               // Category (e.g., "Best Actor", "Best Director")
    val awardShow: String = "",              // Award show name (e.g., "Academy Awards", "Golden Globes")
    val year: Int = 0,                       // Year awarded
    val winner: String = "",                 // Person ID who won
    val winnerName: String = "",             // Winner's name for quick access
    val movieId: String = "",                // Movie ID if movie-related award
    val movieName: String = "",              // Movie name for quick access
    val isNomination: Boolean = false,       // Is this just a nomination or actual win
    val description: String = "",            // Award description
    val prestige: AwardPrestige = AwardPrestige.MEDIUM, // How prestigious is this award
    val image: String = "",                  // Award image/trophy photo
    val ceremony: String = "",               // Specific ceremony name
    val presenter: String = ""               // Who presented the award
)