package com.just_for_fun.justforfun.data.models.user

import com.just_for_fun.justforfun.data.models.tmdb.award.media.Genre

// Represents user statistics for a specific year
data class UserStats(
    val userId: String,
    val year: Int,
    val moviesWatched: Int = 0,
    val tvShowsWatched: Int = 0,
    val reviewsWritten: Int = 0,
    val ratingsGiven: Int = 0,
    val topGenre: Genre? = null,
    val averageRating: Float = 0.0f,
    val totalWatchTime: Int = 0
)
