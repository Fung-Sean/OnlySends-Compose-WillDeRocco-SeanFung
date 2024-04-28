package com.example.onlysends_compose.ui.profile
import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.User
import kotlinx.coroutines.launch

private const val TAG = "ProfileViewModel"
class ProfileViewModel(
    application: Application,
    private val user: User,
    private val onUpdateUser: (User) -> Unit
) : AndroidViewModel(application) {

    // obtain context from application
    val context = getApplication<Application>()

    // Define MutableState properties directly
    val profileUiState: MutableState<ProfileUiState> =  mutableStateOf(ProfileUiState(user = user))

    init {
        // "create user doc" (really just updates the user)
        Firestore.handleCreateUserDocument(
            user = user,
            onUpdateUser = onUpdateUser
        )
    }

    // updateProfile : updates user profile with username and climbStyle (and calls fetchData)
    fun updateProfile(
        newUsername: String,
        newClimbStyle: String
    ) {
        Firestore.handleUpdateUserProfile(
            context = context,
            userId = user.userId,
            newUsername = newUsername,
            newClimbStyle = newClimbStyle,
            onUpdateUser = onUpdateUser
        )
    }
}


data class ProfileUiState(
    var isLoading: Boolean = false,
    var user: User,
    val error: String? = null
)
