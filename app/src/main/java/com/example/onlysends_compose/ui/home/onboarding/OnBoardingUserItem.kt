package com.example.onlysends_compose.ui.home.onboarding

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onlysends_compose.R
import com.example.onlysends_compose.components.unused.CircleImage
import com.example.onlysends_compose.components.unused.FollowsButton
import com.example.onlysends_compose.ui.home.fake_data.FollowsUser
import com.example.onlysends_compose.ui.home.fake_data.sampleUsers
import com.example.onlysends_compose.ui.home.theme.OnlySendsTheme

@Composable

fun OnBoardingUserItem(
    modifier: Modifier = Modifier,
    followsUser: FollowsUser,
    onUserClick: (FollowsUser) -> Unit,
    isFollowing: Boolean = false,
    onFollowButtonClick: (Boolean, FollowsUser) -> Unit
) {
    Card(
        modifier = modifier
            .size(height = 140.dp, width = 130.dp)
            .clickable {
                onUserClick(followsUser)
            },
        //elevation = 0.dp
    ) {
        // Content of your Card
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            CircleImage(
                modifier = modifier.size(50.dp),
                imageUrl = followsUser.profileUrl
            ) {
                onUserClick(followsUser)
            }
            
            Spacer(modifier = modifier.height(4.dp))

            Text(
                text = followsUser.name,
                style = MaterialTheme.typography.bodySmall,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = modifier.height(8.dp))

            FollowsButton(
                modifier = modifier.fillMaxWidth().height(30.dp),
                text = R.string.follow_button_text,
                onClick = { onFollowButtonClick(!isFollowing, followsUser) }
            )
        }
    }
}

@Preview
@Composable
private fun OnBoardingUserPreview(){
    OnlySendsTheme {
        OnBoardingUserItem(followsUser = sampleUsers.first(),
            onUserClick = {},
            onFollowButtonClick = { _, _ ->})
    }
}