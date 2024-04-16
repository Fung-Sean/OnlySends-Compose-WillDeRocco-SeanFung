package com.example.onlysends_compose.firestore

import android.content.Context
import android.icu.util.Freezable
import android.util.Log
import android.widget.Toast
import com.example.onlysends_compose.firestore.types.Friend
import com.example.onlysends_compose.firestore.types.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

object Firestore {
    private const val TAG = "Firestore"

    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    // createUserDocument : creates and updates user (calls onUpdateUser when successful)
    fun createUserDocument(
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


    // userToFriend : function to convert a User document to a Friend object
    private fun userToFriend(userDoc: DocumentSnapshot): Friend {
        return Friend(
            userId = userDoc.id,
            username = userDoc.getString("username") ?: "",
            profilePictureUrl = userDoc.getString("profilePictureUrl"),
            climbingStyle = userDoc.getString("climbingStyle") ?: "",
            numFriends = (userDoc.get("friends") as? List<*>)?.size ?: 0
        )
    }

    // searchAllFriends : returns a list of Friend objects for all POTENTIAL friends
    fun searchAllFriends(user: User, onFriendsLoaded: (List<Friend>) -> Unit) {
        val usersCollection = db.collection("users")

        usersCollection.get()
            .addOnSuccessListener { querySnapshot ->
                val friendsList = mutableListOf<Friend>()

                for (document in querySnapshot.documents) {
                    // TO-DO: filter out user and current friends
                    val friend = userToFriend(document)
                    friendsList.add(friend)
                }

                onFriendsLoaded(friendsList)
            }
            .addOnFailureListener { exception ->
                Log.e(TAG, "Error searching all friends", exception)
                onFriendsLoaded(emptyList()) // Return empty list in case of failure
            }
    }


    // searchUserFriends : returns a list of Friend objects for the current USER


    // followFriend : adds friend to `outgoingFriends` for user and `incomingFriends` for friend
    fun followFriend(
        user: User,
        friend: Friend,
        onUpdateUser: (User) -> Unit
    ) {
        // Get references to the user and friend documents
        val userRef = db.collection("users").document(user.userId)
        val friendRef = db.collection("users").document(friend.userId)

        // Fetch the friend document from Firestore
        friendRef.get()
            .addOnSuccessListener { friendDoc ->
                if (friendDoc.exists()) {
                    // Extract the incomingFriends list from the friend document
                    val incomingFriendsList =
                        friendDoc.get("incomingFriends") as? List<Map<String, Any>> ?: emptyList()

                    // Convert incomingFriends list to a list of Friend objects
                    val incomingFriends = incomingFriendsList.map { friendData ->
                        Friend(
                            userId = friendData["userId"] as String,
                            username = friendData["username"] as String,
                            profilePictureUrl = friendData["profilePictureUrl"] as? String,
                            climbingStyle = friendData["climbingStyle"] as String,
                            numFriends = friendData["numFriends"] as Int
                        )
                    }

                    val userFriend = Friend(
                        userId = user.userId,
                        username = user.username,
                        profilePictureUrl = user.profilePictureUrl,
                        climbingStyle = user.climbingStyle,
                        numFriends = user.numFriends
                    )

                    // Add user as an incoming friend for the friend
                    val friendUpdates = hashMapOf<String, Any>(
                        "incomingFriends" to incomingFriends + userFriend
                    )

                    // Update the friend document in Firestore
                    friendRef.update(friendUpdates)
                        .addOnSuccessListener {
                            // Successfully updated friend document
                            Log.d(TAG, "Friend document updated successfully")

                            /*--------------------------------------------------------------------*/
                            // Now, update the user's outgoingFriends list
                            // Now, update the user's outgoingFriends list
                            val updatedUserOutgoingFriends = user.outgoingFriends + friend
                            val userUpdates = hashMapOf<String, Any>(
                                "outgoingFriends" to updatedUserOutgoingFriends
                            )

                            // Update the user document in Firestore
                            userRef.update(userUpdates)
                                .addOnSuccessListener {
                                    // Successfully updated user document
                                    Log.d(TAG, "User document updated successfully")

                                    // Update local user object with the new outgoingFriends list
                                    val updatedUser = user.copy(outgoingFriends = updatedUserOutgoingFriends)
                                    onUpdateUser(updatedUser)
                                }
                                .addOnFailureListener { exception ->
                                    // Failed to update user document
                                    Log.e(TAG, "Error updating user document", exception)
                                }
                            /*--------------------------------------------------------------------*/
                        }
                        .addOnFailureListener { exception ->
                            // Failed to update friend document
                            Log.e(TAG, "Error updating friend document", exception)
                        }
                } else {
                    // Friend document doesn't exist
                    Log.e(TAG, "Friend document does not exist")
                }
            }
            .addOnFailureListener { exception ->
                // Error fetching friend document
                Log.e(TAG, "Error fetching friend document", exception)
            }
    }
}
