package com.example.onlysends_compose.components.navigation

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.onlysends_compose.R
import com.example.onlysends_compose.firestore.types.User
import androidx.compose.material3.Text
import androidx.compose.ui.draw.clip
import coil.compose.AsyncImage
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.colorResource
import androidx.navigation.NavHostController

@Composable
fun CustomTopAppBar(
    user: User?,
    navController: NavHostController,
    context: Context,
) {
    val onlySendsBlue = colorResource(id = R.color.onlySendsBlue)
    val white = colorResource(id = R.color.white)

    val gradientBrush = Brush.verticalGradient(
        colors = listOf(onlySendsBlue, white)
    )

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // Your logo and app name
                Image(
                    painter = painterResource(id = R.drawable.temp_logo),
                    contentDescription = "Logo",
                    modifier = Modifier.size(42.dp),
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = "OnlySends", modifier = Modifier.weight(1f))

                // User greeting and profile picture
                Text(
                    text = "Hello, ${user?.username?.split("\\s+".toRegex())?.firstOrNull() ?: ""}",
                )
                Spacer(modifier = Modifier.width(16.dp))
                if (user?.profilePictureUrl != null) {
                    AsyncImage(
                        model = user.profilePictureUrl,
                        contentDescription = "User profile picture",
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .clickable { navController.navigate( context.getString(R.string.profile) ) },
                        contentScale = ContentScale.Crop
                    )
                }
            }
        },
        backgroundColor = Color.Transparent, // Set background color to transparent
        elevation = 0.dp, // Set elevation to 0 to remove shadow
        modifier = Modifier
            .fillMaxWidth()
            .background(brush = gradientBrush) // Apply gradient background
    )
}