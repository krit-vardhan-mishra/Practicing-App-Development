package com.just_for_fun.justforfun.data.models.user.review

// Represents a user review for a movie
data class Review(
    val id: String,                          // Unique identifier
    val movieId: String,                     // Movie being reviewed
    val userId: String,                      // User who wrote the review
    val userName: String,                    // User's name for quick access
    val userAvatar: String? = null,          // User's profile picture
    val rating: Float,                       // Rating given (1-5 or 1-10)
    val title: String? = null,               // Review title
    val content: String,                     // Review content
    val dateCreated: String,                 // Date created (DD-MM-YYYY)
    val dateModified: String? = null,        // Date last modified
    val isVerifiedPurchase: Boolean = false, // Did user actually watch/buy the movie
    val helpfulVotes: Int = 0,              // Number of helpful votes
    val totalVotes: Int = 0,                // Total votes (helpful + not helpful)
    val isSpoiler: Boolean = false,         // Contains spoilers
    val isVerified: Boolean = false,        // Is this a verified review
    val reviewType: ReviewType = ReviewType.USER, // Type of review
    val pros: List<String> = emptyList(),   // What user liked
    val cons: List<String> = emptyList(),   // What user didn't like
    val wouldRecommend: Boolean? = null,    // Would recommend to others
    val watchedDate: String? = null,        // When user watched the movie
    val watchedWith: String? = null,        // Who they watched with
    val watchedWhere: String? = null,       // Where they watched (theater, home, etc.)
    val tags: List<String> = emptyList(),   // Review tags
    val replies: List<String> = emptyList() // Reply IDs to this review
)
