package com.example.onlysends_compose.ui.home

import android.graphics.drawable.Icon
import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiPeople
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Landscape
import androidx.compose.material.icons.outlined.EmojiPeople
import androidx.compose.material.icons.outlined.Group
import androidx.compose.material.icons.outlined.Landscape
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.Icon
import androidx.compose.material3.Tab
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.onlysends_compose.R
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
    val tabItems = listOf(
        TabItem(
            title = "Hangboard",
            unselectedIcon = Icons.Outlined.Group,
            selectedIcon = Icons.Filled.Group
        ),
        TabItem(
            title = "Your wall",
            unselectedIcon = Icons.Outlined.Landscape,
            selectedIcon = Icons.Filled.Landscape
        )
    )

    var selectedTabIndex by remember {
        mutableIntStateOf(0)
    }

    val pagerState = rememberPagerState {
        tabItems.size
    }
    
    // update HorizontalPager every time selectedTabIndex changes
    LaunchedEffect(selectedTabIndex) {
        pagerState.animateScrollToPage(selectedTabIndex)
    }

    // update selectedTabIndex every time Horizontal pager changes
    LaunchedEffect(pagerState.currentPage) {
        selectedTabIndex = pagerState.currentPage
    }
//    // update selectedTabIndex every time Horizontal pager changes
//    LaunchedEffect(pagerState.currentPage, pagerState.isScrollInProgress) {
//        // this doesn't matter much with only 2 tabs (but fixes swipe issues with > 2 tabs)
//        if (!pagerState.isScrollInProgress) {
//            selectedTabIndex = pagerState.currentPage
//        }
//    }


    // filter posts by either friends or just your own posts (i.e. "hangboard" or "wall")

    Log.d(TAG, "posts are ${postsUiState.posts.toList()}")

    // pullRefreshState : keeps track of "refreshing" loading status and fetches data
    val pullRefreshState = rememberPullRefreshState(
        refreshing = postsUiState.isLoading,
        onRefresh = { fetchMoreData() })

    Box (
        modifier = modifier
            .fillMaxSize()
            .pullRefresh(state = pullRefreshState)
    ){
        Column(modifier = Modifier.fillMaxSize()) {
//            PageHeaderText( text = "Explore" )

            TabRow(
                selectedTabIndex = selectedTabIndex,
                backgroundColor = colorResource(id = R.color.white),
            ) {
                tabItems.forEachIndexed { index, item ->
                    Tab(
                        selected = index == selectedTabIndex,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = item.title) },
                        icon = {
                            Icon(
                                imageVector = if (index == selectedTabIndex) { item.selectedIcon} else { item.unselectedIcon },
                                contentDescription = item.title
                            )
                        }
                    )
                }
            }
            
            HorizontalPager(
                state = pagerState,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
            ) {index ->
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


        }

        PullRefreshIndicator(refreshing = postsUiState.isLoading,
            state = pullRefreshState,
            modifier = modifier.align(Alignment.TopCenter)
        )
    }
}

data class TabItem(
    val title: String,
    val unselectedIcon: ImageVector,
    val selectedIcon: ImageVector
)

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