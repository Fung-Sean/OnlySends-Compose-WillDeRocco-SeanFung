package com.example.onlysends_compose.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    user: User?,
    onSignOut: () -> Unit
) {
    // variable to keep track of username
    var username by remember { mutableStateOf(user?.username) }

    // variables for dropdown menu of climbing styles
    var climbStyle by remember { mutableStateOf(user?.climbingStyle) }
    var expanded by remember { mutableStateOf(false) }
    val climbStyles = listOf("Bouldering", "Sport Climbing", "Trad Climbing", "Lead Climbing", "Top-rope Climbing", "Ice Climbing")

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // display profile picture
        if (user?.profilePictureUrl != null) {
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
                value = username ?: "",
                onValueChange = { newValue ->
                    username = newValue
                },
                label = { username ?: "" },
                modifier = Modifier.padding(vertical = 8.dp),
                singleLine = true,
                textStyle = androidx.compose.ui.text.TextStyle(fontSize = 18.sp)
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
                onExpandedChange = { expanded = it }
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
                            text = { Text(text = style) },
                            onClick = { climbStyle = style }
                        )
                    }
                }
            }

        }

        // button to let user update the user in Firestore db
        Button(
            onClick = {},
            modifier = Modifier
                .padding(top = 20.dp)
        ) {
            Text(text = "Update")
        }

        // button to let user sign out
        Button(
            onClick = onSignOut,
            modifier = Modifier
                .padding(top = 20.dp)
        ) {
            Text(text = "Sign out")
        }
    }
}

