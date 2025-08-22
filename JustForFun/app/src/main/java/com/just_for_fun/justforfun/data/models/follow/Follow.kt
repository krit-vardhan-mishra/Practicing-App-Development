package com.just_for_fun.justforfun.data.models.follow

data class Follow(
    val id: String = "",                     // unique follow relationship ID
    val followerId: String = "",             // ID of user who is following
    val followingId: String = "",            // ID of user being followed
    val followDate: Long = 0L,               // when follow relationship was created
    val isAccepted: Boolean = true,          // for private accounts, whether follow request is accepted
    val requestDate: Long = 0L               // when follow request was sent (for private accounts)
)
