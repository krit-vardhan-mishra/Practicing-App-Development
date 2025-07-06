package com.just_for_fun.justforfun.data.models

data class Rating(
    val id: String,                 // unique ID for the rating
    val mediaRatingId: String,      // ID of movie/show
    val mediaType: MediaType,       // MOVIE or TVSHOW
    val userId: String,             // ID of user who gave the rating
    val rating: Float,              // rating given
    val timestamp: Long,             // timestamp of the rating
    val isPublic: Boolean = true    // whether rating is visible to others
)