package com.example.onlysends_compose.ui.search

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onlysends_compose.components.friends.SearchFriendItem
import com.example.onlysends_compose.components.generic.CustomSearchBar
import com.example.onlysends_compose.components.navigation.PageHeaderText
import com.example.onlysends_compose.firestore.types.User

private const val TAG = "SearchScreen"

// SearchScreen : composable function that allows user to search for friends and accept/delete requests
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun SearchScreen(
    modifier: Modifier = Modifier,
    searchUiState: SearchUiState,
    fetchMoreData: () -> Unit,
    onFollowFriend: (User) -> Unit,
    onAcceptFriend: (User) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") } // Remember the search query

    val filteredFriends = searchUiState.potentialFriends.filter {
        it.friend.username.contains(searchQuery, ignoreCase = true)
    }

    // updateSearchQuery : passed into CustomSearchBar and updates searchQuery on keystroke change
    val updateSearchQuery: (String) -> Unit = { newQuery ->
        searchQuery = newQuery
    }

    val pullRefreshState = rememberPullRefreshState(
        refreshing = searchUiState.isLoading,
        onRefresh = { fetchMoreData() })

    // Render the UI using the list of potentialFriends
    Box(
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(state = pullRefreshState)
            .padding(10.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            PageHeaderText(text = "Find Friends")

            CustomSearchBar(
                searchQuery = searchQuery,
                placeHolder = "Search friends by name",
                maxLength = 50,
                onUpdateSearch = updateSearchQuery
            )

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
                    .padding(bottom = 60.dp),
            ){
                items(
                    items = filteredFriends,
                ){
                    SearchFriendItem(
                        friendRequest = it,
                        onFollowFriend = onFollowFriend,
                        onAcceptFriend = onAcceptFriend
                    )
                }
            }
        }

        PullRefreshIndicator(refreshing = searchUiState.isLoading,
            state = pullRefreshState,
            modifier = modifier.align(Alignment.TopCenter)
        )
    }
}


@Preview
@Composable
private fun SearchScreenPreview() {
    SearchScreen(
        searchUiState = SearchUiState(),
        fetchMoreData = {  },
        onFollowFriend = { },
        onAcceptFriend = { }
    )
}