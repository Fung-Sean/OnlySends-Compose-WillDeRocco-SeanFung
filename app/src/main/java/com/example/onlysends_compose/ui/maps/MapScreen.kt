package com.example.onlysends_compose.ui.maps

import android.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.onlysends_compose.ui.home.theme.buttonColor
import com.example.onlysends_compose.ui.home.theme.signOutColor
import com.google.maps.android.compose.rememberCameraPositionState


@Composable
fun MapScreen(
    navController: NavController
){
    val cameraPositionState = rememberCameraPositionState{
        position = defaultCameraPosition
    }
    var isMapLoaded by remember {
        mutableStateOf(false)
    }

    var searchText by remember {
        mutableStateOf("Search a place")
    }
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Row (
            modifier = Modifier.align(Alignment.CenterHorizontally)
        ){
            Text(
                text = "New Heights",
                style = MaterialTheme.typography.displayMedium,
                modifier = Modifier
                    .padding(16.dp)

            )
            Spacer(modifier = Modifier.weight(1f)) // Add a spacer to occupy the available space
            Button(
                onClick = { navController.navigate("AddHeight")},
                modifier = Modifier.align(Alignment.CenterVertically).padding(2.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(signOutColor)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.ic_input_add),
                    contentDescription = null,
                )
            }
        }

        MapDisplay(
            modifier = Modifier
                .fillMaxWidth()
                .height(550.dp),
            cameraPositionState = cameraPositionState,
            onMapLoaded = {
                isMapLoaded = true
            }
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Adjust padding as needed
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Icon
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search",
                modifier = Modifier.size(24.dp)
            )

            // TextField for search input
            OutlinedTextField(
                value = searchText,
                onValueChange = { searchText = it },
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )

            // Button for search action
            Button(
                onClick = { /* Handle search action */ },
                modifier = Modifier.padding(start = 8.dp),
                colors = ButtonDefaults.buttonColors(buttonColor)
            ) {
                Text(text = "Search")
            }
        }
    }
}