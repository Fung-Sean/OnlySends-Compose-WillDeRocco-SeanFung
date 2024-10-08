package com.example.onlysends_compose.firestore.modules

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.onlysends_compose.firestore.types.User
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.tasks.await

private const val TAG = "FriendFirestore"

// followFriend : adds friend to `outgoingFriends` for user and `incomingFriends` for friend
fun followFriend(
    db: FirebaseFirestore,
    context: Context,
    user: User,
    friend: User,
    onSuccess: () -> Unit
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
                    friendDoc.get("incomingFriends") as? List<String> ?: emptyList()

                // Variable to track whether user is already in incomingFriends list
                var userAlreadyInIncomingFriends = false

                // Convert incomingFriends list to a list of friend userId strings
                val incomingFriends = incomingFriendsList.map { friendId ->
                    if (user.userId == friendId) {
                        // Set flag indicating user is already in incomingFriends list
                        userAlreadyInIncomingFriends = true
                        return@map null // Skip mapping this friend
                    }
                    friendId
                }.filterNotNull() // Filter out null values

                // If user is already in incomingFriends list, exit function
                if (userAlreadyInIncomingFriends) {
                    Log.d(TAG, "user already inside of incomingFriends list")
                    return@addOnSuccessListener
                }

                // Add user as an incoming friend for the friend
                val friendUpdates = hashMapOf<String, Any>(
                    "incomingFriends" to incomingFriends + user.userId
                )

                // Update the friend document in Firestore
                friendRef.update(friendUpdates)
                    .addOnSuccessListener {
                        // Successfully updated friend document
                        Log.d(TAG, "Friend document updated successfully")

                        /*--------------------------------------------------------------------*/
                        // Now, update the user's outgoingFriends list
                        val updatedUserOutgoingFriends = user.outgoingFriends + friend.userId
                        val userUpdates = hashMapOf<String, Any>(
                            "outgoingFriends" to updatedUserOutgoingFriends
                        )

                        // Update the user document in Firestore
                        userRef.update(userUpdates)
                            .addOnSuccessListener {
                                // Successfully updated user document
                                Log.d(TAG, "User document updated successfully")

                                Toast.makeText(context, "Added friend successfully", Toast.LENGTH_SHORT).show()

//                                // Update local user object with the new outgoingFriends list
//                                val updatedUser = user.copy(outgoingFriends = updatedUserOutgoingFriends)

                                // SUCCESS: invoke function call to re-render users
                                onSuccess()
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
                        Toast.makeText(context, "Error adding friend", Toast.LENGTH_SHORT).show()
                        Log.e(TAG, "Error updating friend document", exception)
                    }
            } else {
                // Friend document doesn't exist
                Toast.makeText(context, "Error adding friend", Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Friend document does not exist")
            }
        }
        .addOnFailureListener { exception ->
            // Error fetching friend document
            Toast.makeText(context, "Error adding friend", Toast.LENGTH_SHORT).show()
            Log.e(TAG, "Error fetching friend document", exception)
        }
}

// acceptFriend : two stage process
// 1) add user to `friendUserRef.friends` AND remove user from `friendUserRef.outgoingFriends`
// 2) adds friend to `userRef.friends` AND remove friend from `userRef.incomingFriends`
fun acceptFriend(
    db: FirebaseFirestore,
    context: Context,
    user: User,
    friend: User,
    onSuccess: () -> Unit
) {
    // Get references to the user and friend documents
    val userRef = db.collection("users").document(user.userId)
    val friendRef = db.collection("users").document(friend.userId)

    // Stage 1 (updating friendRef)
    // Fetch the friend document from Firestore
    friendRef.get()
        .addOnSuccessListener { friendDoc ->
            if (friendDoc.exists()) {
                // extract user object from friendDoc
                val friendObject = friendDoc.toObject<User>() ?: User()

                // extract friends and outgoingFriends
                val friendRefFriends = friendObject.friends
                val friendRefOutgoing = friendObject.outgoingFriends

                // STEP 1a) adds user to friendUserRef.friends
                var userAlreadyInFriendRefFriends = false

                // Convert friendRef.friends list to a list of Friend objects
                val updatedFriendRefFriends = friendRefFriends.map { friendId ->
                    if (user.userId == friendId) {
                        // Set flag indicating user is already in friends list
                        userAlreadyInFriendRefFriends = true
                        return@map null // Skip mapping this friend
                    }
                    friendId
                }.filterNotNull() // Filter out null values

                // If user is already in incomingFriends list, exit function
                if (userAlreadyInFriendRefFriends) {
                    Log.d(TAG, "user already inside of friends list of friend:  $friend")
                    return@addOnSuccessListener
                }

                // STEP 1b) remove user from friendsUserRef.outgoingFriends
                val updatedFriendRefOutgoing = friendRefOutgoing.map { friendId ->
                    if (user.userId == friendId) {
                        return@map null // Skip mapping this friend
                    }
                    friendId
                }.filterNotNull() // Filter out null values

                // LAST STEP of 1) Update the friend document
                val friendUpdates = hashMapOf<String, Any>(
                    "friends" to updatedFriendRefFriends + user.userId,
                    "outgoingFriends" to updatedFriendRefOutgoing,
                    "numFriends" to friendObject.numFriends + 1
                )

                // Update the friend document in Firestore
                friendRef.update(friendUpdates)
                    .addOnSuccessListener {
                        // Successfully updated friend document
                        Log.d(TAG, "Friend document updated successfully")

                        /*--------------------------------------------------------------------*/
                        // Stage 2) (updating userRef)
                        // Step 2a) adds friend to userRef.friends
                        // Get references to the user and friend documents
                        userRef.get()
                            .addOnSuccessListener { userDoc ->
                                if (userDoc.exists()) {
                                    // extract user object from friendDoc
                                    val userObject = userDoc.toObject<User>() ?: User()

                                    // extract friends and outgoingFriends
                                    val userRefFriends = userObject.friends
                                    val userRefIncoming = userObject.incomingFriends

                                    // STEP 2a) adds user to friendUserRef.friends
                                    var friendAlreadyInUserRefFriends = false

                                    // Obtain all valid userRef friends
                                    val updatedUserRefFriends = userRefFriends.map { friendId ->
                                        if (friend.userId == friendId) {
                                            // Set flag indicating user is already in friends list
                                            friendAlreadyInUserRefFriends = true
                                            return@map null // Skip mapping this friend
                                        }
                                        friendId
                                    }.filterNotNull() // Filter out null values

                                    // If friend is already in user.friends list, exit function
                                    if (friendAlreadyInUserRefFriends) {
                                        Log.d(TAG, "friend already inside of friends list of -> user:  $user || friend: $friend")
                                        return@addOnSuccessListener
                                    }

                                    // STEP 2b) remove friend from userRef.incoming
                                    val updatedUserRefIncoming = userRefIncoming.map { friendId ->
                                        if (friend.userId == friendId) {
                                            return@map null // Skip mapping this friend
                                        }
                                        friendId
                                    }.filterNotNull() // Filter out null values

                                    // LAST STEP of 2) Update the friend document
                                    val userUpdates = hashMapOf<String, Any>(
                                        "friends" to updatedUserRefFriends + friend.userId,
                                        "incomingFriends" to updatedUserRefIncoming,
                                        "numFriends" to userObject.numFriends + 1
                                    )

                                    // Update the friend document in Firestore
                                    userRef.update(userUpdates)
                                        .addOnSuccessListener {
                                            // Successfully updated friend document
                                            Log.d(TAG, "User document updated successfully (accepted friend: $friend)")
                                            // Update local user object with the new outgoingFriends list
//                                            val updatedUser = user.copy(
//                                                friends = updatedUserRefFriends + friend.userId,
//                                                incomingFriends = updatedUserRefIncoming,
//                                                numFriends = userObject.numFriends + 1,
//                                            )

                                            // display Toast for user
                                            Toast.makeText(context, "Accepted friend successfully", Toast.LENGTH_SHORT).show()

                                            // SUCCESS: invoke function call to re-render users
                                            onSuccess()

                                        }
                                        .addOnFailureListener { exception ->
                                            // Failed to update friend document
                                            Toast.makeText(context, "Error accepting friend", Toast.LENGTH_SHORT).show()
                                            Log.e(TAG, "Error updating user document", exception)
                                        }
                                } else {
                                    Toast.makeText(context, "Error accepting friend", Toast.LENGTH_SHORT).show()
                                    // Friend document doesn't exist
                                    Log.e(TAG, "User document does not exist")
                                }
                            }
                            .addOnFailureListener { exception ->
                                Toast.makeText(context, "Error accepting friend", Toast.LENGTH_SHORT).show()
                                // Error fetching friend document
                                Log.e(TAG, "Error fetching user document", exception)
                            }

                    }
                    .addOnFailureListener { exception ->
                        Toast.makeText(context, "Error accepting friend", Toast.LENGTH_SHORT).show()
                        // Failed to update friend document
                        Log.e(TAG, "Error updating friend document", exception)
                    }
            } else {
                Toast.makeText(context, "Error accepting friend", Toast.LENGTH_SHORT).show()
                // Friend document doesn't exist
                Log.e(TAG, "Friend document does not exist")
            }
        }
        .addOnFailureListener { exception ->
            Toast.makeText(context, "Error accepting friend", Toast.LENGTH_SHORT).show()
            // Error fetching friend document
            Log.e(TAG, "Error fetching friend document", exception)
        }
}

// removeFriend : two stage process
// 1) removes user from friend.friends
// 2) removes friend from user.friends
suspend fun removeFriend(
    db: FirebaseFirestore,
    context: Context,
    user: User,
    friend: User,
    onSuccess: () -> Unit
) {
    try {
        // Get references to the user and friend documents
        val userRef = db.collection("users").document(user.userId)
        val friendRef = db.collection("users").document(friend.userId)

        // VALIDATION: ensure user/friend are still friends
        val userSnapshot = userRef.get().await()

        // convert to an object
        val userObject = userSnapshot.toObject<User>() ?: User()

        // if user doesn't contain friend, exit function early
        if (!userObject.friends.contains(friend.userId)) {
            Log.d(TAG, "User already removed friend $friend")
            return
        }

        // Remove friend from user's friends list and decrement numFriends
        userRef.update(
            mapOf(
                "friends" to FieldValue.arrayRemove(friend.userId),
                "numFriends" to FieldValue.increment(-1)
            )
        ).await()

        // Remove user from friend's friends list and decrement numFriends
        friendRef.update(
            mapOf(
                "friends" to FieldValue.arrayRemove(user.userId),
                "numFriends" to FieldValue.increment(-1)
            )
        ).await()
        // Call onSuccess callback
        onSuccess()

        Toast.makeText(context, "Successfully removed friend", Toast.LENGTH_LONG).show()
        Log.d(TAG, "User (${user.userId} ${user.username}) successfully removed friend (${friend.userId} ${friend.username})")

    } catch (e: Exception) {
        // Handle any potential errors, such as network issues or Firestore exceptions
        Toast.makeText(context, "Error deleting friend", Toast.LENGTH_LONG).show()
        Log.d(TAG, "ERROR: User (${user.userId} ${user.username}) failed to remove friend (${friend.userId} ${friend.username})")
        e.printStackTrace()
    }
}