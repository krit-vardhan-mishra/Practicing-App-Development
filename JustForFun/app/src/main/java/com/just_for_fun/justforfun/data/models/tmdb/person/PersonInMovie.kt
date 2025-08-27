package com.just_for_fun.justforfun.data.models.tmdb.award.person

// Represents a person's involvement in a specific movie/show
data class PersonInMovie(
    val personId: String = "",               // Reference to Person.id
    val personName: String = "",             // Person's name for quick access
    val role: PersonRole = PersonRole.ACTOR, // Their role in this movie
    val characterName: String = "",          // Character name if actor/actress
    val order: Int = 0,                      // Billing order (0 = lead, higher = supporting)
    val department: String = "",             // Department (e.g., "Acting", "Directing", "Writing")
    val job: String = "",                    // Specific job title (e.g., "Executive Producer")
    val profileImage: String = "",           // Person's profile image
    val creditId: String = "",               // Unique credit ID for this role
    val isMainCast: Boolean = false,         // Is this person in main cast
    val screenTime: Int = 0,                 // Screen time in minutes (for actors, 0 if unknown)
    val salary: String = "",                 // Salary for this role
    val episodeCount: Int = 0                // Number of episodes (for TV shows, 0 if not applicable)
)
