package com.just_for_fun.justforfun.data.models.rating

data class Rating(
    val id: String = "",                     // unique rating ID
    val userId: String = "",                 // ID of user who gave the rating
    val mediaId: String = "",                // TMDB movie/TV show ID
    val mediaType: String = "",              // "movie" or "tv"
    val rating: Float = 0.0f,                // user's rating (1-10)
    val timestamp: Long = 0L,                // when rating was given
    val lastModified: Long = 0L,             // when rating was last updated
    val isRewatch: Boolean = false           // whether this is a rewatch rating
)
