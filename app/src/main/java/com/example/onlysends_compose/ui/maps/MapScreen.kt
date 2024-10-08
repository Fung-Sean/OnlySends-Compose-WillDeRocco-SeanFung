package com.example.onlysends_compose.ui.maps

import android.R
import android.app.Activity
import android.content.Context
import android.net.Uri
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CutCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.BottomSheetScaffold
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
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.example.onlysends_compose.MainActivity.Destinations
import com.example.onlysends_compose.components.generic.CustomSearchBar
import com.example.onlysends_compose.components.navigation.PageHeaderText
import com.example.onlysends_compose.firestore.types.User
import com.example.onlysends_compose.ui.home.theme.RoundedCornerShape
import com.example.onlysends_compose.ui.home.theme.buttonColor
import com.example.onlysends_compose.ui.home.theme.signOutColor
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch

//Composable for map screen and displaying the map using the Map.kt file as a template
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun MapScreen(
    navController: NavController,
    modifier: Modifier = Modifier,
    context: Context,
    activity: Activity,
    user: User
){
    val viewModel = remember {
        LocationViewModel(context, activity, user ) // Pass both context and activity
    }
    //Use viewmodel for background functions
    var isMapLoaded by remember {
        mutableStateOf(false)
    }
    //Sets the mapLoaded initially to false since we are unsure if there are permissions and of user location yet
    var bottomSheetVisible by remember { mutableStateOf(false) }
    //Initializes the state of the bottom sheet to false so that it is down initially
    val bottomSheetState = rememberBottomSheetScaffoldState()
    //Remembers the state of the bottom sheet based on whether it is up or down

    // updateSearchQuery : passed into CustomSearchBar and updates searchQuery on keystroke change
    val updateSearchQuery: (String) -> Unit = { newQuery ->
        viewModel.textState.value = newQuery
        viewModel.searchPlaces(newQuery)
    }

    //Launches with the current lattitude and longitude using viewModel function
    LaunchedEffect(viewModel.currentLatLong) {
        viewModel.getAddress(viewModel.currentLatLong)
    }
    //Ensures bottomSheetVisibility is initially down
    LaunchedEffect(Unit) {
//        focusRequester.requestFocus()
        bottomSheetVisible = true
    }
    BottomSheetScaffold(
        scaffoldState = bottomSheetState,
        sheetPeekHeight = 200.dp,
        sheetContent = {
            SheetContent(
                viewModel = viewModel,
                updateSearchQuery = updateSearchQuery,
                navController = navController
            )
        }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            PageHeaderText(text = "New Heights")

            MapDisplay(
                modifier = Modifier
                    .fillMaxSize(),
                onMapLoaded = {
                    isMapLoaded = true
                },
                viewModel = viewModel,
                context = LocalContext.current
            )
        }

    }
}

//Put all sheet content into a separate composable for readability.
@Composable
private fun SheetContent(
    viewModel: LocationViewModel,
    updateSearchQuery: (String) -> Unit,
    navController: NavController
) {
    Column(
//        modifier = Modifier.heightIn(min = 100.dp, max = 500.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
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
                    // Pass viewModel.currentLatLong to the destination
                    val lat = viewModel.currentLatLong.latitude
                    val long = viewModel.currentLatLong.longitude
                    navController.navigate("${Destinations.AddHeight}/$location/$lat/$long")
                },
                shape = RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp, bottomStart = 8.dp, bottomEnd = 8.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = colorResource(id = com.example.onlysends_compose.R.color.onlySends),
                    contentColor = colorResource(id = com.example.onlysends_compose.R.color.white)
                ),
                enabled = viewModel.textState.value != "Address not found" // Disable button if the address is not found
            ) {
                Text(text = "+")
            }
        }

        // Autofill suggestions
        LazyColumn(
            modifier = Modifier.weight(1f),
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
    }
}

