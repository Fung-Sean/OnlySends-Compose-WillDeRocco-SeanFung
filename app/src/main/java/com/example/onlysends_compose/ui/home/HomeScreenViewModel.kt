package com.example.onlysends_compose.ui.home
import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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
    val postsUiState by mutableStateOf(PostsUiState())

    init {
        Log.d(TAG, "VIEWMODEL INTIALIZED")
        fetchData()
    }

    fun fetchData() {
        postsUiState.isLoading = true

        viewModelScope.launch {
            // Fetch data from Firestore using application context
            val postsFromFirestore = Firestore.handleGetFriendPosts(getApplication(), user)

            Log.d(TAG, "finished fetching posts: $postsFromFirestore")

            // Update UI state on the main/UI thread
            withContext(Dispatchers.Main) {
                postsUiState.isLoading = false
                postsUiState.posts = postsFromFirestore
                Log.d(TAG, "updated postUiState ${postsUiState.isLoading}")
            }
        }
    }
}

data class PostsUiState(
    var isLoading: Boolean = false,
    var posts: List<Post> = listOf(),
    val error: String? = null
)
