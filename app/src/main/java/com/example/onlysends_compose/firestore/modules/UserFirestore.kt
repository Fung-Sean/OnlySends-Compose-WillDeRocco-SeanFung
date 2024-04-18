package com.example.onlysends_compose.firestore.modules

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.User
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "UserFirestore"

// createUserDocument : creates and updates user (calls onUpdateUser when successful)
fun createUserDocument(
    db: FirebaseFirestore,
    user: User,
    onUpdateUser: (User) -> Unit
) {
    Log.d(TAG, "creating user document: $user")

    val userRef = db.collection("users").document(user.userId)
    userRef.get()
        .addOnSuccessListener { document ->
            if (!document.exists()) {
                val userData = hashMapOf(
                    "userId" to user.userId,
                    "username" to user.username,
                    "profilePictureUrl" to user.profilePictureUrl,
                    "friends" to user.friends,
                    "outgoingFriends" to user.outgoingFriends,
                    "incomingFriends" to user.incomingFriends,
                    "posts" to user.posts,
                    "favoriteMaps" to user.favoriteMaps,
                    "climbingStyle" to user.climbingStyle,
                    "numFollowers" to user.numFriends
                    // Add other user data as needed
                )

                userRef.set(userData)
                    .addOnSuccessListener {
                        Log.d(TAG, "User document created successfully")
                    }
                    .addOnFailureListener { e ->
                        Log.w(TAG, "Error adding document", e)

                    }
            } else {
                // update `user` object according to database
                // User exists in Firestore, update the local user object
                val updatedUser = document.toObject(User::class.java)
                if (updatedUser != null) {
                    onUpdateUser(updatedUser)
                }
                Log.d(TAG, "user already exists in db: $updatedUser")
            }
        }
        .addOnFailureListener { exception ->
            Log.d(TAG, "get failed with ", exception)
        }
}

// updateUserProfile : updates the user with `newUsername` and `newClimbStyle`
fun updateUserProfile(
    db: FirebaseFirestore,
    context: Context,
    userId: String,
    newUsername: String,
    newClimbStyle: String,
    onUpdateUser: (User) -> Unit
) {        // Get reference to the user document
    val userRef = db.collection("users").document(userId)

    // Update the fields
    val updates = hashMapOf<String, Any>(
        "username" to newUsername,
        "climbingStyle" to newClimbStyle
    )

    // Perform the update
    userRef.update(updates)
        .addOnSuccessListener {
            Log.d(TAG, "User profile updated successfully")

            // Retrieve the updated user document and invoke the onUpdateUser callback
            userRef.get()
                .addOnSuccessListener { documentSnapshot ->
                    val updatedUser = documentSnapshot.toObject(User::class.java)
                    if (updatedUser != null) {
                        // update `user` state
                        onUpdateUser(updatedUser)
                        // Display toast message
                        Toast.makeText(context, "User profile updated successfully", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e(TAG, "Error retrieving updated user document", exception)
                    // Display toast message
                    Toast.makeText(context, "Internal error updating profile", Toast.LENGTH_SHORT).show()
                }
        }
        .addOnFailureListener { exception ->
            Log.e(TAG, "Error updating user profile", exception)
        }
}