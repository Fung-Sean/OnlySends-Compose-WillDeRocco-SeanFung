package com.example.onlysends_compose.firestore.types

import com.example.onlysends_compose.ui.sign_in.UserData

data class User(
    val userId: String,
    val username: String?,
    val profilePictureUrl: String?,
    val friends: List<Friend> = listOf(),
    val outgoingFriends: List<FriendRequest> = listOf(),
    val incomingFriends: List<FriendRequest> = listOf(),
    val posts: List<Post> = listOf(),
    val favoriteMaps: List<GoogleMap> = listOf(),
    val climbingStyle: String = "",
    val numFollowers: Int = 0
) {
    // No-argument constructor (able to initialize User without arguments -> allows Firestore to deserialize User object)
    constructor() : this("", "", "", listOf(), listOf(), listOf(), listOf(), listOf(), "", 0)

    companion object {
        fun fromUserData(userData: UserData): User {
            return User(
                userId = userData.userId,
                username = userData.username,
                profilePictureUrl = userData.profilePictureUrl
            )
        }
    }
}
