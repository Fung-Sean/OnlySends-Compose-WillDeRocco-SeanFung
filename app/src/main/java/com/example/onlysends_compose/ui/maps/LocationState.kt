package com.example.onlysends_compose.ui.maps

import com.google.type.LatLng
//Holds the information on where a user is
sealed class LocationState {
    object NoPermission: LocationState()
    object LocationDisabled: LocationState()
    object LocationLoading: LocationState()
    data class LocationAvailable(val location: com.google.android.gms.maps.model.LatLng): LocationState()
    object Error: LocationState()
}