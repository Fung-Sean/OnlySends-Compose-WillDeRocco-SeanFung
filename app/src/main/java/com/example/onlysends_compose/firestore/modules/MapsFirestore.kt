package com.example.onlysends_compose.firestore.modules

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.onlysends_compose.firestore.types.MapLocation
import com.example.onlysends_compose.firestore.types.Post
import com.example.onlysends_compose.firestore.types.User
import com.example.onlysends_compose.ui.maps.new_height.AddHeightUiState
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

private const val TAG = "MapsFirestore"



// addHeight : creates MapLocation object for this location
fun addHeight(
    db: FirebaseFirestore,
    context: Context,
    user: User,
    addHeightUiState: AddHeightUiState,
    onSuccess: () -> Unit
) {
    // Reference to the "heights" collection
    val heightsCollection = db.collection("heights")

    // initialize object to add to db
    val height = MapLocation(
        userId = user.userId,
        username =  user.username,
        profilePictureUrl = user.profilePictureUrl,
        siteLocation = addHeightUiState.siteLocation.value,
        latLng = addHeightUiState.currentLatLng.value,
        siteName = addHeightUiState.siteName.value,
        notes = addHeightUiState.notes.value,
    )

    try {
        // Add the height object to Firestore
        heightsCollection.add(height)
            .addOnSuccessListener {
                // If addition is successful, invoke onSuccess callback
                onSuccess()
            }
            .addOnFailureListener { e ->
                // If addition fails, handle the error (e.g., show a toast)
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    } catch (e: Exception) {
        // Handle any other exceptions
        Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
    }
}

// getHeights : obtains list of MapLocations of user and their friends
suspend fun getHeights(
    db: FirebaseFirestore,
    context: Context,
    user: User,
): SnapshotStateList<MapLocation> {
    // define list of Posts to return
    val locations = mutableStateListOf<MapLocation>()

    try {
        // Get reference to the user document
        val userRef = db.collection("users").document(user.userId)

        // get userSnapshot from db
        val userSnapshot = userRef.get().await()

        // convert to an object
        val userObject = userSnapshot.toObject<User>() ?: User()

        // Reference to the "heights" collection
        val heightsCollection = db.collection("heights")

        // Fetch all posts in the collection
        val querySnapshot = heightsCollection.get().await()

        // Iterate over each document in the query snapshot
        for (document in querySnapshot.documents) {
            // Convert Firestore document to a Post object
            val location = document.toObject<MapLocation>() ?: MapLocation()

            // check if MapLocation userId is either current user OR friend
            if (location.userId == userObject.userId || userObject.friends.contains(location.userId)) {
                // Add the MapLocation object to the list of posts
                locations.add(location)
            }
        }
    } catch (e: Exception) {
        // Handle any potential errors, such as network issues or Firestore exceptions
        Toast.makeText(context, "Error fetching MapLocations", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }

    Log.d(TAG,"Finished finding MapLocations: ${locations.toList()}")
    return locations
}