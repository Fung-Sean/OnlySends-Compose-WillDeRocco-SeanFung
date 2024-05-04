package com.example.onlysends_compose.ui.maps

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapType
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberMarkerState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState


val bostonState = LatLng(
    42.3601,
    -71.0589
)
val defaultCameraPosition = CameraPosition.fromLatLngZoom(bostonState, 13f)
@Composable
fun MapDisplay(
    modifier: Modifier = Modifier,

    onMapLoaded: () -> Unit,
    viewModel: LocationViewModel, // Add LocationViewModel parameter
    context: android.content.Context,

) {
    LocationPermissionHandler(
        viewModel = viewModel,
        context = context
    ) // Call LocationPermissionHandler composable

    val locationState = rememberMarkerState(
        position = bostonState
    )


    val mapUiSettings by remember { mutableStateOf(MapUiSettings()) }
    val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }


    var showInfoWindow by remember {
        mutableStateOf(true)
    }

    AnimatedContent(
        viewModel.locationState, label = "Map"
    ) { state ->
        when (state) {
            is LocationState.NoPermission -> {
                viewModel.requestLocationPermissions()
            }
            is LocationState.LocationDisabled -> {
                viewModel.enableLocationServices()
            }

            is LocationState.LocationLoading -> {
                Text("Loading Map")
            }

            is LocationState.Error -> {
                Column {
                    Text("Error fetching your location")
                    Button(onClick = { viewModel.getCurrentLocation() }) {
                        Text("Retry")
                    }
                }
            }

            is LocationState.LocationAvailable -> {
                val cameraPositionState = rememberCameraPositionState {
                    position = CameraPosition.fromLatLngZoom(state.location, 15f)
                }


                LaunchedEffect(cameraPositionState.isMoving) {
                    if (!cameraPositionState.isMoving) {
                        viewModel.getAddress(cameraPositionState.position.target)
                    }
                }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = mapUiSettings,
                    properties = mapProperties
                )
                LaunchedEffect(viewModel.currentLatLong) {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLng(viewModel.currentLatLong))
                    Log.d(TAG, "LaunchEffect Launched, ${viewModel.currentLatLong}")
                }
            }
        }
    }
}