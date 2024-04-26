package com.example.onlysends_compose.ui.home

import android.app.Application
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
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.PointerIcon.Companion.Text
import androidx.compose.ui.text.style.TextAlign
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
    application: Application,
){
    // Initialize the ViewModel
    val viewModel: HomeScreenViewModel = remember {
        HomeScreenViewModel(
            application = application,
            user = user
        )
    }

    // Retrieve postsUiState from the ViewModel
    val postsUiState by viewModel.postsUiState

    val pullRefreshState = rememberPullRefreshState(
        refreshing = postsUiState.isLoading,
        onRefresh = { viewModel.fetchData() })


    Log.d(TAG, "isLoading: ${postsUiState.isLoading} posts: ${postsUiState.posts.toList()}")


    Box (
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(state = pullRefreshState)
    ){
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
//        // display "Full Name" text
//        Text(
//            text = "posts area" + postsUiState.posts.toString(),
//            textAlign = TextAlign.Left,
//            fontSize = 18.sp
//        )
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
                application = Application()
            )
        }
    }
}