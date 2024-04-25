package com.example.onlysends_compose.firestore.types

data class Post(
    val userId: String,
    val username: String,
    val profilePictureUrl: String?,
    val postPictureUrl: String,
    val caption: String,
    val timestamp: Long
) {
    // No-argument constructor (able to initialize Post without arguments -> allows Firestore to deserialize Post object)
    constructor() : this("", "", "", "", "", 0)
}