package com.example.onlysends_compose.ui.maps

import com.google.type.LatLng

sealed class LocationState {
    object NoPermission: LocationState()
    object LocationDisabled: LocationState()
    object LocationLoading: LocationState()
    data class LocationAvailable(val location: com.google.android.gms.maps.model.LatLng): LocationState()
    object Error: LocationState()
}