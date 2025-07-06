package com.just_for_fun.justforfun.data.models

data class Reply(
    val id: String,                        // unique ID of the reply
    val userId: String,                    // ID of the user who wrote the reply
    val parentId: String,                  // ID of the review or reply this reply is attached to
    val parentType: ParentType,            // Indicates whether it's a reply to a REVIEW or another REPLY
    val userProfilePhoto: String,          // profile photo of the replying user
    val userName: String,                  // name of the replying user
    val replyText: String,                 // content of the reply
    val timestamp: Long,                   // timestamp of the reply
    val likeCount: Int = 0,                // total likes
    val dislikeCount: Int = 0,             // total dislikes
    val isLiked: Boolean = false,            // whether the current user has liked this reply
    val isDisliked: Boolean = false,         // whether the current user has disliked this reply
    val replies: List<String> = emptyList(),   // list of reply IDs that are replies to this reply
    val isEdited: Boolean = false,              // whether the reply has been edited
    val editedTimestamp: Long? = null           // timestamp of last edit
)
//  a reply will also be review of someone's review or reply