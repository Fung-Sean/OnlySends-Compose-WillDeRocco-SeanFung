package com.example.onlysends_compose.ui.components

import androidx.annotation.StringRes
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.onlysends_compose.ui.home.theme.buttonColor

@Composable
fun FollowsButton(
    modifier: Modifier = Modifier,
    @StringRes text: Int,
    onClick: () -> Unit,
    isOutline: Boolean = false
){
    Button(
        onClick = onClick,
        modifier = modifier,
        colors = if(isOutline){
            ButtonDefaults.buttonColors(buttonColor)
        }else{
            ButtonDefaults.buttonColors(buttonColor)
        },
        border = if(isOutline){
            ButtonDefaults.outlinedBorder
        }else{
            null
        },
        elevation = ButtonDefaults.elevation(
            defaultElevation = 0.dp
        )
    ) {

        Text(
            text = stringResource(id = text),
            style = MaterialTheme.typography.labelSmall.copy(
                fontSize = 12.sp,
                color = Color.White
            ),
            textAlign = TextAlign.Center // Center the text horizontally and vertically
        )
    }
}