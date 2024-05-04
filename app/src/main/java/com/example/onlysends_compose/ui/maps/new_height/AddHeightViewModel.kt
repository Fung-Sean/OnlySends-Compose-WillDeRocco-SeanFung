package com.example.onlysends_compose.ui.maps.new_height
import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.User
import kotlinx.coroutines.launch

private const val TAG = "AddHeightViewModel"
class AddHeightViewModel(
    application: Application,
    private val user: User,
    siteLocation: MutableState<String>,
    private val onSuccess: () -> Unit
) : AndroidViewModel(application) {

    // obtain context from application
    val context = getApplication<Application>()

    // Define MutableState properties directly
    val addHeightUiState: MutableState<AddHeightUiState> =  mutableStateOf(AddHeightUiState())

    init {
        addHeightUiState.value.siteLocation = siteLocation
        addHeightUiState.value.siteName = mutableStateOf(siteLocation.value) // can be altered by user
        Log.d(TAG, "initialized addHeightUiState ${addHeightUiState.value}")
    }


    // addHeight : calls handleAddHeight in Firestore and fetches new data
    fun addHeight() {
        Log.d(TAG, "adding height ${addHeightUiState.value}")
        viewModelScope.launch {
            // call handleAddHeight with fetchData as callback function
            Firestore.handleAddHeight(
                context = context,
                user = user,
                addHeightUiState = addHeightUiState.value,
                onSuccess = onSuccess
            )
        }
    }
}

data class AddHeightUiState(
    var isLoading: Boolean = false,
    var siteLocation: MutableState<String> = mutableStateOf(""),
    var siteName: MutableState<String> = mutableStateOf(""),
    var notes: MutableState<String> = mutableStateOf(""),
    val error: String? = null
)
