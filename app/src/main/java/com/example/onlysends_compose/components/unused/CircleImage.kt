package com.example.onlysends_compose.components.unused

import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.R
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import coil.compose.AsyncImage

var lightImage = R.string.not_selected
@Composable

fun CircleImage(
    modifier: Modifier = Modifier,
    imageUrl: String?,
    onClick: () -> Unit
){
    AsyncImage(
        model = imageUrl, 
        contentDescription = null, 
        modifier = modifier
            .clip(CircleShape)
            .clickable { onClick() },
        placeholder = if(MaterialTheme.colors.isLight){
            painterResource(id = com.example.onlysends_compose.R.drawable.light_image_place_holder)
        }else{
            painterResource(id = com.example.onlysends_compose.R.drawable.dark_image_place_holder)
        },
        contentScale = ContentScale.Crop
    )
}