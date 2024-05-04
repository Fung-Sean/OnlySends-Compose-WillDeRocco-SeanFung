package com.example.onlysends_compose.firestore.modules

import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.navigation.NavHostController
import com.example.onlysends_compose.R
import com.example.onlysends_compose.firestore.types.MapLocation
import com.example.onlysends_compose.firestore.types.Post
import com.example.onlysends_compose.firestore.types.User
import com.example.onlysends_compose.ui.maps.new_height.AddHeightUiState
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "Map[sFirestore"



// addHeight : creates MapLocation object for this location
suspend fun addHeight(
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