package com.example.onlysends_compose.firestore.modules

import android.util.Log
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.Friend
import com.example.onlysends_compose.firestore.types.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

private const val TAG = "SearchFirestore"

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
fun searchAllFriends(
    db: FirebaseFirestore,
    user: User,
    onFriendsLoaded: (List<Friend>) -> Unit
) {
    val usersCollection = db.collection("users")

    usersCollection.get()
        .addOnSuccessListener { querySnapshot ->
            val friendsList = mutableListOf<Friend>()

            // iterate over every user document
            for (document in querySnapshot.documents) {
                // obtain the list of friends this friend has
                val friendUser = document.toObject<User>() ?: User()
                val friends = friendUser.friends

                // TO-DO: filter out user and current friends
                val friend = userToFriend(document)

                Log.d(TAG, "friends of friend are $friends")
                // Filter out the `user` `user.friends` and `friend.friends` for user
                if (friend.userId != user.userId &&
                    !user.friends.any { it.userId == friend.userId } &&
                    !friends.any{ it.userId == user.userId }
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

// searchUserFriends : returns a list of Friend objects for USER friends
fun searchUserFriends(
    db: FirebaseFirestore,
    user: User,
    onFriendsLoaded: (List<Friend>) -> Unit
) {
    val usersCollection = db.collection("users")

    usersCollection.get()
        .addOnSuccessListener { querySnapshot ->
            val friendsList = mutableListOf<Friend>()

            // iterate over every user document
            for (document in querySnapshot.documents) {
                // obtain the list of friends this friend has
                val friendUser = document.toObject<User>() ?: User()
                val friends = friendUser.friends

                // TO-DO: filter out user and current friends
                val friend = userToFriend(document)

                Log.d(TAG, "friends of friend are $friends")
                // Filter out the `user` `user.friends` and `friend.friends` for user
                if (friend.userId != user.userId &&
                    !user.friends.any { it.userId == friend.userId } &&
                    !friends.any{ it.userId == user.userId }
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