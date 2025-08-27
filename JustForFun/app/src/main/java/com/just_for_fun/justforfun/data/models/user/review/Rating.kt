package com.just_for_fun.justforfun.data.models.user.review

import com.just_for_fun.justforfun.data.models.tmdb.award.media.MediaType

data class Rating(
    val id: String = "",                    // unique ID for the rating
    val mediaId: String = "",               // ID of movie/show (TMDB ID)
    val mediaType: MediaType = MediaType.MOVIE, // MOVIE or TV_SHOW
    val userId: String = "",                // ID of user who gave the rating
    val rating: Float = 0.0f,               // rating given (1-10 scale)
    val timestamp: Long = 0L,               // timestamp when rating was given
    val lastModified: Long = 0L,            // timestamp when rating was last updated
    val isPublic: Boolean = true,           // whether rating is visible to others
    val isRewatch: Boolean = false,         // whether this is a rewatch rating
    val season: Int? = null,                // For TV shows - which season rated
    val episode: Int? = null,               // For TV shows - which episode rated
    val notes: String = ""                  // optional notes about the rating
)