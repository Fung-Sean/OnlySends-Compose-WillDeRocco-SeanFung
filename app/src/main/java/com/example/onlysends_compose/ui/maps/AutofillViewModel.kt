package com.example.onlysends_compose.ui.maps

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

class AutofillViewModel : ViewModel(){
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