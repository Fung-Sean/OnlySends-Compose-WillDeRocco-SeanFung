package com.example.onlysends_compose.ui.home

import android.util.Log
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.example.onlysends_compose.firestore.types.User
import com.example.onlysends_compose.ui.components.PostListItem
import com.example.onlysends_compose.ui.home.theme.OnlySendsTheme

private const val TAG = "HomeScreen"
@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    user: User,
    modifier: Modifier = Modifier,
    postsUiState: PostsUiState,
    fetchMoreData: () -> Unit
){

    val pullRefreshState = rememberPullRefreshState(
        refreshing = postsUiState.isLoading,
        onRefresh = { fetchMoreData() })


    // Use LaunchedEffect to trigger a side effect whenever isLoading changes
    LaunchedEffect(postsUiState.isLoading) {
        // Log the new value of isLoading
        Log.d(TAG, "isLoading: ${postsUiState.isLoading}")
        // Update the previousIsLoading value
    }

    Box (
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(state = pullRefreshState)
    ){
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
        ){
           items(items = postsUiState.posts){
               PostListItem(
                   post = it,
               )
           }
        }

        PullRefreshIndicator(refreshing = postsUiState.isLoading,
            state = pullRefreshState,
            modifier = modifier.align(Alignment.TopCenter))
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    OnlySendsTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomeScreen(
                user = User(),
                postsUiState = PostsUiState(posts = listOf()), // Provide appropriate PostsUiState here
                fetchMoreData = {},
            )
        }
    }
}