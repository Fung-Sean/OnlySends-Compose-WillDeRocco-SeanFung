package com.example.onlysends_compose.firestore.modules

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavHostController
import com.example.onlysends_compose.R
import com.example.onlysends_compose.firestore.types.Post
import com.example.onlysends_compose.firestore.types.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import com.google.firebase.storage.storage
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.UUID
import kotlin.reflect.KFunction1

private const val TAG = "PostFirestore"

// Define a callback interface to return the URI (NOTE: ChatGPT helped me create this function)
interface UploadCallback {
    fun onUploadSuccess(uri: String)
    fun onUploadFailure(exception: Exception)
}

// uploadToCloudStorage : helper function to createPost (uploads postPictureUrl to cloud and returns link)
fun uploadToCloudStorage(
    context: Context?,
    postPictureUri: Uri,
    callback: UploadCallback
) {
    // Convert URI to Bitmap
    val inputStream = context?.contentResolver?.openInputStream(postPictureUri)
    val bitmap = BitmapFactory.decodeStream(inputStream)

    // Upload Bitmap to Cloud Storage
    val storageRef = Firebase.storage.reference
    val imageRef = storageRef.child("images/${UUID.randomUUID()}.jpg")
    val baos = ByteArrayOutputStream()
    bitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
    val data = baos.toByteArray()
    val uploadTask = imageRef.putBytes(data)

    // Listen for the success/failure of the upload task
    uploadTask.addOnSuccessListener { taskSnapshot ->
        // Image uploaded successfully, get the URL
        imageRef.downloadUrl.addOnSuccessListener { uri ->
            Log.d(TAG, "Successfully uploaded to cloud storage: $uri")
            callback.onUploadSuccess(uri.toString()) // Pass URI to the callback
        }
    }.addOnFailureListener { exception ->
        // Handle unsuccessful uploads
        callback.onUploadFailure(exception) // Pass exception to the callback
        Toast.makeText(context, "Error uploading image (Cloud Storage)", Toast.LENGTH_LONG).show()
        Log.d(TAG, "Error uploading image: $exception")
    }
}

// createPost : creates a post for a given user
fun createPost(
    db: FirebaseFirestore,
    context: Context?,
    user: User,
    caption: String,
    postPictureUri: Uri?,
    navController: NavHostController,
) {
    // perform context validation
    if (context == null) {
        Log.d(TAG, "context must be defined")
        return
    }

    // extract current timestamp
    val currentTimeMillis = System.currentTimeMillis()

    // validation (ensure postPictureUri is valid)
    if (postPictureUri == null) {
        Log.d(TAG, "postPictureUri not selected")
        Toast.makeText(context, "Select a photo to post", Toast.LENGTH_LONG).show()
        return
    }

    // Call uploadToCloudStorage and handle the URI in the callback
    uploadToCloudStorage(context, postPictureUri, object : UploadCallback {
        override fun onUploadSuccess(uri: String) {
            // Define Post object to add to Firestore
            val post = Post(
                userId = user.userId,
                username = user.username,
                profilePictureUrl = user.profilePictureUrl,
                postPictureUrl = uri,
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

                    // navigate to home page
                    navController.navigate(context.getString(R.string.home))
                }
                .addOnFailureListener { e ->
                    // Handle failure
                    Toast.makeText(context, "Error creating post", Toast.LENGTH_LONG).show()
                    Log.d(TAG, "Error creating post: $e")
                }
        }

        override fun onUploadFailure(exception: Exception) {
            // Handle failure
            Toast.makeText(context, "Error uploading image (Cloud Storage)", Toast.LENGTH_LONG).show()
            Log.d(TAG, "Error uploading image: $exception")
        }

    })
}

// getFriendPosts : returns a list of posts for a user's friends (and the user)
suspend fun getFriendPosts(
    db: FirebaseFirestore,
    context: Context,
    user: User,
): SnapshotStateList<Post> {
    // define list of Posts to return
    val posts = mutableStateListOf<Post>()

    try {
        // Reference to the "posts" collection
        val postsCollection = db.collection("posts")

        // Fetch all posts in the collection
        val querySnapshot = postsCollection.get().await()

        // Iterate over each document in the query snapshot
        for (document in querySnapshot.documents) {
            // Convert Firestore document to a Post object
            val post = document.toObject<Post>() ?: Post()

            // check if post userId is either current user OR friend
            if (post.userId == user.userId || user.friends.contains(post.userId)) {
                // Add the Post object to the list of posts
                posts.add(post)
            }
        }
    } catch (e: Exception) {
        // Handle any potential errors, such as network issues or Firestore exceptions
        Toast.makeText(context, "Error fetching posts", Toast.LENGTH_LONG).show()
        e.printStackTrace()
    }

    Log.d(TAG,"Finished finding posts: $posts")
    return posts
}
