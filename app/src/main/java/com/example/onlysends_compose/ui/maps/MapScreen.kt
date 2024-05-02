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
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onlysends_compose.ui.home.theme.buttonColor
import com.example.onlysends_compose.ui.home.theme.signOutColor
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MapScreen(
    navController: NavController,
    modifier: Modifier = Modifier
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

    val bottomSheetState = rememberBottomSheetScaffoldState()
    val scope = rememberCoroutineScope()

    androidx.compose.material3.BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetPeekHeight = 100.dp,
        sheetContent = {
            // Your bottom sheet content here
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
            Spacer(modifier = modifier.padding(200.dp))
        }
    ) {
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
                    modifier = Modifier
                        .align(Alignment.CenterVertically)
                        .padding(2.dp),
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
                    .height(450.dp),
                cameraPositionState = cameraPositionState,
                onMapLoaded = {
                    isMapLoaded = true
                },
                viewModel = viewModel(), // Create a LocationViewModel instance using viewModel()
                context = LocalContext.current // Obtain the Android context using LocalContext.current
            )
        }

    }
}