package com.just_for_fun.justforfun.data.models.tmdb.award.person

// Represents a person (actor, director, writer, etc.) in the entertainment industry
data class Person(
    val id: String,                          // Unique identifier
    val name: String,                        // Full name
    val profileImage: String? = null,        // Profile photo URL
    val biography: String? = null,           // Biography/description
    val birthDate: String? = null,           // Birth date (DD-MM-YYYY)
    val deathDate: String? = null,           // Death date if applicable (DD-MM-YYYY)
    val birthPlace: String? = null,          // Place of birth (city, country)
    val nationality: String? = null,         // Nationality
    val gender: Gender? = null,              // Gender
    val height: String? = null,              // Height (e.g., "5'10\"" or "178 cm")
    val knownFor: List<String> = emptyList(), // List of movie/show IDs they're known for
    val roles: List<PersonRole> = emptyList(), // Their roles in the industry
    val awards: List<String> = emptyList(),   // List of award IDs they've won
    val socialMedia: SocialMediaLinks? = null, // Social media profiles
    val imdbId: String? = null,              // IMDB ID for external reference
    val tmdbId: String? = null,              // TMDB ID for external reference
    val isPopular: Boolean = false,          // Is this person currently popular/trending
    val popularityScore: Float = 0.0f,       // Popularity ranking score
    val totalMovies: Int = 0,                // Total number of movies/shows
    val totalAwards: Int = 0,                // Total number of awards won
    val netWorth: String? = null,            // Estimated net worth
    val agent: String? = null,               // Agent/management contact
    val trivia: List<String> = emptyList(),  // Fun facts/trivia
    val alternateNames: List<String> = emptyList(), // Alternative names/stage names
    val isDeceased: Boolean = false,         // Whether the person is alive or not
    val spouseName: String? = null,          // Current/former spouse name
    val children: Int? = null,               // Number of children
    val education: List<String> = emptyList(), // Educational background
    val careerStartYear: Int? = null,        // Year they started their career
    val isRetired: Boolean = false           // Whether they're retired from acting/directing
)