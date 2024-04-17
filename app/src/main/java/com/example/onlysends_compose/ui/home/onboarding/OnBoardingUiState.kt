package com.example.onlysends_compose.ui.home.onboarding

import com.example.onlysends_compose.ui.home.fake_data.FollowsUser
import com.example.onlysends_compose.ui.home.fake_data.Post

data class OnBoardingUiState(
    val isLoading: Boolean = false,
    val users: List<FollowsUser> = listOf(), //CHANGE TO FIRESTORE
    val error: String?= null,
    val shouldShowOnBoarding: Boolean = false
)
