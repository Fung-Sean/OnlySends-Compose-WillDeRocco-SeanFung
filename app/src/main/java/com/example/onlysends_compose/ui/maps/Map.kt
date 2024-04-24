package com.example.onlysends_compose.ui.maps

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

val bostonState = LatLng(
    42.3601,
    -71.0589
)
val defaultCameraPosition = CameraPosition.fromLatLngZoom(bostonState, 13f)

class Map {

}
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    cameraPositionState: CameraPositionState,
    onMapLoaded: () -> Unit
){
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

    GoogleMap (
        modifier = modifier,
        onMapLoaded = onMapLoaded,
        cameraPositionState = cameraPositionState,
        uiSettings = mapUiSettings,
        properties = mapProperties,
    ){
        MarkerInfoWindow {

        }

//        Marker(
//            state = locationState,
//            draggable = true,
//            onClick = {
//                if (showInfoWindow){
//                    locationState.showInfoWindow()
//                }else{
//                    locationState.hideInfoWindow()
//                }
//                showInfoWindow = !showInfoWindow
//
//                return@Marker false
//
//            },
//            title = "Boston Map title"
//        )
        MarkerInfoWindowContent(
            state = locationState,
            draggable = true,
            onClick = {
                if (showInfoWindow){
                    locationState.showInfoWindow()
                }else{
                    locationState.hideInfoWindow()
                }
                showInfoWindow = !showInfoWindow

                return@MarkerInfoWindowContent false

            },
            title = "Boston Map title"
        ){
            Text(text = "YOUR MOTHER")
        }

    }
}