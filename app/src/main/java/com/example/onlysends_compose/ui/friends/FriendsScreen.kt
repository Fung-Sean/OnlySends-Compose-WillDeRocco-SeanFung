package com.example.onlysends_compose.ui.friends

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.User
import com.example.onlysends_compose.ui.home.theme.buttonColor
import kotlin.reflect.KFunction1

const val TAG = "FriendsScreen"

// FriendsScreen : composable function that allows user to view all current frineds
@Composable
fun FriendsScreen(
    user: User,
    onUpdateUser: KFunction1<User, Unit>
) {
    // Get the current context
    val context = LocalContext.current

    // State to hold the list of friends
    var friends by remember { mutableStateOf(emptyList<User>()) }

    // track loading state
    var isLoading by remember { mutableStateOf(false) }

    LaunchedEffect(key1 = user) {
        // update state variables every time user object is altered

        // start loading the loader
        isLoading = true

        // fetch potentialFriends from db
        Firestore.handleSearchUserFriends(user) { loadedFriends ->
            // update potentialFriends with db results
            friends = loadedFriends
            Log.d(TAG, "loaded friends $friends")
            isLoading = false
        }
    }
//
//    fun isFriendInOutgoingList(user: User, friend: Friend): Boolean {
//        return user.outgoingFriends.any { it.userId == friend.userId }
//    }
//    fun isFriendInIncoming(user: User, friend: Friend): Boolean {
//        return user.incomingFriends.any { it.userId == friend.userId }
//    }

    // Render the UI using the list of friends
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(30.dp) // Add some padding for better spacing
        ) {
            // Show loading indicator if friends is empty and loading is true
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.width(64.dp),
                    color = MaterialTheme.colorScheme.secondary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant,
                )

            } else {
                friends.forEach { friend ->
                    // pic, username (plus info underneath), button (follow or pending)
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .padding(bottom = 30.dp)
                    ) {
                        // display profile picture
                        if (friend.profilePictureUrl != null) {
                            AsyncImage(
                                model = friend.profilePictureUrl,
                                contentDescription = "User profile picture",
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // display column of username (plus info)
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = friend.username,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Row() {
                                val numFriendsText = if (friend.numFriends == 1) {
                                    "${friend.numFriends} friend, "
                                } else {
                                    "${friend.numFriends} friends, "
                                }

                                Text(
                                    text = numFriendsText,
                                    fontSize = 12.sp,
                                )
                                Text(
                                    text = friend.climbingStyle,
                                    fontSize = 12.sp,
                                )
                            }
                        }

                        Button(
                            colors = ButtonDefaults.buttonColors(buttonColor),
                            enabled = false,
                            onClick = { },
                            modifier = Modifier
                                .size(
                                    width = 95.dp,
                                    height = 35.dp
                                ),
                        ) {
                            Text(
                                text = "Remove",
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }
    }
}

