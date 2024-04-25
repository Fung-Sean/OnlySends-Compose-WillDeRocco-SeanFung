package com.example.onlysends_compose.firestore.modules

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.onlysends_compose.firestore.types.Post
import com.example.onlysends_compose.firestore.types.User
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "PostFirestore"

// createPost : creates a post for a given user
fun createPost(
    db: FirebaseFirestore,
    context: Context?,
    user: User,
    caption: String,
    postPictureUri: String
) {
    val currentTimeMillis = System.currentTimeMillis()

    // define Post object to add to Firestore
    val post = Post(
        userId = user.userId,
        username = user.username,
        profilePictureUrl = user.profilePictureUrl,
        postPictureUrl = postPictureUri,
        caption = caption,
        timestamp = currentTimeMillis
    )

    // Reference to the "posts" collection
    val postsCollection = db.collection("posts")

    // Add the post document to the "posts" collection
    postsCollection.add(post)
        .addOnSuccessListener { documentReference ->
            // Handle success
            Toast.makeText(context, "Created post successfully!", Toast.LENGTH_LONG).show()
            Log.d(TAG, "Created post successfully: $documentReference")
        }
        .addOnFailureListener { e ->
            // Handle failure
            Toast.makeText(context, "Error creating post", Toast.LENGTH_LONG).show()
            Log.d(TAG, "Error creating post: $e")
        }
}