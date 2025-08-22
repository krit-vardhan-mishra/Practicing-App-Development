package com.just_for_fun.justforfun.data.models.watchlist

data class WatchlistItem(
    val id: String = "",                     // unique watchlist item ID
    val userId: String = "",                 // ID of user who added this item
    val mediaId: String = "",                // TMDB movie/TV show ID
    val mediaType: String = "",              // "movie" or "tv"
    val addedDate: Long = 0L,                // when item was added to watchlist
    val priority: Int = 0,                   // priority level (1-5, 5 being highest)
    val notes: String = "",                  // optional user notes
    val isWatched: Boolean = false,          // whether user has watched this
    val watchedDate: Long? = null            // when user marked this as watched
)
