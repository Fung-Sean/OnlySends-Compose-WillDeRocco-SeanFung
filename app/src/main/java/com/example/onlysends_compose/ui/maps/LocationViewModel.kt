package com.example.onlysends_compose.ui.maps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider.Factory
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.type.LatLng
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

const val REQUEST_LOCATION_PERMISSIONS = 1001 // Define your desired request code

class LocationViewModel(private val context: Context, private val activity: Activity) : ViewModel(){

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }

    var locationState by mutableStateOf<LocationState>(LocationState.NoPermission)
    fun requestLocationPermissions() {
        if (ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Permissions not granted, request them
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ),
                REQUEST_LOCATION_PERMISSIONS
            )
        } else {
            // Permissions already granted, fetch current location
            getCurrentLocation()
        }
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

    //HANDLES ALL THE STUFF FOR THE PLACES API
    val locationAutofill = mutableStateListOf<AutocompleteResult>()
    private var searchJob: Job? = null // Renamed job to searchJob for clarity

    fun searchPlaces(query: String) {
        searchJob?.cancel() // Cancel the previous search job if it exists
        locationAutofill.clear() // Clear the autofill list before populating with new suggestions

        searchJob = viewModelScope.launch {
            val request = FindAutocompletePredictionsRequest
                .builder()
                .setQuery(query)
                .build()

            try {
                // Perform the API call to find autocomplete predictions
                val response = placesClient.findAutocompletePredictions(request).await()

                // Map the response to AutocompleteResult objects and add them to locationAutofill
                locationAutofill.addAll(response.autocompletePredictions.map {
                    AutocompleteResult(
                        it.getFullText(null).toString(),
                        it.placeId
                    )
                })
            } catch (e: Exception) {
                // Handle any exceptions that may occur during the API call
                e.printStackTrace()
                // Log or display appropriate error messages
            }
        }
    }
}


