package com.example.onlysends_compose.firestore.types

import com.example.onlysends_compose.ui.sign_in.UserData

data class User(
    val userId: String,
    val username: String,
    val profilePictureUrl: String?,
    val friends: List<String> = listOf(),
    val outgoingFriends: List<String> = listOf(),
    val incomingFriends: List<String> = listOf(),
    val posts: List<Post> = listOf(),
    val favoriteMaps: List<GoogleMap> = listOf(),
    val climbingStyle: String = "",
    val numFriends: Int = 0
) {
    // No-argument constructor (able to initialize User without arguments -> allows Firestore to deserialize User object)
    constructor() : this("", "", "", listOf(), listOf(), listOf(), listOf(), listOf(), "", 0)
}
