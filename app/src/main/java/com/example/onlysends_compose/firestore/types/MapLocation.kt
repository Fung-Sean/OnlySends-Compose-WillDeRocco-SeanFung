package com.example.onlysends_compose.firestore.types

import com.google.firebase.firestore.GeoPoint

data class MapLocation(
    val userId: String,
    val username: String,
    val profilePictureUrl: String?,
    val siteLocation: String,
    val latLng: GeoPoint,
    val siteName: String,
    val notes: String,
) {
    // No-argument constructor (able to initialize MapLocation without arguments -> allows Firestore to deserialize MapLocation object)
    constructor() : this("", "", "", "", GeoPoint(0.0, 0.0), "", "")
}
