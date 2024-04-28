package com.example.onlysends_compose.ui.friends
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

private const val TAG = "FriendsViewModel"
class FriendsViewModel(
    application: Application,
    private val user: User
) : AndroidViewModel(application) {

    // obtain context from application
    val context = getApplication<Application>()

    // Define MutableState properties directly
    val friendsUiState: MutableState<FriendsUiState> =  mutableStateOf(FriendsUiState())

    init {
        fetchData()
    }

    // fetchData : updates friendsUiState with list of potential friends
    fun fetchData() {
        friendsUiState.value.isLoading = true

        viewModelScope.launch {
            // Fetch data from Firestore using application context
            val usersFromFirestore = Firestore.handleSearchUserFriends(user)

            friendsUiState.value.isLoading = false
            friendsUiState.value.friends.clear()
            friendsUiState.value.friends.addAll(usersFromFirestore)
            Log.d(TAG, "updated friendsUiState ${friendsUiState.value.isLoading} ${friendsUiState.value.friends.toList()}")
        }
    }

}


data class FriendsUiState(
    var isLoading: Boolean = false,
    var friends: SnapshotStateList<User> = mutableStateListOf(),
    val error: String? = null
)
