package com.example.onlysends_compose.ui.search

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.Friend
import com.example.onlysends_compose.firestore.types.User

const val TAG = "SearchScreen"

// SearchScreen : composable function that allows user to search for friends and accept/delete requests
@Composable
fun SearchScreen(
    user: User
) {
// State to hold the list of friends
    var potentialFriends by remember { mutableStateOf(emptyList<Friend>()) }

    // Call searchUserFriends whenever anything about the user changes
    LaunchedEffect(user) {
        Firestore.searchAllFriends(user) { loadedFriends ->
            potentialFriends = loadedFriends
            Log.d(TAG, "loaded potential friends $potentialFriends")
        }
    }

    // Render the UI using the list of potentialFriends
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.TopCenter
    ) {
        // Render your UI here using the `potentialFriends` list
        // For example:
        Column(
            modifier = Modifier.padding(30.dp) // Add some padding for better spacing
        ) {
            potentialFriends.forEach { friend ->
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
                            Text(
                                text = "${friend.numFriends} friends, ",
                                fontSize = 12.sp,
                            )
                            Text(
                                text = friend.climbingStyle,
                                fontSize = 12.sp,
                            )
                        }
                    }


                    // display button to add friend (or disabled button saying "pending")
                    Button(
                        onClick = {},
                        modifier = Modifier
                            .size(
                                width = 90.dp,
                                height = 35.dp
                            )
                    ) {
                        Text(text = "Follow")
                    }
                }
            }
        }
    }

}