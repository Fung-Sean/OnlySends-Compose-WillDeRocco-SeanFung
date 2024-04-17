package com.example.onlysends_compose.ui.home.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onlysends_compose.R
import com.example.onlysends_compose.ui.home.fake_data.FollowsUser
import com.example.onlysends_compose.ui.home.fake_data.sampleUsers
import com.example.onlysends_compose.ui.home.theme.OnlySendsTheme
import com.example.onlysends_compose.ui.home.theme.RoundedCornerShape

@Composable
fun OnBoardingSection(
    modifier: Modifier = Modifier,
    users: List<FollowsUser>,
    onUserClick: (FollowsUser) -> Unit,
    onFollowButtonClick: (Boolean, FollowsUser) -> Unit,
    onBoardingFinish: () -> Unit
){
    Column (
        modifier = modifier.fillMaxWidth()
    ){
        Text(text = stringResource(id = R.string.onboarding_title),
            modifier = modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            style = MaterialTheme.typography.headlineMedium,
            textAlign = TextAlign.Center
        )
       Text(text = stringResource(id = R.string.onboarding_description),
           modifier = modifier
               .fillMaxWidth()
               .padding(horizontal = 16.dp),
           style = MaterialTheme.typography.bodyMedium,
           textAlign = TextAlign.Center
           )
        Spacer(modifier = modifier.heightIn(16.dp))
        
        UsersRow(users = users, onUserClick = onUserClick, onFollowButtonClick = onFollowButtonClick)
        
        OutlinedButton(
            onClick = onBoardingFinish,
            modifier = modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth(fraction = 0.5f)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape()
        ) {
            Text(text = stringResource(id = R.string.onboarding_done_button))
        }
    }
}

@Composable
private fun UsersRow(
    modifier: Modifier = Modifier,
    users: List<FollowsUser>,
    onUserClick: (FollowsUser) -> Unit,
    onFollowButtonClick: (Boolean, FollowsUser) -> Unit
) {
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        modifier = modifier
    ) {
        items(items = users, key = {followsUser -> followsUser.id}) {
            OnBoardingUserItem(
                followsUser = it,
                onUserClick = onUserClick,
                onFollowButtonClick = onFollowButtonClick
            )
        }
    }
}

@Preview
@Composable
private fun OnBoardingSectionPreview(){
    OnlySendsTheme {
        Surface {
            OnBoardingSection(
                users = sampleUsers,
                onUserClick = {},
                onFollowButtonClick = { _, _ ->},
            ) {

            }
        }
    }
}