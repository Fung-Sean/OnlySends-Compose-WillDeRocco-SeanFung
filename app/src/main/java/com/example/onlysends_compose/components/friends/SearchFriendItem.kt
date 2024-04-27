package com.example.onlysends_compose.components.friends

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.FriendRequest
import com.example.onlysends_compose.firestore.types.User
import com.example.onlysends_compose.ui.home.theme.buttonColor


@Composable
fun SearchFriendItem(
    user: User,
    friendRequest: FriendRequest,
    fetchMoreData: () -> Unit
) {
    val friend = friendRequest.friend

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


        // button : either "Follow" or "Pending"
        if (friendRequest.isOutgoingFriend) {
            // display button saying "pending" (outgoing request)
            Button(
                onClick = {},
                enabled = false,
                modifier = Modifier
                    .size(
                        width = 85.dp,
                        height = 35.dp
                    )
            ) {
                Text(
                    text = "Pending",
                    fontSize = 9.sp
                )
            }
        } else if (friendRequest.isIncomingFriend){
            // display button saying "accept" (incoming request)
            Button(
                colors = ButtonDefaults.buttonColors(buttonColor),
                onClick = {
                    Firestore.handleAcceptFriend(
                        context = context,
                        user = user,
                        friend = friend,
                        fetchMoreData = fetchMoreData
                    )
                },
                modifier = Modifier
                    .size(
                        width = 85.dp,
                        height = 35.dp
                    )
            ) {
                Text(
                    text = "Accept",
                    fontSize = 10.sp
                )
            }
        } else {
            // display button saying "follow" (potential new friend)
            Button(
                colors = ButtonDefaults.buttonColors(buttonColor),
                onClick = {
                    Firestore.handleFollowFriend(
                        context = context,
                        user = user,
                        friend = friend,
                        onUpdateUser = onUpdateUser)
                },
                modifier = Modifier
                    .size(
                        width = 85.dp,
                        height = 35.dp
                    ),
            ) {
                Text(
                    text = "Follow",
                    fontSize = 12.sp
                )
            }
        }
    }
}
