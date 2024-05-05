package com.example.onlysends_compose.ui.maps

import android.util.Log
import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CenterFocusWeak
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.example.onlysends_compose.R
import com.example.onlysends_compose.firestore.types.MapLocation
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
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerInfoWindow
import com.google.maps.android.compose.rememberCameraPositionState

// Function to convert address to LatLng coordinates

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

    val mapUiSettings by remember { mutableStateOf(MapUiSettings()) }
    val mapProperties by remember { mutableStateOf(MapProperties(isMyLocationEnabled = true)) }

    AnimatedContent(
        viewModel.locationState, label = "Map",
        contentAlignment = Alignment.Center
    ) { state ->
        when (state) {
            is LocationState.NoPermission -> {
                viewModel.requestLocationPermissions()
            }
            is LocationState.LocationDisabled -> {
                viewModel.enableLocationServices()
            }

            is LocationState.LocationLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 200.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(20.dp))
                        Text("Loading Map")
                    }
                }
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
                    properties = mapProperties,
                    onMapLoaded = onMapLoaded,
                ) {
                    // Add markers for each MapLocation
                    viewModel.locations.forEach { location ->
                        val latLng = LatLng(location.latLng.latitude, location.latLng.longitude)
                        MarkerInfoWindow(
                            state = rememberMarkerState( position = latLng),
                            title = location.siteName,
                        ) {
                            CustomInfoWindowContent(title = location.siteName, snippet = location.notes)
                        }
//                        Marker(
//                            state = rememberMarkerState( position = latLng),
//                            title = location.siteName,
//                            snippet = location.notes,
//                            // You can customize marker icon if needed
//                            // icon = BitmapDescriptorFactory.fromResource(R.drawable.your_custom_marker_icon)
//                        )
                    }
                }

                LaunchedEffect(viewModel.currentLatLong) {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLng(viewModel.currentLatLong))
                    Log.d(TAG, "LaunchEffect Launched, ${viewModel.currentLatLong}")
                }
            }
        }
    }
}

@Composable
private fun CustomInfoWindowContent(title: String, snippet: String) {
    Box(
        modifier = Modifier
            .padding(16.dp)
            .background(color = colorResource(id = R.color.white))
            .widthIn(max = 250.dp)
//            .heightIn(max = 100.dp)
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp)
                .verticalScroll(rememberScrollState()) // Apply vertical scrolling directly on Column
        ) {
            Text(text = title, fontWeight = FontWeight.Bold)
            Text(text = snippet)
        }
    }
}