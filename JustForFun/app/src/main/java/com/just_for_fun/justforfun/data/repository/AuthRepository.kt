package com.just_for_fun.justforfun.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val auth: FirebaseAuth = FirebaseAuth.getInstance(),
    private val db: FirebaseFirestore = FirebaseFirestore.getInstance()
) {
    suspend fun signUpWithEmail(
        name: String,
        username: String,
        email: String,
        password: String
    ): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        val uid = result.user?.uid ?: throw IllegalStateException("User creation failed")

        // Reserve username atomically to keep it unique
        db.runTransaction { tr ->
            val unameDoc = db.collection("usernames").document(username)
            val snapshot = tr.get(unameDoc)
            if (snapshot.exists()) throw IllegalStateException("Username already taken")
            tr.set(unameDoc, mapOf("uid" to uid))
        }.await()

        val now = System.currentTimeMillis()
        val userDoc = mapOf(
            "id" to uid,
            "name" to name,
            "email" to email,
            "username" to username,
            "bio" to "",
            "profilePicture" to "",
            "joinDate" to now,
            "location" to null,
            "website" to null,
            "following" to emptyList<String>(),
            "followers" to emptyList<String>(),
            "favourites" to emptyList<String>(),
            "watchList" to emptyList<String>(),
            "reviews" to emptyList<String>(),
            "ratings" to emptyList<String>(),
            "likedReviews" to emptyList<String>(),
            "likedReplies" to emptyList<String>(),
            "replies" to emptyList<Map<String, Any?>>(),
            "watchHistory" to emptyList<String>(),
            "socialLinks" to emptyMap<String, String>(),
            "topGenres" to emptyList<String>(),
            "customListMovies" to emptyList<String>(),
            "customShowMovies" to emptyList<String>(),
            "blockedUsers" to emptyList<String>(),
            "preferences" to mapOf(
                "notificationsEnable" to true,
                "emailNotifications" to true,
                "pushNotifications" to true,
                "privateProfile" to false,
                "showWatchHistory" to true,
                "showRatings" to true,
                "showReviews" to true,
                "allowFollowRequests" to true,
                "showOnlineStatus" to true,
                "language" to "en",
                "theme" to "system"
            ),
            "isPrivate" to false,
            "isVerified" to false,
            "lastActivateDate" to now,
            "totalReviews" to 0,
            "totalRatings" to 0,
            "averageRating" to 0.0
        )

        db.collection("users").document(uid).set(userDoc).await()
        // Attach a claim time to help with analytics or future
        auth.currentUser?.updateProfile(null)
        db.collection("meta").document("users").set(
            mapOf("count" to FieldValue.increment(1))
        , com.google.firebase.firestore.SetOptions.merge()).await()
        return uid
    }

    suspend fun signInWithEmail(email: String, password: String): String {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw IllegalStateException("Sign in failed")
    }

    fun currentUserId(): String? = auth.currentUser?.uid

    fun isSignedIn(): Boolean = auth.currentUser != null

    fun signOut() {
        auth.signOut()
    }
}

