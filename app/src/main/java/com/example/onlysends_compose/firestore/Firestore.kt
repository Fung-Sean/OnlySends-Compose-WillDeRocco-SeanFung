package com.example.onlysends_compose.firestore

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.navigation.NavHostController
import com.example.onlysends_compose.firestore.modules.acceptFriend
import com.example.onlysends_compose.firestore.modules.createPost
import com.example.onlysends_compose.firestore.modules.createUserDocument
import com.example.onlysends_compose.firestore.modules.followFriend
import com.example.onlysends_compose.firestore.modules.getFriendPosts
import com.example.onlysends_compose.firestore.modules.searchAllFriends
import com.example.onlysends_compose.firestore.modules.searchUserFriends
import com.example.onlysends_compose.firestore.modules.updateUserProfile
import com.example.onlysends_compose.firestore.types.FriendRequest
import com.example.onlysends_compose.firestore.types.Post
import com.example.onlysends_compose.firestore.types.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlin.reflect.KFunction1

// Firestore : object contains a series of functions serving as endpoints to the actual functions
// (actual functions located in modules folder)
object Firestore {

    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    /* ------------------------------ UserFirestore functions ------------------------------ */
    // handleCreateUserDocument : creates and updates user (calls onUpdateUser when successful)
    fun handleCreateUserDocument(
        user: User,
        onUpdateUser: (User) -> Unit
    ) {
        // call UserFirestore's createUserDocument
        createUserDocument(
            db = db,
            user = user,
            onUpdateUser = onUpdateUser
        )
    }

    // handleUpdateUserProfile : updates the user with `newUsername` and `newClimbStyle`
    fun handleUpdateUserProfile(
        context: Context,
        userId: String,
        newUsername: String,
        newClimbStyle: String,
        onUpdateUser: (User) -> Unit
    ) {
        updateUserProfile(
            db = db,
            context = context,
            userId = userId,
            newUsername = newUsername,
            newClimbStyle = newClimbStyle,
            onUpdateUser = onUpdateUser
        )
    }

    /* ------------------------------ SearchFirestore functions ------------------------------ */
    suspend fun handleSearchAllFriends(
        user: User,
    ): List<FriendRequest> {
        return searchAllFriends(
            db = db,
            user = user,
        )
    }

    // searchUserFriends : returns a list of Friend objects for the current USER
    suspend fun handleSearchUserFriends(
        user: User,
    ): List<User> {
        return searchUserFriends(
            db = db,
            user = user,
        )
    }

    /* ------------------------------ FriendFirestore functions ------------------------------ */
    // handleFollowFriend : adds friend to `outgoingFriends` for user and `incomingFriends` for friend
    fun handleFollowFriend(
        context: Context,
        user: User,
        friend: User,
        onSuccess: () -> Unit
    ) {
        followFriend(
            db = db,
            context = context,
            user = user,
            friend = friend,
            onSuccess = onSuccess
        )
    }

    // handleAcceptFriend : two stage process
    // 1) add user to `friendUserRef.friends` AND remove user from `friendUserRef.outgoingFriends`
    // 2) adds friend to `userRef.friends` AND remove friend from `userRef.incomingFriends`
    fun handleAcceptFriend(
        context: Context,
        user: User,
        friend: User,
        onSuccess: () -> Unit
    ) {
        acceptFriend(
            db = db,
            context = context,
            user = user,
            friend = friend,
            onSuccess = onSuccess
        )
    }

    /* ------------------------------ PostFirestore functions ------------------------------ */
    // handleCreatePost : creates a post for a user
    fun handleCreatePost(
        context: Context?,
        user: User,
        caption: String,
        postPictureUri: Uri?,
        navController: NavHostController,
    ) {
        createPost(
            db = db,
            context = context,
            user = user,
            caption = caption,
            postPictureUri = postPictureUri,
            navController = navController,
        )
    }

    // handleGetFriendPosts : returns a list of Post objects for this User's friends
    suspend fun handleGetFriendPosts(
        context: Context,
        user: User,
    ): SnapshotStateList<Post> {
        return getFriendPosts(
            db = db,
            context = context,
            user = user,
        )
    }

}
