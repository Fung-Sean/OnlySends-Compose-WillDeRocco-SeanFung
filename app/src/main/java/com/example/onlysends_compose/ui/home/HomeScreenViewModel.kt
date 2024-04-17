package com.example.onlysends_compose.ui.home
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlysends_compose.ui.home.fake_data.Post
import com.example.onlysends_compose.ui.home.fake_data.samplePosts
import com.example.onlysends_compose.ui.home.fake_data.sampleUsers
import com.example.onlysends_compose.ui.home.onboarding.OnBoardingUiState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class HomeScreenViewModel : ViewModel() {
    // Define MutableState properties directly
    var onBoardingUiState by mutableStateOf(OnBoardingUiState())
        private set

    var postsUiState by mutableStateOf(PostsUiState())
        private set

    init{
        fetchData()
    }
    fun fetchData(){
        onBoardingUiState = onBoardingUiState.copy(isLoading = true)
        postsUiState = postsUiState.copy(isLoading = true)
        viewModelScope.launch {
            delay(1000)

            onBoardingUiState=onBoardingUiState.copy(
                isLoading = false,
                users = sampleUsers,
                shouldShowOnBoarding = true
            )
            postsUiState=postsUiState.copy(
                isLoading = false,
                posts = samplePosts,

            )
        }
    }
}

data class PostsUiState(
    val isLoading: Boolean = false,
    val posts: List<Post> = listOf(), //CHANGE TO FIRESTORE
    val error: String? = null
)
