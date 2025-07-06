package com.just_for_fun.justforfun.data.models

data class UserPreferences(
    val notificationsEnable: Boolean = true,
    val emailNotifications: Boolean = true,
    val pushNotifications: Boolean = true,
    val privateProfile: Boolean = false,
    val showWatchHistory: Boolean = true,
    val showRatings: Boolean = true,
    val showReviews: Boolean = true,
    val allowFollowRequests: Boolean = true,
    val showOnlineStatus: Boolean = true,
    val language: String = "en",
    val theme: String = "system"
)