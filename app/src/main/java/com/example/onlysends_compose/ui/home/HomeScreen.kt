package com.example.onlysends_compose.ui.home

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onlysends_compose.components.navigation.PageHeaderText
import com.example.onlysends_compose.components.posts.PostListItem
import com.example.onlysends_compose.ui.home.theme.OnlySendsTheme

private const val TAG = "HomeScreen"
@OptIn(ExperimentalMaterialApi::class, ExperimentalFoundationApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    postsUiState: PostsUiState,
    fetchMoreData: () -> Unit
){


    val pullRefreshState = rememberPullRefreshState(
        refreshing = postsUiState.isLoading,
        onRefresh = { fetchMoreData() })

//    Log.d(TAG, "isLoading: ${postsUiState.isLoading} posts: ${postsUiState.posts.toList()}")

    Box (
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(state = pullRefreshState)
    ){
        Column(modifier = Modifier.fillMaxSize()) {
            PageHeaderText(
                modifier = Modifier
                    .padding(10.dp),
                text = "Explore"
            )

            LazyColumn(
                modifier = modifier
                    .fillMaxSize()
            ){
               items(
                   items = postsUiState.posts,
               ){
                   Log.d(TAG, "posts are ${postsUiState.posts}")
                   PostListItem(
                       post = it,
                   )
               }
            }
        }


        PullRefreshIndicator(refreshing = postsUiState.isLoading,
            state = pullRefreshState,
            modifier = modifier.align(Alignment.TopCenter)
        )
    }
}

@Preview
@Composable
private fun HomeScreenPreview() {
    OnlySendsTheme {
        Surface(color = MaterialTheme.colors.background) {
            HomeScreen(
                postsUiState = PostsUiState(),
                fetchMoreData = { }
            )
        }
    }
}