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

    val sortedPotentialFriends = potentialFriends.sortedWith(compareBy(
        // First, sort by isIncomingFriend in descending order (true comes before false)
        { !it.isIncomingFriend },
        // Then, for tie-breaks, sort by friend.username alphabetically
        { it.friend.username }
    ))

    Log.d(TAG, "collected SORTED potentialFriends: ${sortedPotentialFriends.toList()}")
    return@coroutineScope sortedPotentialFriends
}

// searchUserFriends : returns a list of User objects for USER friends
suspend fun searchUserFriends(
    db: FirebaseFirestore,
    user: User,
): List<User> = coroutineScope {
    val usersCollection = db.collection("users")

    val friends = mutableListOf<User>()

    try {
        usersCollection.get().await().documents.forEach { document ->
            val friend = document.toObject<User>() ?: User()
            val friendOfFriends = friend.friends

            // Filter out the `user` and ensures `userId` is within the friends.friends
            if (friend.userId != user.userId &&
                friendOfFriends.any { it == user.userId }
            ) {

                friends.add(friend)
            }
        }
    } catch (e: Exception) {
        Log.e(TAG, "Error searching all friends", e)
    }

    val sortedFriends = friends.sortedBy { it.username }

    Log.d(TAG, "collected SORTED friends: ${sortedFriends.toList()}")
    return@coroutineScope sortedFriends
}