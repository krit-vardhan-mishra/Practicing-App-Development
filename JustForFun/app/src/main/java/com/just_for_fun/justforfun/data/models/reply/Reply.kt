package com.just_for_fun.justforfun.data.models.reply

data class Reply(
    val id: String = "",                     // unique reply ID
    val reviewId: String = "",               // ID of review this reply belongs to
    val userId: String = "",                 // ID of user who wrote the reply
    val content: String = "",                // reply content
    val timestamp: Long = 0L,                // when reply was created
    val lastModified: Long = 0L,             // when reply was last edited
    val likes: List<String> = emptyList(),   // list of user IDs who liked this reply
    val likesCount: Int = 0,                 // cached count of likes
    val isEdited: Boolean = false,           // whether reply has been edited
    val parentReplyId: String? = null        // for nested replies (reply to a reply)
)
