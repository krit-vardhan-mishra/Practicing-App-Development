package com.just_for_fun.justforfun.data.models.review

data class Review(
    val id: String = "",                     // unique review ID
    val userId: String = "",                 // ID of user who wrote the review
    val mediaId: String = "",                // TMDB movie/TV show ID
    val mediaType: String = "",              // "movie" or "tv"
    val title: String = "",                  // review title
    val content: String = "",                // review content
    val rating: Float = 0.0f,                // user's rating (1-10)
    val timestamp: Long = 0L,                // when review was created
    val lastModified: Long = 0L,             // when review was last edited
    val likes: List<String> = emptyList(),   // list of user IDs who liked this review
    val likesCount: Int = 0,                 // cached count of likes
    val replies: List<String> = emptyList(), // list of reply IDs
    val repliesCount: Int = 0,               // cached count of replies
    val isSpoiler: Boolean = false,          // whether review contains spoilers
    val isEdited: Boolean = false,           // whether review has been edited
    val tags: List<String> = emptyList()     // optional tags for categorizing reviews
)
