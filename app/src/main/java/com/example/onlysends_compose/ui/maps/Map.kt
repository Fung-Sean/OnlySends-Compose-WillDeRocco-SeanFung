package com.example.onlysends_compose.ui.maps

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.MarkerInfoWindowContent
import com.google.maps.android.compose.rememberMarkerState
import androidx.compose.runtime.remember
import com.google.api.Context
import com.google.maps.android.compose.rememberCameraPositionState


val bostonState = LatLng(
    42.3601,
    -71.0589
)
val defaultCameraPosition = CameraPosition.fromLatLngZoom(bostonState, 13f)

class Map {

}
@Composable
fun MapDisplay(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState,
    onMapLoaded: () -> Unit,
    viewModel: LocationViewModel, // Add LocationViewModel parameter
    context: android.content.Context
) {
    LocationPermissionHandler(
        viewModel = viewModel,
        context = context
    ) // Call LocationPermissionHandler composable

    val locationState = rememberMarkerState(
        position = bostonState
    )

    val mapUiSettings by remember {
        mutableStateOf(MapUiSettings(compassEnabled = false))
    }
    val mapProperties by remember {
        mutableStateOf(MapProperties(mapType = MapType.NORMAL))
    }

    var showInfoWindow by remember {
        mutableStateOf(true)
    }

    AnimatedContent(
        viewModel.locationState, label = "Map"
    ) { state ->
        when (state) {
            is LocationState.NoPermission -> {
                Column {
                    Text("We need location permission to continue")
                    Button(onClick = { viewModel.requestLocationPermissions() }) {
                        Text("Request permission")
                    }
                }
            }
            is LocationState.LocationDisabled -> {
                Column {
                    Text("We need location to continue")
                    Button(onClick = { viewModel.enableLocationServices() }) {
                        Text("Enable location")
                    }
                }
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
                val mapUiSettings by remember { mutableStateOf(MapUiSettings()) }
                val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }

                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    uiSettings = mapUiSettings,
                    properties = mapProperties
                )
            }
        }
    }
}