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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddAPhoto
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.AddCircleOutline
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.onlysends_compose.R
import com.example.onlysends_compose.components.generic.ButtonWithIcon
import com.example.onlysends_compose.components.navigation.PageHeaderText
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.User
import com.example.onlysends_compose.ui.home.fake_data.Post
import com.example.onlysends_compose.ui.home.theme.OnlySendsTheme
import com.example.onlysends_compose.ui.home.theme.RoundedCornerShape
import com.example.onlysends_compose.ui.home.theme.buttonColor
import com.example.onlysends_compose.ui.home.theme.signOutColor
import kotlin.reflect.KFunction1

private const val TAG = "AddPostScreen"
//This is the add posts composable. It allows a user to add a post to the database.
@Composable
fun AddPostScreen(
    context: Context? = null,
    user: User,
    navController: NavHostController,
) {
    val caption = remember { mutableStateOf("") }
    //Uses a remember state of string to update the value of caption as a user types.
    var selectedImageByUri by remember {
        mutableStateOf<Uri?>(null)
    }
    //Holds the Uri of an image
    val photoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickVisualMedia(),
        onResult = {
            selectedImageByUri = it
            Log.d(TAG, "selected image is: $selectedImageByUri")
        }
    )
    //Allows a user to choose an image from their photo library

    Column(
        modifier = Modifier
            .padding(10.dp)
            .padding(bottom = 30.dp)
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PageHeaderText(
            text = "Add a Post"
        )

        ButtonWithIcon(
            text = "Select a photo",
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.onlySendsBlue),
                contentColor = colorResource(id = R.color.white)
            ),
            icon = Icons.Default.AddAPhoto,
            onClick = {
                photoPickerLauncher.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
                )
            }
        )

        Spacer(modifier = Modifier.padding(30.dp))

        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .height(280.dp)
                .aspectRatio(1f)
                .clip(RoundedCornerShape(topEnd = 8.dp , topStart = 8.dp, bottomEnd = 8.dp, bottomStart = 8.dp)),
            model = selectedImageByUri,
            contentDescription = null,
            contentScale = ContentScale.Crop,
        )
        //Async image allows a developer to display an image that is selected  by the uri
        Spacer(modifier = Modifier.padding(40.dp))

        OutlinedTextField(
            value = caption.value,
            onValueChange = { newValue -> caption.value = newValue },
            label = { Text("Caption") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(12.dp))

        ButtonWithIcon(
            text = "Add Post",
            icon = Icons.Default.AddCircle,
            onClick = {
                Firestore.handleCreatePost(
                    context = context,
                    user = user,
                    caption = caption.value,
                    postPictureUri = selectedImageByUri,
                    navController = navController,
                )
            }
        )
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

