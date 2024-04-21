package com.example.onlysends_compose.firestore.modules

import android.util.Log
import com.example.onlysends_compose.firestore.types.User
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

private const val TAG = "SearchFirestore"

// searchAllFriends : updates a list of User objects for all POTENTIAL friends
fun searchAllFriends(
    db: FirebaseFirestore,
    user: User,
    onFriendsLoaded: (List<User>) -> Unit
) {
    val usersCollection = db.collection("users")

    usersCollection.get()
        .addOnSuccessListener { querySnapshot ->
            val friendsList = mutableListOf<User>()

            // iterate over every user document
            for (document in querySnapshot.documents) {
                // obtain the list of friends this friend has
                val friend = document.toObject<User>() ?: User()
                val friends = friend.friends


                Log.d(TAG, "friends of friend are $friends")
                // Filter out the `user` and ensures user is not friends with `friend`
                if (friend.userId != user.userId &&
                    !friends.any{ it == user.userId }
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