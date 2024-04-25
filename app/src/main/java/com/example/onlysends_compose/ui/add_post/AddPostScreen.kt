package com.example.onlysends_compose.ui.add_post
import android.content.Context
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.onlysends_compose.R
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.User
import com.example.onlysends_compose.ui.home.fake_data.Post
import com.example.onlysends_compose.ui.home.theme.OnlySendsTheme
import com.example.onlysends_compose.ui.home.theme.RoundedCornerShape
import com.example.onlysends_compose.ui.home.theme.buttonColor
import com.example.onlysends_compose.ui.home.theme.signOutColor

private const val TAG = "AddPostScreen"

//USED CHATGPT FOR LAYOUT IN THAT IT WASN'T BEING LAYED OUT CORRECTLY
@Composable
fun AddPostScreen(
    context: Context? = null,
    user: User,
    navController: NavHostController,
) {
    val caption = remember { mutableStateOf("") } // Initialize caption state

    var selectedImageByUri by remember {
        mutableStateOf<Uri?>(null)
    }

    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            selectedImageByUri = it
            Log.d(TAG, "selected image is: $selectedImageByUri")
        }
    )

    Column(
        modifier = Modifier
            .padding(16.dp)
            .verticalScroll(rememberScrollState())
    ) {
        Text(
            text = "Add a Post",
            style = MaterialTheme.typography.displaySmall
        )

        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                signOutColor,
                contentColor = Color.White
            ),
            onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = "Pick A Photo To Post",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontSize = 18.sp,
                    ),
                    modifier = Modifier.weight(1f)
                )
                Spacer(modifier = Modifier.padding(4.dp))
                Icon(
                    painter = painterResource(id = R.drawable.add_photo),
                    contentDescription = "Add image",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
        Spacer(modifier = Modifier.padding(30.dp))
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(240.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape()),
            model = selectedImageByUri,
            contentDescription = null,
            contentScale = ContentScale.FillBounds
        )
        Spacer(modifier = Modifier.padding(40.dp))
        OutlinedTextField(
            value = caption.value,
            onValueChange = { newValue -> caption.value = newValue },
            label = { Text("Caption") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        Button(
            shape = RoundedCornerShape(8.dp),
            colors = ButtonDefaults.buttonColors(
                signOutColor,
            ),
            onClick = {
                Firestore.handleCreatePost(
                    context = context,
                    user = user,
                    caption = caption.value,
                    postPictureUri = selectedImageByUri,
                    navController = navController
                )
                      },
            modifier = Modifier
                .padding(vertical = 16.dp)
                .align(Alignment.CenterHorizontally)
                .fillMaxWidth()
        ) {
            Row {
                Icon(
                    painter = painterResource(id = android.R.drawable.ic_input_add),
                    contentDescription = null
                )
                Text(
                    "Add Post",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

// AddPostScreenPreview : not used for page logic (only to preview layout)
@Preview
@Composable
fun AddPostScreenPreview() {
    val user = User(
        userId = "123",
        username = "SampleUser",
        profilePictureUrl = null, // Provide appropriate values for other fields as needed
    )
    val navController = rememberNavController()

    OnlySendsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AddPostScreen(user = user, navController = navController)
        }
    }
}

