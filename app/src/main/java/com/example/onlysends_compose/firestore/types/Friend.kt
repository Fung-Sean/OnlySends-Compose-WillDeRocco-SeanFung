package com.example.onlysends_compose.firestore.types

data class Friend(
    val userId: String,
    val username: String,
    val profilePictureUrl: String?,
    // MAYBE??? include the outgoingFriends so we can quickly (prob not tho tbh -> too confusing)
    val climbingStyle: String = "",
    val numFriends: Int = 0,
){
    // No-argument constructor (able to initialize User without arguments -> allows Firestore to deserialize User object)

    constructor() : this("", "", "", "", 0)

}
