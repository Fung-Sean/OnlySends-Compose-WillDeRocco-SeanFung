package com.example.onlysends_compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlysends_compose.R

@Composable
fun PostListItem(
    modifier: Modifier = Modifier
){
    
}

@Composable
fun PostHeader(
    modifier: Modifier = Modifier,
    name: String,
    profileURL: String,
    date: String,
    onProfileClick: () -> Unit
){
    Row (
        modifier = modifier
            .fillMaxWidth()
            .padding(
                horizontal = 16.dp,
                vertical = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceEvenly
    ){
        CircleImage(imageUrl = profileURL, modifier = modifier.size(30.dp)) {
            onProfileClick()
        }
        Text(text = name, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)

        Box(modifier = modifier
            .size(4.dp)
            .clip(CircleShape)
            .background(
                color = Color.LightGray
            )
        )
        Text(
            text = date,
            style = MaterialTheme.typography.labelSmall.copy(
                textAlign = TextAlign.Start,
                fontSize = 12.sp,
                color = Color.LightGray
            ),
            modifier = modifier.weight(1f)
        )
        Icon(painter = painterResource(
            id = R.drawable.round_more_horiz_24), 
            contentDescription = null,
            tint = Color.LightGray
        )
    }
}

@Composable
fun PostLikesRow(
    modifier: Modifier = Modifier,
    likeCount: Int,
    commentCount: Int,
    onLikeClick: () -> Unit,
    onCommentClick: () -> Unit
){
    Row (
        modifier = modifier
            .fillMaxWidth()
            .padding(
                vertical = 0.dp,
                horizontal = 8.dp
            ),
        verticalAlignment = Alignment.CenterVertically
    ){
        IconButton(onClick = onLikeClick) {
            Icon(painter = painterResource(
                id = R.drawable.like_icon_outlined), 
                contentDescription = null,
                tint = Color.LightGray
            )
        }
        Text(
            text = "$likeCount",
            style = MaterialTheme.typography.bodyMedium.copy(
                fontSize = 18.sp
            )
        )
        Spacer(modifier = modifier.width(8.dp))
    }
}