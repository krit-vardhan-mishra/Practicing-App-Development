package com.just_for_fun.justforfun.data.models.user.review

// Represents a reply to a review or another reply in the app
data class Reply(
    val id: String = "",                     // unique ID of the reply
    val userId: String = "",                 // ID of the user who wrote the reply
    val parentId: String = "",               // ID of the review or reply this reply is attached to
    val parentType: ParentType = ParentType.REVIEW, // Indicates whether it's a reply to a REVIEW or another REPLY
    val userProfilePhoto: String = "",       // profile photo of the replying user
    val userName: String = "",               // name of the replying user
    val replyText: String = "",              // content of the reply
    val timestamp: Long = 0L,                // timestamp of the reply
    val lastModified: Long = 0L,             // timestamp when last modified
    val likes: List<String> = emptyList(),   // User IDs who liked this reply
    val likesCount: Int = 0,                 // cached count of likes
    val dislikes: List<String> = emptyList(), // User IDs who disliked this reply
    val dislikesCount: Int = 0,              // cached count of dislikes
    val replies: List<String> = emptyList(), // list of reply IDs that are replies to this reply
    val isEdited: Boolean = false,           // whether the reply has been edited
    val isSpoiler: Boolean = false,          // whether the reply contains spoilers
    val isVerified: Boolean = false          // whether the replying user is verified
)