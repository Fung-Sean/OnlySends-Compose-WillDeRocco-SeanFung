package com.example.onlysends_compose.firestore.types

data class Friend(
    val userId: String,
    val username: String,
    val profilePictureUrl: String?,
    // MAYBE??? include the outgoingFriends so we can quickly (prob not tho tbh -> too confusing)
    val climbingStyle: String = "",
    val numFriends: Int = 0,
)
