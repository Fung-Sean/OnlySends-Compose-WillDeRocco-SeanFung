package com.example.onlysends_compose.components.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PageHeaderText(
    text: String
) {
    Column(
        modifier = Modifier
            .padding(bottom = 30.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.displaySmall
        )
    }
}