package com.example.onlysends_compose.firestore.modules

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.util.Log
import android.widget.Toast
import com.example.onlysends_compose.firestore.types.Post
import com.example.onlysends_compose.firestore.types.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.storage
import java.io.ByteArrayOutputStream
import java.util.UUID

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
    postPictureUri: Uri?
) {
    // extract current timestamp
    val currentTimeMillis = System.currentTimeMillis()

    Log.d(TAG, "postPictureUri is $postPictureUri")
    // validation (ensure postPictureUri is valid)
    if (postPictureUri == null) {
        Log.d(TAG, "postPictureUri not selected $postPictureUri")
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