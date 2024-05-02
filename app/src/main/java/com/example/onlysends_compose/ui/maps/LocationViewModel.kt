package com.example.onlysends_compose.ui.maps

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.type.LatLng

class LocationViewModel(private val context: Context) : ViewModel(){
    lateinit var fusedLocationClient: FusedLocationProviderClient


    var locationState by mutableStateOf<LocationState>(LocationState.NoPermission)
    fun requestLocationPermissions() {
        locationState = LocationState.LocationLoading
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions not granted, handle this scenario
            locationState = LocationState.NoPermission
            return
        }
        // Permissions granted, fetch current location
        getCurrentLocation()
    }

    // Function to enable location services
    fun enableLocationServices() {
        val locationRequest = LocationRequest.create()
        val builder = LocationSettingsRequest.Builder().addLocationRequest(locationRequest)
        val task = LocationServices.getSettingsClient(context).checkLocationSettings(builder.build())

        task.addOnSuccessListener(OnSuccessListener<LocationSettingsResponse> {
            // Location services enabled, fetch current location
            getCurrentLocation()
        })

        task.addOnFailureListener(OnFailureListener { e ->
            val statusCode = (e as com.google.android.gms.common.api.ResolvableApiException).statusCode
            if (statusCode == LocationSettingsStatusCodes.RESOLUTION_REQUIRED) {
                // Location services disabled, show dialog to enable
                // Implement logic to handle resolution, if needed
            }
        })
    }
    fun getCurrentLocation(){
        locationState = LocationState.LocationLoading
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions not granted, handle this scenario
            locationState = LocationState.NoPermission
            return
        }
        fusedLocationClient.getCurrentLocation(Priority.PRIORITY_HIGH_ACCURACY, null)
            .addOnSuccessListener { location ->
                locationState = if (location == null) {
                    LocationState.Error
                } else {
                    LocationState.LocationAvailable(
                        com.google.android.gms.maps.model.LatLng(location.latitude, location.longitude)
                    )
                }
            }
    }
}