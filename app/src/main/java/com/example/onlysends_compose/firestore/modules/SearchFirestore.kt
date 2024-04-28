package com.example.onlysends_compose.firestore.modules

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.example.onlysends_compose.firestore.types.FriendRequest
import com.example.onlysends_compose.firestore.types.Post
import com.example.onlysends_compose.firestore.types.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.tasks.await

private const val TAG = "SearchFirestore"

// define helper functions for searchAllFriends
fun isFriendInOutgoingList(user: User, friend: User): Boolean {
    return friend.incomingFriends.any { it == user.userId }
}
fun isFriendInIncoming(user: User, friend: User): Boolean {
    return friend.outgoingFriends.any { it == user.userId }
}

// searchAllFriends : updates a list of User objects for all POTENTIAL friends
suspend fun searchAllFriends(
    db: FirebaseFirestore,
    user: User,
): List<FriendRequest> = coroutineScope {
    val usersCollection = db.collection("users")

    val potentialFriends = mutableListOf<FriendRequest>()

    try {
        usersCollection.get().await().documents.forEach { document ->
            val friend = document.toObject<User>() ?: User()
            val friends = friend.friends

            if (friend.userId != user.userId &&
                !friends.any { it == user.userId }
            ) {
                val friendRequest = FriendRequest(friend = friend)

                if (isFriendInIncoming(user, friend)) {
                    friendRequest.isIncomingFriend = true
                } else if (isFriendInOutgoingList(user, friend)) {
                    friendRequest.isOutgoingFriend = true
                }

                potentialFriends.add(friendRequest)
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error searching all friends", e)
    }
    Log.d(TAG, "collected potentialFriends: ${potentialFriends.toList()}")
    return@coroutineScope potentialFriends
}

// searchUserFriends : returns a list of User objects for USER friends
fun searchUserFriends(
    db: FirebaseFirestore,
    user: User,
    onFriendsLoaded: (List<User>) -> Unit
) {
    // grab userCollection
    val usersCollection = db.collection("users")

    // call get method on all users and check if userId of friend matches
    usersCollection.get()
        .addOnSuccessListener { querySnapshot ->
            val friendsList = mutableListOf<User>()

            // iterate over every user document
            for (document in querySnapshot.documents) {
                // obtain the list of friends this friend has
                val friend = document.toObject<User>() ?: User()

                Log.d(TAG, "considering user as friend: $friend")
                // Filter out the `user` and ensures `userId` is within the friends.friends
                if (friend.userId != user.userId &&
                    friend.friends.any { it == user.userId }
                ) {
                    friendsList.add(friend)
                }
            }

            onFriendsLoaded(friendsList)
        }
        .addOnFailureListener { exception ->
            Log.e(TAG, "Error searching all friends", exception)
            onFriendsLoaded(emptyList()) // Return empty list in case of failure
        }
}