package com.example.onlysends_compose.ui.maps

import android.R
import android.app.Activity
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onlysends_compose.MainActivity.Destinations
import com.example.onlysends_compose.components.generic.CustomSearchBar
import com.example.onlysends_compose.ui.home.theme.RoundedCornerShape
import com.example.onlysends_compose.ui.home.theme.buttonColor
import com.example.onlysends_compose.ui.home.theme.signOutColor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MapScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    context: Context,
    activity: Activity,
){
    val viewModel = remember {
        LocationViewModel(context, activity ) // Pass both context and activity
    }

    val cameraPositionState = rememberCameraPositionState{
        position = defaultCameraPosition
    }
    var isMapLoaded by remember {
        mutableStateOf(false)
    }

    val bottomSheetState = rememberBottomSheetScaffoldState()


    // updateSearchQuery : passed into CustomSearchBar and updates searchQuery on keystroke change
    val updateSearchQuery: (String) -> Unit = { newQuery ->
        viewModel.textState.value = newQuery
        viewModel.searchPlaces(newQuery)
    }

    LaunchedEffect(viewModel.currentLatLong) {
        viewModel.getAddress(viewModel.currentLatLong)
    }

    androidx.compose.material3.BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetPeekHeight = 200.dp,
        sheetContent = {

            // Icon, TextField, and Button for search input
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.Top,
                horizontalArrangement = Arrangement.Center
            ) {

                CustomSearchBar(
                    modifier = Modifier
                        .width(300.dp),
                    searchQuery = viewModel.textState.value,
                    placeHolder = "Search for climbing spots!",
                    maxLength = 100,
                    onUpdateSearch = updateSearchQuery
                )

                Button(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .size(55.dp),
                    onClick = {
                        val location = viewModel.textState.value
                        navController.navigate("${Destinations.AddHeight}/$location")
                    },
                    shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = colorResource(id = com.example.onlysends_compose.R.color.onlySends),
                        contentColor = colorResource(id = com.example.onlysends_compose.R.color.white)
                    ),
                ) {
                    Text(text = "+")
                }

            }

            // Autofill suggestions
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(viewModel.locationAutofill) { autofillItem ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                            .clickable {
                                viewModel.text = autofillItem.address
                                viewModel.locationAutofill.clear()
                                viewModel.getCoordinates(autofillItem)
                                // Update viewModel.currentLatLong
                            }
                    ) {
                        Text(autofillItem.address)
                    }
                }
            }
            Spacer(modifier = Modifier.padding(200.dp))
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
                        .align(Alignment.CenterVertically)

                )

            }

            MapDisplay(
                modifier = Modifier
                    .fillMaxSize(),
                onMapLoaded = {
                    isMapLoaded = true
                },
                viewModel = viewModel, // Create a LocationViewModel instance using viewModel()
                context = LocalContext.current // Obtain the Android context using LocalContext.current
            )
        }

    }
}

