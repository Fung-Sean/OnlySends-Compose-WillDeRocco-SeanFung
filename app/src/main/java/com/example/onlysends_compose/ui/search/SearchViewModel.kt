package com.example.onlysends_compose.ui.search
import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.FriendRequest
import com.example.onlysends_compose.firestore.types.User
import kotlinx.coroutines.launch

private const val TAG = "SearchViewModel"

class SearchViewModel(
    application: Application,
    private val user: User
) : AndroidViewModel(application) {

    // obtain context from application
    val context = getApplication<Application>()

    // Define MutableState properties directly
    val searchUiState: MutableState<SearhUiState> =  mutableStateOf(SearhUiState())

    init {
        Log.d(TAG, "init method (fetching data)")
        fetchData()
    }

    fun fetchData() {
        searchUiState.value.isLoading = true

        viewModelScope.launch {
            // Fetch data from Firestore using application context
            val usersFromFirestore = Firestore.handleSearchAllFriends(user)

            searchUiState.value.isLoading = false
            searchUiState.value.potentialFriends.clear()
            searchUiState.value.potentialFriends.addAll(usersFromFirestore)
            Log.d(TAG, "updated searchUiState ${searchUiState.value.isLoading} ${searchUiState.value.potentialFriends}")
        }
    }
}

data class SearhUiState(
    var isLoading: Boolean = false,
    var potentialFriends: SnapshotStateList<FriendRequest> = mutableStateListOf(),
    val error: String? = null
)
