package com.just_for_fun.justforfun.data.models

data class Review(
    val id: String,                     // unique ID for the review
    val reviewMediaId: String,          // ID of the movie/show that is reviewed
    val userId: String,                 // ID of the user who wrote the review
    val userName: String,               // name of the user not username
    val userProfilePhoto: String,       // profile photo of the user
    val rating: Float,                  // rating given by the user
    val reviewText: String,             // content of the review
    val timestamp: Long,                // review post time
    val likeCount: Int = 0,                 // number of likes
    val dislikeCount: Int = 0,              // number of dislikes
    val isLiked: Boolean = false,               // the current user liked the review or not
    val isDisliked: Boolean = false,            // the current user disliked the review or not
    val replies: List<String> = emptyList(),          // IDs of replies to this review (parentType = REVIEW)
    val isEdited: Boolean = false,              // wether the review has been edited
    val editedTimestamp: Long? = null,          // timestamp of last edit
    val isSpoiler: Boolean = false,             // wheter review contains spoilder
    val tags: List<String> = emptyList()        // custon tags for review
)