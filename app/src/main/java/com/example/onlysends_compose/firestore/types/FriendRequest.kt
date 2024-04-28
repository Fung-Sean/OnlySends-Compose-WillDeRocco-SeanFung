package com.example.onlysends_compose.firestore.types

data class FriendRequest (
    val friend: User,
    var isOutgoingFriend: Boolean = false,
    var isIncomingFriend: Boolean = false,
) {
    // No-argument constructor (able to initialize User without arguments -> allows Firestore to deserialize User object)
    constructor() : this(User(), false, false)
}
