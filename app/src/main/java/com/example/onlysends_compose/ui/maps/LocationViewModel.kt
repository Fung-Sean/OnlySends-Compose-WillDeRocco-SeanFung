package com.example.onlysends_compose.ui.maps

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager
import android.location.Geocoder
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.FriendRequest
import com.example.onlysends_compose.firestore.types.MapLocation
import com.example.onlysends_compose.firestore.types.User
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.LocationSettingsRequest
import com.google.android.gms.location.LocationSettingsResponse
import com.google.android.gms.location.LocationSettingsStatusCodes
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.IOException


const val REQUEST_LOCATION_PERMISSIONS = 1001 // Define your desired request code - GPT
const val TAG = "LocationViewModel"

class LocationViewModel(
    private val context: Context,
    private val activity: Activity,
    private val user: User
) : ViewModel(){

    // Initialize placesClient and goeCoder
    private var placesClient: PlacesClient = Places.createClient(context)
    private var geoCoder: Geocoder = Geocoder(context)

    // initialize list of MapLocations
    val locations: SnapshotStateList<MapLocation> = mutableStateListOf()

    init {
        // fetch all MapLocations of user/friends
        fetchData()
    }

    // fetchData : updates searchUiState with list of potential friends
    private fun fetchData() {
        viewModelScope.launch {
            // Fetch data from Firestore using application context
            val locationsFromFirestore =
                Firestore.handleGetHeights(
                    context = context,
                    user = user
                )

            locations.clear()
            locations.addAll(locationsFromFirestore)

            Log.d(TAG, "updated locationsFromFirestore ${locations.toList()}")
        }
    }

    private val fusedLocationClient: FusedLocationProviderClient by lazy {
        LocationServices.getFusedLocationProviderClient(context)
    }
    var currentLatLong by mutableStateOf(com.google.android.gms.maps.model.LatLng(0.0, 0.0))

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
    //Function that gets the current location of the device
    //Assumes permissions are granted.
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
                    currentLatLong = LatLng(location.latitude, location.longitude)

                    LocationState.LocationAvailable(
                        com.google.android.gms.maps.model.LatLng(location.latitude, location.longitude)
                    )
                }
            }
    }

    //HANDLES ALL THE STUFF FOR THE PLACES API
    val locationAutofill = mutableStateListOf<AutocompleteResult>()
    private var searchJob: Job? = null // Renamed job to searchJob for clarity
    //searchPlaces allows the lazy column in the bottom sheet to be populated with guesses for the user's search
    fun searchPlaces(query: String) {
        searchJob?.cancel()
        locationAutofill.clear()

        searchJob = viewModelScope.launch {
            val request = FindAutocompletePredictionsRequest
                .builder()
                .setQuery(query)
                .build()

            try {
                val response = placesClient.findAutocompletePredictions(request).await()

                locationAutofill.addAll(response.autocompletePredictions.map {
                    AutocompleteResult(
                        it.getFullText(null).toString(),
                        it.placeId,
                    )
                })
                textState.value = query // Update textState value here
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    //retrieve the coordinates (latitude and longitude) of a place selected from an autocomplete result
    fun getCoordinates(result: AutocompleteResult) {
        val placeFields = listOf(Place.Field.LAT_LNG)
        val request = FetchPlaceRequest.newInstance(result.placeId, placeFields)
        placesClient.fetchPlace(request)
            .addOnSuccessListener {
                if (it != null) {
                    currentLatLong = it.place.latLng!!
                }
            }
            .addOnFailureListener {
                it.printStackTrace()
            }
        Log.d(TAG, "CurrentLatLong is $currentLatLong")
    }
    val textState = mutableStateOf("")
    var text = ""
    //Gets the address of the current place in the center of the map.
    fun getAddress(latLng: LatLng) {
        viewModelScope.launch {
            try {
                val addressList = geoCoder.getFromLocation(latLng.latitude, latLng.longitude, 1)
                if (addressList != null && addressList.isNotEmpty()) {
                    // Get the first address from the list
                    val address = addressList[0].getAddressLine(0)
                    textState.value = address ?: "Address not found"
                } else {
                    textState.value = "Address not found"
                }
            } catch (e: Exception) {
                // Handle any exceptions that may occur
                e.printStackTrace()
                textState.value = "Error fetching address"
            }
        }
    }
}

//Creates a data class for the autocomplete strings and the values held by them
data class AutocompleteResult(
    val address: String,
    val placeId: String,
)

//data class LocationUiState(
//    var isLoading: Boolean = false,
//    val error: String? = null
//)


