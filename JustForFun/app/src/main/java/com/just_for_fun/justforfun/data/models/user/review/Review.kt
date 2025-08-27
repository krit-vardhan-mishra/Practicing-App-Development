package com.just_for_fun.justforfun.data.models.user.review

// Represents a user review for a movie or TV show
data class Review(
    val id: String = "",                     // Unique identifier
    val mediaId: String = "",                // Movie/TV show ID (TMDB ID)
    val mediaType: String = "",              // "movie" or "tv"
    val userId: String = "",                 // User who wrote the review
    val userName: String = "",               // User's name for quick access
    val userAvatar: String = "",             // User's profile picture URL
    val rating: Float = 0.0f,                // Rating given (1-10)
    val title: String = "",                  // Review title
    val content: String = "",                // Review content
    val timestamp: Long = 0L,                // Timestamp when created
    val lastModified: Long = 0L,             // Timestamp when last modified
    val isVerifiedWatch: Boolean = false,    // Did user actually watch the movie/show
    val helpfulVotes: Int = 0,               // Number of helpful votes
    val totalVotes: Int = 0,                 // Total votes (helpful + not helpful)
    val isSpoiler: Boolean = false,          // Contains spoilers
    val isVerified: Boolean = false,         // Is this a verified review
    val reviewType: ReviewType = ReviewType.USER, // Type of review
    val pros: List<String> = emptyList(),    // What user liked
    val cons: List<String> = emptyList(),    // What user didn't like
    val wouldRecommend: Boolean? = null,     // Would recommend to others
    val watchedDate: Long? = null,           // When user watched (timestamp)
    val watchedWith: String = "",            // Who they watched with
    val watchedWhere: String = "",           // Where they watched (theater, home, etc.)
    val tags: List<String> = emptyList(),    // Review tags
    val replies: List<String> = emptyList(), // Reply IDs to this review
    val likes: List<String> = emptyList(),   // User IDs who liked this review
    val likesCount: Int = 0,                 // Cached count of likes
    val isEdited: Boolean = false,           // Whether review has been edited
    val season: Int? = null,                 // For TV shows - which season reviewed
    val episode: Int? = null                 // For TV shows - which episode reviewed
)
