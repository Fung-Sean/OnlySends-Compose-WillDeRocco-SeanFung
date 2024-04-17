package com.example.onlysends_compose.ui.home

import androidx.compose.runtime.Composable
import androidx.navigation.ActivityNavigator
import androidx.navigation.NavDestinationDsl

@Composable
fun Home(
    navigator: ActivityNavigator
){
    val viewModel: HomeScreenViewModel = HomeScreenViewModel()

    HomeScreen(
        onBoardingUiState = viewModel.onBoardingUiState,
        postsUiState = viewModel.postsUiState,
        onPostClick = {},
        onProfileClick = {},
        onLikeClick = {},
        onCommentClick = {},
        onFollowButtonClick = {_, _ ->},
        onBoardingFinish = {},
        fetchMoreData = {
            viewModel.fetchData()
        }
    )
}