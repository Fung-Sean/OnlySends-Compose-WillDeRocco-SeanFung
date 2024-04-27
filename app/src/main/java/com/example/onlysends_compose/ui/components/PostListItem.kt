package com.example.onlysends_compose.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.onlysends_compose.firestore.types.Post
import com.example.onlysends_compose.ui.home.theme.OnlySendsTheme
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("MMM dd, yyyy hh:mm a", Locale.getDefault())
    val date = Date(timestamp)
    return sdf.format(date)
}

@Composable
fun PostListItem(
    modifier: Modifier = Modifier,
    post: Post,
    isDetailScreen: Boolean = false
){
    Column (
        modifier = modifier
            .fillMaxWidth()
            .aspectRatio(ratio = 0.75f)
            .background(color = MaterialTheme.colorScheme.surface)
    ){
        PostHeader(
            name = post.username,
            profileURL = post.profilePictureUrl,
            date = formatDate(post.timestamp),
        )
        AsyncImage(model = post.postPictureUrl,
            contentDescription = null,
            modifier = modifier
                .fillMaxWidth()
                .aspectRatio(ratio = 1.0f),
            contentScale = ContentScale.Crop,
            placeholder = if(androidx.compose.material.MaterialTheme.colors.isLight){
                painterResource(id = com.example.onlysends_compose.R.drawable.light_image_place_holder)
            }else{
                painterResource(id = com.example.onlysends_compose.R.drawable.dark_image_place_holder)
            }
        )
        Text(
            text = post.caption,
            style = MaterialTheme.typography.bodyMedium.copy(
                color = Color.DarkGray
            ),
            modifier = modifier
                .padding(horizontal = 16.dp),
            maxLines = if (isDetailScreen){
                20
            }else{
                2
            },
            overflow = TextOverflow.Ellipsis
            )

    }
}

@Composable
fun PostHeader(
    modifier: Modifier = Modifier,
    name: String,
    profileURL: String?,
    date: String,
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
        }

        Spacer(modifier = Modifier.width(10.dp))

        Text(text = name, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurface)

        Spacer(modifier = Modifier.width(10.dp))

        Box(modifier = modifier
            .size(4.dp)
            .clip(CircleShape)
            .background(
                color = Color.LightGray
            )
        )

        Spacer(modifier = Modifier.width(10.dp))

        Text(
            text = date,
            style = MaterialTheme.typography.labelSmall.copy(
                textAlign = TextAlign.Start,
                fontSize = 12.sp,
                color = Color.LightGray
            ),
            modifier = modifier.weight(1f)
        )

    }
}

@Composable
fun PostLikesRow(
    modifier: Modifier = Modifier,
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
//        IconButton(onClick = onLikeClick) {
//            Icon(painter = painterResource(
//                id = R.drawable.like_icon_outlined),
//                contentDescription = null,
//                tint = Color.LightGray
//            )
//        }
//        Text(
//            text = "$likeCount",
//            style = MaterialTheme.typography.bodyMedium.copy(
//                fontSize = 18.sp
//            )
//        )
//        Spacer(modifier = modifier.width(8.dp))
//
//        IconButton(onClick = onCommentClick) {
//            Icon(painter = painterResource(
//                id = R.drawable.chat_icon_outlined),
//                contentDescription = null,
//                tint = Color.LightGray
//            )
//        }
//        Text(
//            text = "$commentCount",
//            style = MaterialTheme.typography.bodyMedium.copy(
//                fontSize = 18.sp
//            )
//        )
    }
}


@Preview
@Composable
private fun PostListItemPreview() {
    OnlySendsTheme {
        Surface(color = androidx.compose.material.MaterialTheme.colors.surface) {
            PostListItem(
                post = Post(),
            )
        }
    }
}

@Preview
@Composable
private fun PostHeaderPreview() {
    OnlySendsTheme {
        Surface(color = androidx.compose.material.MaterialTheme.colors.surface) {
            PostHeader(
                name = "Mr Dip",
                profileURL = "",
                date = "20 min",
            )
        }
    }
}


@Preview
@Composable
private fun PostLikesRowPreview() {
    OnlySendsTheme {
        Surface(color = androidx.compose.material.MaterialTheme.colors.surface) {
            PostLikesRow(
            )
        }
    }
}