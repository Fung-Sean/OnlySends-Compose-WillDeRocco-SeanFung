package com.example.onlysends_compose.firestore.modules

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.onlysends_compose.firestore.types.User
import com.google.firebase.firestore.FirebaseFirestore

private const val TAG = "FriendFirestore"

// followFriend : adds friend to `outgoingFriends` for user and `incomingFriends` for friend
fun followFriend(
    db: FirebaseFirestore,
    context: Context,
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

                // Variable to track whether user is already in incomingFriends list
                var userAlreadyInIncomingFriends = false

                // Convert incomingFriends list to a list of Friend objects
                val incomingFriends = incomingFriendsList.map { friendData ->
                    if (user.userId == friendData["userId"] as String) {
                        // Set flag indicating user is already in incomingFriends list
                        userAlreadyInIncomingFriends = true
                        return@map null // Skip mapping this friend
                    }
                    Friend(
                        userId = friendData["userId"] as String,
                        username = friendData["username"] as String,
                        profilePictureUrl = friendData["profilePictureUrl"] as? String,
                        climbingStyle = friendData["climbingStyle"] as String,
                        numFriends = (friendData["numFriends"] as Long).toInt()
                    )
                }.filterNotNull() // Filter out null values

                // If user is already in incomingFriends list, exit function
                if (userAlreadyInIncomingFriends) {
                    Log.d(TAG, "user already inside of incomingFriends list")
                    return@addOnSuccessListener
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

                                Toast.makeText(context, "Added friend successfully", Toast.LENGTH_SHORT).show()

                                // Update local user object with the new outgoingFriends list
                                val updatedUser = user.copy(outgoingFriends = updatedUserOutgoingFriends)
                                onUpdateUser(updatedUser)
                            }
                            .addOnFailureListener { exception ->
                                // Failed to update user document
                                Toast.makeText(context, "Error adding friend", Toast.LENGTH_SHORT).show()
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

// acceptFriend : two stage process
// 1) add user to friendUserRef.friends
// 2) adds friend to userRef.friends

// this was the old approach below (let's simplify it to the one above ^^)
// 1) a) adds user to friendUserRef.friends b) deletes `user` from `friendUserRef.outgoingFriends`
// 2) c) adds friend to userRef.friends d) deletes `friend` from `userRef.incomingFriends`
fun acceptFriend(
    db: FirebaseFirestore,
    context: Context,
    user: User,
    friend: Friend,
    onUpdateUser: (User) -> Unit
) {
    // Get references to the user and friend documents
    val userRef = db.collection("users").document(user.userId)
    val friendRef = db.collection("users").document(friend.userId)

    // Stage 1 (updating friendRef)
    // Fetch the friend document from Firestore
    friendRef.get()
        .addOnSuccessListener { friendDoc ->
            if (friendDoc.exists()) {
                // STEP 1) adds user to friendUserRef.friends
                // Extract the incomingFriends list from the friend document
                val friendRefFriends =
                    friendDoc.get("friends") as? List<Map<String, Any>> ?: emptyList()

                var userAlreadyInFriendRefFriends = false

                // Convert friendRef.friends list to a list of Friend objects
                val updatedFriendRefFriends = friendRefFriends.map { friendData ->
                    if (user.userId == friendData["userId"] as String) {
                        // Set flag indicating user is already in friends list
                        userAlreadyInFriendRefFriends = true
                        return@map null // Skip mapping this friend
                    }
                    Friend(
                        userId = friendData["userId"] as String,
                        username = friendData["username"] as String,
                        profilePictureUrl = friendData["profilePictureUrl"] as? String,
                        climbingStyle = friendData["climbingStyle"] as String,
                        numFriends = (friendData["numFriends"] as Long).toInt()
                    )
                }.filterNotNull() // Filter out null values

                // If user is already in incomingFriends list, exit function
                if (userAlreadyInFriendRefFriends) {
                    Log.d(TAG, "user already inside of friends list of friend:  $friend")
                    return@addOnSuccessListener
                }


                val userFriend = Friend(
                    userId = user.userId,
                    username = user.username,
                    profilePictureUrl = user.profilePictureUrl,
                    climbingStyle = user.climbingStyle,
                    numFriends = user.numFriends
                )

                // LAST STEP of 1) Update the friend document
                val friendUpdates = hashMapOf<String, Any>(
                    "friends" to updatedFriendRefFriends + userFriend
                )

                // Update the friend document in Firestore
                friendRef.update(friendUpdates)
                    .addOnSuccessListener {
                        // Successfully updated friend document
                        Log.d(TAG, "Friend document updated successfully")

                        /*--------------------------------------------------------------------*/
                        // Stage 2) (updating userRef)
                        // Step 2) adds friend to userRef.friends
                        userRef.get()
                            .addOnSuccessListener { userDoc ->
                                val userRefFriends =
                                    userDoc.get("friends") as? List<Map<String, Any>> ?: emptyList()

                                var friendAlreadyInUserRefFriends = false

                                // Convert friendRef.friends list to a list of Friend objects
                                val updatedUserRefFriends = userRefFriends.map { friendData ->
                                    if (friend.userId == friendData["userId"] as String) {
                                        // Set flag indicating user is already in friends list
                                        friendAlreadyInUserRefFriends = true
                                        return@map null // Skip mapping this friend
                                    }
                                    Friend(
                                        userId = friendData["userId"] as String,
                                        username = friendData["username"] as String,
                                        profilePictureUrl = friendData["profilePictureUrl"] as? String,
                                        climbingStyle = friendData["climbingStyle"] as String,
                                        numFriends = (friendData["numFriends"] as Long).toInt()
                                    )
                                }.filterNotNull() // Filter out null values

                                // If user is already in incomingFriends list, exit function
                                if (friendAlreadyInUserRefFriends) {
                                    Log.d(TAG, "user already inside of friends list of friend:  $friend")
                                    return@addOnSuccessListener
                                }

                                val userRefFriend = Friend(
                                    userId = friend.userId,
                                    username = friend.username,
                                    profilePictureUrl = friend.profilePictureUrl,
                                    climbingStyle = friend.climbingStyle,
                                    numFriends = friend.numFriends
                                )

                                // LAST STEP: Update the user document
                                val userUpdates = hashMapOf<String, Any>(
                                    "friends" to updatedUserRefFriends + userRefFriend
                                )
                                // Update the user document in Firestore
                                userRef.update(userUpdates)
                                    .addOnSuccessListener {
                                        // Successfully updated user document
                                        Log.d(TAG, "User document updated successfully")

                                        Toast.makeText(context, "Accepted friend successfully", Toast.LENGTH_SHORT).show()

                                        // Update local user object with the new friends list
                                        val updatedUser = user.copy(friends = updatedUserRefFriends + userRefFriend)
                                        onUpdateUser(updatedUser)

                                    }
                                    .addOnFailureListener { exception ->
                                        // Failed to update user document
                                        Toast.makeText(context, "Error adding friend", Toast.LENGTH_SHORT).show()
                                        Log.e(TAG, "Error updating user document", exception)
                                    }
                                /*--------------------------------------------------------------------*/
                            }
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