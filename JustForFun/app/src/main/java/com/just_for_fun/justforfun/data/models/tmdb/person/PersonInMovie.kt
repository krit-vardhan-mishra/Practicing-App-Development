package com.just_for_fun.justforfun.data.models.tmdb.award.person

// Represents a person's involvement in a specific movie/show
data class PersonInMovie(
    val personId: String,                    // Reference to Person.id
    val personName: String,                  // Person's name for quick access
    val role: PersonRole,                    // Their role in this movie
    val characterName: String? = null,       // Character name if actor/actress
    val order: Int = 0,                      // Billing order (0 = lead, higher = supporting)
    val department: String? = null,          // Department (e.g., "Acting", "Directing", "Writing")
    val job: String? = null,                 // Specific job title (e.g., "Executive Producer")
    val profileImage: String? = null,        // Person's profile image
    val creditId: String? = null,            // Unique credit ID for this role
    val isMainCast: Boolean = false,         // Is this person in main cast
    val screenTime: Int? = null,             // Screen time in minutes (for actors)
    val salary: String? = null,              // Salary for this role
    val episodeCount: Int? = null            // Number of episodes (for TV shows)
)
