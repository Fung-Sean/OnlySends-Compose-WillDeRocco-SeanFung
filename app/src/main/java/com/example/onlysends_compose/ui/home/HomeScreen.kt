package com.example.onlysends_compose.ui.home

import android.content.res.Configuration
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.onlysends_compose.ui.components.PostListItem
import com.example.onlysends_compose.ui.home.fake_data.FollowsUser
import com.example.onlysends_compose.ui.home.fake_data.samplePosts
import com.example.onlysends_compose.ui.home.fake_data.sampleUsers
import com.example.onlysends_compose.ui.home.onboarding.OnBoardingSection
import com.example.onlysends_compose.ui.home.onboarding.OnBoardingUiState
import com.example.onlysends_compose.ui.home.theme.OnlySendsTheme

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    onBoardingUiState: OnBoardingUiState,
    postsUiState: PostsUiState,
    onPostClick: (com.example.onlysends_compose.ui.home.fake_data.Post) -> Unit, //Change to firestore
    onProfileClick: (Int) -> Unit,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit,
    onFollowButtonClick: (Boolean, FollowsUser) -> Unit,
    onBoardingFinish: () -> Unit,
    fetchMoreData: () -> Unit
){
    val pullRefreshState = rememberPullRefreshState(
        refreshing = onBoardingUiState.isLoading && postsUiState.isLoading,
        onRefresh = { fetchMoreData() })
    Box (
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(state = pullRefreshState)
    ){
        LazyColumn(
            modifier = modifier
                .fillMaxSize()
        ){
            if (onBoardingUiState.shouldShowOnBoarding){
                item (key = "onboardingsection"){
                    OnBoardingSection(
                        users = onBoardingUiState.users,
                        onUserClick = { onProfileClick(it.id) },
                        onFollowButtonClick = onFollowButtonClick
                    ) {
                        onBoardingFinish()

                    }
                }
            }

           items(items = postsUiState.posts, key = {post -> post.id}){
               PostListItem(
                   post = it,
                   onPostClick = onPostClick,
                   onProfileClick = onProfileClick,
                   onLikeClick = onLikeClick ,
                   onCommentClick = onCommentClick
               )
           }
        }

        PullRefreshIndicator(refreshing = onBoardingUiState.isLoading && postsUiState.isLoading,
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
                onBoardingUiState = OnBoardingUiState(users = sampleUsers, shouldShowOnBoarding = true),
                postsUiState = PostsUiState(posts = samplePosts), // Provide appropriate PostsUiState here
                onFollowButtonClick = { _, _ -> },
                onPostClick = {},
                onProfileClick = {},
                onLikeClick = {},
                onCommentClick = {},
                fetchMoreData = {},
                onBoardingFinish = {}
            )
        }
    }
}