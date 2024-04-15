package com.example.onlysends_compose.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.onlysends_compose.firestore.types.User

// simple function to render the profile page (with sign-out options)
// for now -> doesn't contain any state, so no need for ViewModel
@Composable
fun ProfileScreen(
    user: User?,
    onSignOut: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // display profile picture
        if(user?.profilePictureUrl != null) {
            AsyncImage(
                model = user.profilePictureUrl,
                contentDescription = "User profile picture",
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(16.dp))
        }
        // display username
        if(user?.username != null) {
            Text(
                text = user.username,
                textAlign = TextAlign.Center,
                fontSize = 36.sp
            )
        }
        // button to let user sign out
        Button(onClick = onSignOut) {
            Text(text = "Sign out")
        }
    }

}