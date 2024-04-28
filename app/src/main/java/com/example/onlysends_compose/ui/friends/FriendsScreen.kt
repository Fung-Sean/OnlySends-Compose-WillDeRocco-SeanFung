package com.example.onlysends_compose.ui.friends

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.onlysends_compose.components.friends.FriendItem
import com.example.onlysends_compose.components.friends.SearchFriendItem
import com.example.onlysends_compose.components.navigation.PageHeaderText
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.User
import com.example.onlysends_compose.ui.home.theme.buttonColor
import com.example.onlysends_compose.ui.search.SearchUiState
import kotlin.reflect.KFunction1

private const val TAG = "FriendsScreen"

// FriendsScreen : composable function that allows user to view all current friends
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun FriendsScreen(
    modifier: Modifier = Modifier,
    friendsUiState: FriendsUiState,
    fetchMoreData: () -> Unit,
) {
    val pullRefreshState = rememberPullRefreshState(
        refreshing = friendsUiState.isLoading,
        onRefresh = { fetchMoreData() })
    
    // Render the UI using the list of potentialFriends
    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(state = pullRefreshState)
    ) {
        PageHeaderText(text = "Find Friends")

        LazyColumn(
            modifier = modifier
                .fillMaxSize()
        ){
            items(
                items = friendsUiState.friends,
            ){
                FriendItem(friend = it)
            }
        }

        PullRefreshIndicator(refreshing = friendsUiState.isLoading,
            state = pullRefreshState,
            modifier = modifier.align(Alignment.TopCenter)
        )
    }
}

