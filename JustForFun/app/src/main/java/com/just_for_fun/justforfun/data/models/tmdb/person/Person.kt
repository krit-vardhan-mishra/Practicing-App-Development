package com.just_for_fun.justforfun.data.models.tmdb.award.person

// Represents a person (actor, director, writer, etc.) in the entertainment industry
data class Person(
    val id: String = "",                     // Unique identifier (TMDB ID)
    val name: String = "",                   // Full name
    val profileImage: String = "",           // Profile photo URL
    val biography: String = "",              // Biography/description
    val birthDate: Long? = null,             // Birth date as timestamp
    val deathDate: Long? = null,             // Death date as timestamp if applicable
    val birthPlace: String = "",             // Place of birth (city, country)
    val nationality: String = "",            // Nationality
    val gender: Gender = Gender.UNKNOWN,     // Gender
    val height: String = "",                 // Height (e.g., "5'10\"" or "178 cm")
    val knownFor: List<String> = emptyList(), // List of movie/show IDs they're known for
    val roles: List<PersonRole> = emptyList(), // Their roles in the industry
    val awards: List<String> = emptyList(),   // List of award IDs they've won
    val socialMedia: SocialMediaLinks = SocialMediaLinks(), // Social media profiles
    val imdbId: String = "",                 // IMDB ID for external reference
    val tmdbId: String = "",                 // TMDB ID for external reference
    val isPopular: Boolean = false,          // Is this person currently popular/trending
    val popularityScore: Float = 0.0f,       // Popularity ranking score
    val totalMovies: Int = 0,                // Total number of movies/shows
    val totalAwards: Int = 0,                // Total number of awards won
    val netWorth: String = "",               // Estimated net worth
    val agent: String = "",                  // Agent/management contact
    val trivia: List<String> = emptyList(),  // Fun facts/trivia
    val alternateNames: List<String> = emptyList(), // Alternative names/stage names
    val isDeceased: Boolean = false,         // Whether the person is alive or not
    val spouseName: String = "",             // Current/former spouse name
    val children: Int = 0,                   // Number of children
    val education: List<String> = emptyList(), // Educational background
    val careerStartYear: Int = 0,            // Year they started their career (0 if unknown)
    val isRetired: Boolean = false           // Whether they're retired from acting/directing
)