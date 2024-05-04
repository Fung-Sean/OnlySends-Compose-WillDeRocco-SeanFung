package com.example.onlysends_compose.firestore.types

data class MapLocation(
    val userId: String,
    val username: String,
    val profilePictureUrl: String?,
    val locationAddress: String,
    val locationName: String,
    val notes: String,
) {
    // No-argument constructor (able to initialize MapLocation without arguments -> allows Firestore to deserialize MapLocation object)
    constructor() : this("", "", "", "", "", "")
}
