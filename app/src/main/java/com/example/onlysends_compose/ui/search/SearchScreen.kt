package com.example.onlysends_compose.ui.search

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.ui.unit.dp
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
            modifier = Modifier.padding(16.dp) // Add some padding for better spacing
        ) {
            potentialFriends.forEach { friend ->
                Text(
                    text = friend.username,
                    modifier = Modifier.padding(vertical = 8.dp) // Add vertical padding between each username
                )
            }
        }
    }

}