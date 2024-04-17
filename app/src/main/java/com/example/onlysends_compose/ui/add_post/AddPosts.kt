package com.example.onlysends_compose.ui.add_post

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.modifier.modifierLocalMapOf
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onlysends_compose.ui.home.fake_data.Post
import com.example.onlysends_compose.ui.home.theme.OnlySendsTheme

@Composable
fun AddPostScreen(
    onPostAdded: (Post) -> Unit,
    modifier:  Modifier = Modifier
) {
    // Define local state for text input
    val postText = remember { mutableStateOf("") }


    // Add more state for other input fields as needed

    Column(
        modifier = Modifier.padding(16.dp)
    ) {
        // Text input field
        OutlinedTextField(
            value = postText.value,
            onValueChange = { newValue -> postText.value = newValue },
            label = { Text("Caption") },
            modifier = Modifier.fillMaxWidth()
        )

        // Add more input fields here as needed (e.g., image picker)

        // Button to submit the post
        Button(
            onClick = {
                // Create a Post object with the input data
                val newPost = Post(
                    id = "", // You may generate an ID for the post FIX LATER
                    text = postText.value,
                    imageUrl = "", // If you have an image URL, you can use it here
                    createdAt = "", // You may want to use the actual current date here
                    likesCount = 0, // Initialize likes count to zero
                    commentCount = 0, // Initialize comment count to zero
                    authorId = 0, // Assign the ID of the author if applicable
                    authorName = "Your Name", // You may have a logged-in user's name here
                    authorImage = "Your Profile URL", // You may have a logged-in user's profile image URL here
                    isLiked = false, // Initialize isLiked to false
                    isOwnPost = true // Assuming this is a post created by the current user
                )
                // Pass the new post to the callback function
                onPostAdded(newPost)
            },
            modifier = modifier
                .padding(vertical = 16.dp)
        ) {
            Text("Add Post")
        }
    }
}

@Preview
@Composable
fun AddPostScreenPreview() {
    val postText = remember { mutableStateOf("") }

    OnlySendsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AddPostScreen(
                onPostAdded = { newPost ->
                    // Handle the new post, e.g., add it to the list of posts
                }
            )
        }
    }
}
//data class Post(
//    val id: String,
//    val text: String,
//    val imageUrl: String,
//    val createdAt: String,
//    val likesCount: Int,
//    val commentCount: Int,
//    val authorId: Int,
//    val authorName: String,
//    val authorImage: String,
//    val isLiked: Boolean = false,
//    val isOwnPost: Boolean = false
//)