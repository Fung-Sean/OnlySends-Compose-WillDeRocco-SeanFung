package com.example.onlysends_compose.ui.home
import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.Post
import com.example.onlysends_compose.firestore.types.User
import kotlinx.coroutines.launch

private const val TAG = "HomeScreenViewModel"

class HomeViewModel(
    application: Application,
    private val user: User
) : AndroidViewModel(application) {

    // obtain context from application
    val context = getApplication<Application>()

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
            val postsFromFirestore = Firestore.handleGetFriendPosts(context, user)

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
