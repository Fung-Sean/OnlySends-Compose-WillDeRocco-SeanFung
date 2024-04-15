package com.example.onlysends_compose.firestore

import android.util.Log
import com.example.onlysends_compose.firestore.types.User
import com.example.onlysends_compose.ui.sign_in.UserData
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

object Firestore {
    private const val TAG = "Firestore"

    fun createUserDocument(db: FirebaseFirestore, user: User) {
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
                        "numFollowers" to user.numFollowers
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
                    Log.d(TAG, "user already exists in db")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }
    }

    // Define other Firestore functions here
}
