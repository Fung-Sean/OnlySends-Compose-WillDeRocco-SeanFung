package com.example.onlysends_compose.ui.profile

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.rememberNavController
import coil.compose.AsyncImage
import com.example.onlysends_compose.R
import com.example.onlysends_compose.components.generic.ButtonWithIcon
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.User
import com.example.onlysends_compose.ui.home.theme.OnlySendsTheme

private const val TAG = "Profile Screen"

// ProfileScreen : renders Profile page and allows users to update their info
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User,
    onSignOut: () -> Unit,
    onUpdateUser: (User) -> Unit
) {
    // Get the current context
    val context = LocalContext.current

    // variable to keep track of username
    var username by remember { mutableStateOf(user.username) }

    // variables for dropdown menu of climbing styles
    var climbStyle by remember { mutableStateOf(user.climbingStyle) }
    var expanded by remember { mutableStateOf(false) }
    val climbStyles = listOf("Bouldering", "Sport Climbing", "Trad Climbing", "Lead Climbing", "Top-rope Climbing", "Ice Climbing")

    LaunchedEffect(user) {
        climbStyle = user.climbingStyle.ifEmpty {
            "pick a style"
        }
        username = user.username
    }

    Log.d(TAG, "current user is $username || ${user.climbingStyle} || $climbStyle sussy")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // display profile picture
        if (user.profilePictureUrl != null) {
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

        // Display "Full Name" text and input box in a Column
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // display "Full Name" text
            Text(
                text = "Full Name",
                textAlign = TextAlign.Left,
                fontSize = 18.sp
            )

            // input box for user's full name
            OutlinedTextField(
                value = username,
                onValueChange = { newValue ->
                    username = newValue
                },
                modifier = Modifier.padding(vertical = 8.dp),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 14.sp)
            )
        }

        // Display "Climbing Style" text and input box in a Column
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // display "Full Name" text
            Text(
                text = "Climbing Style",
                textAlign = TextAlign.Left,
                fontSize = 18.sp
            )


            // dropdown menu of climbing styles
            ExposedDropdownMenuBox(
                expanded = expanded,
                onExpandedChange = { expanded = it },
                modifier = Modifier.background(Color.White)
            ) {
                TextField(
                    value = climbStyle ?: "pick a climbing style",
                    onValueChange = {},
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(),
                    modifier = Modifier.menuAnchor()
                )

                ExposedDropdownMenu(
                    expanded = expanded,
                    onDismissRequest = { expanded = false }
                ) {
                    climbStyles.forEach { style ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = style,
                                    fontSize = 12.sp
                                )
                                   },
                            onClick = { climbStyle = style }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(30.dp))

        // button to let user update the user in Firestore db
        ButtonWithIcon(
            modifier = Modifier
                .size(
                    width = 150.dp,
                    height = 40.dp
                ),
            colors = ButtonDefaults.buttonColors(
                containerColor = colorResource(id = R.color.onlySendsBlue),
                contentColor = colorResource(id = R.color.white)
            ),
            text = "Update",
            icon = Icons.Default.Edit,
            onClick = {
                Firestore.handleUpdateUserProfile(
                    context,
                    user.userId,
                    username,
                    climbStyle,
                    onUpdateUser
                )
            }
        )

        Spacer(modifier = Modifier.height(20.dp))

        // button to let user sign out
        ButtonWithIcon(
            modifier = Modifier
                .size(
                    width = 150.dp,
                    height = 40.dp
                ),
            text = "Sign out",
            icon = Icons.AutoMirrored.Filled.Logout,
            onClick = onSignOut
        )
    }

}


// ProfileScreenPreview : not used for page logic (only to preview layout)
@Preview
@Composable
fun ProfileScreenPreview() {
    val user = User(
        userId = "123",
        username = "SampleUser",
        profilePictureUrl = null, // Provide appropriate values for other fields as needed
    )
    val navController = rememberNavController()

    fun updateUser(newUser: User) {
    }

    OnlySendsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProfileScreen(
                user = user,
                onUpdateUser = { updateUser(user)  },
                onSignOut = {}
            )
        }
    }
}

