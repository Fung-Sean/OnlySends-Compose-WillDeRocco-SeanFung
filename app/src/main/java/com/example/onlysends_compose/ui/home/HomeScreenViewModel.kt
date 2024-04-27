package com.example.onlysends_compose.ui.home
import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.Post
import com.example.onlysends_compose.firestore.types.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

private const val TAG = "HomeScreenViewModel"

class HomeScreenViewModel(
    application: Application,
    private val user: User
) : AndroidViewModel(application) {

    // Define MutableState properties directly
    val postsUiState: MutableState<PostsUiState> =  mutableStateOf(PostsUiState())

    init {
        Log.d(TAG, "init method (fetching data)")
        fetchData()
    }

    fun fetchData() {
        postsUiState.value.isLoading = true

        viewModelScope.launch {
            // Fetch data from Firestore using application context
            val postsFromFirestore = Firestore.handleGetFriendPosts(getApplication(), user)

            postsUiState.value.isLoading = false
            postsUiState.value.posts.clear()
            postsUiState.value.posts.addAll(postsFromFirestore)
            Log.d(TAG, "updated postUiState ${postsUiState.value.isLoading} ${postsUiState.value.posts}")
        }
    }
}

data class PostsUiState(
    var isLoading: Boolean = false,
    var posts: SnapshotStateList<Post> = mutableStateListOf(),
    val error: String? = null
)
