package com.example.onlysends_compose.firestore

import android.content.Context
import com.example.onlysends_compose.firestore.modules.acceptFriend
import com.example.onlysends_compose.firestore.modules.createPost
import com.example.onlysends_compose.firestore.modules.createUserDocument
import com.example.onlysends_compose.firestore.modules.followFriend
import com.example.onlysends_compose.firestore.modules.searchAllFriends
import com.example.onlysends_compose.firestore.modules.searchUserFriends
import com.example.onlysends_compose.firestore.modules.updateUserProfile
import com.example.onlysends_compose.firestore.types.User
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore

object Firestore {

    private val db: FirebaseFirestore by lazy {
        Firebase.firestore
    }

    // handleCreateUserDocument : creates and updates user (calls onUpdateUser when successful)
    fun handleCreateUserDocument(
        user: User,
        onUpdateUser: (User) -> Unit
    ) {
        // call UserFirestore's creatUserDocument
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

    fun handleSearchAllFriends(
        user: User,
        onFriendsLoaded: (List<User>) -> Unit
    ) {
        searchAllFriends(
            db = db,
            user = user,
            onFriendsLoaded = onFriendsLoaded
        )
    }

    // searchUserFriends : returns a list of Friend objects for the current USER
    fun handleSearchUserFriends(
        user: User,
        onFriendsLoaded: (List<User>) -> Unit
    ) {
        searchUserFriends(
            db = db,
            user = user,
            onFriendsLoaded = onFriendsLoaded
        )
    }

    // handleFollowFriend : adds friend to `outgoingFriends` for user and `incomingFriends` for friend
    fun handleFollowFriend(
        context: Context,
        user: User,
        friend: User,
        onUpdateUser: (User) -> Unit
    ) {
        followFriend(
            db = db,
            context = context,
            user = user,
            friend = friend,
            onUpdateUser = onUpdateUser,
        )
    }

    // handleAcceptFriend : two stage process
    // 1) add user to friendUserRef.friends
    // 2) adds friend to userRef.friends
    fun handleAcceptFriend(
        context: Context,
        user: User,
        friend: User,
        onUpdateUser: (User) -> Unit
    ) {
        acceptFriend(
            db = db,
            context = context,
            user = user,
            friend = friend,
            onUpdateUser = onUpdateUser
        )
    }

    // handleCreatePost : creates a post for a user
    fun handleCreatePost(
        context: Context?,
        user: User,
        caption: String,
        postPictureUri: String
    ) {
        createPost(
            db = db,
            context = context,
            user = user,
            caption = caption,
            postPictureUri = postPictureUri
        )
    }

}
