package com.example.onlysends_compose.firestore.modules

import android.util.Log
import com.example.onlysends_compose.firestore.types.User
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.toObject

private const val TAG = "SearchFirestore"

//// userToFriend : function to convert a User document to a Friend object
//private fun userToFriend(userDoc: DocumentSnapshot): Friend {
//    return Friend(
//        userId = userDoc.id,
//        username = userDoc.getString("username") ?: "",
//        profilePictureUrl = userDoc.getString("profilePictureUrl"),
//        climbingStyle = userDoc.getString("climbingStyle") ?: "",
//        numFriends = (userDoc.get("friends") as? List<*>)?.size ?: 0
//    )
//}

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

// ************************************ NOTE: COME BACK TO THIS FUNCTION (I think we can make it more efficient by only fetching all users -> instead of also fetching user) ************************************
// searchUserFriends : returns a list of User objects for USER friends
fun searchUserFriends(
    db: FirebaseFirestore,
    user: User,
    onFriendsLoaded: (List<User>) -> Unit
) {
    // grab userCollection
    val usersCollection = db.collection("users")

    // Get the reference to the specific user document
    val userDocRef = usersCollection.document(user.userId)

    // Fetch the user document from Firestore
    userDocRef.get()
        .addOnSuccessListener { documentSnapshot ->
            if (documentSnapshot.exists()) {
                // Extract the "friends" field from the user document
                val friendIds = documentSnapshot.get("friends") as? List<String> ?: emptyList()

                // call get method on all users and check if userId of friend matches
                usersCollection.get()
                    .addOnSuccessListener { querySnapshot ->
                        val friendsList = mutableListOf<User>()

                        // iterate over every user document
                        for (document in querySnapshot.documents) {
                            // obtain the list of friends this friend has
                            val friend = document.toObject<User>() ?: User()

                            Log.d(TAG, "considering user as friend: $friend")
                            // Filter out the `user` and ensures `friend.userId` is in friendIds
                            if (friend.userId != user.userId &&
                                !friendIds.any { it == user.userId }
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
            } else {
                // User document doesn't exist
                Log.e(TAG, "User document does not exist")
            }
        }
        .addOnFailureListener { exception ->
            // Error fetching user document
            Log.e(TAG, "Error fetching user document", exception)
        }

}