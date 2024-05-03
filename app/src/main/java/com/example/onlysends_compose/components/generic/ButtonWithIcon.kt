package com.example.onlysends_compose.components.generic

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun ButtonWithIcon(
    modifier: Modifier = Modifier,
    text: String,
    colors: ButtonColors = ButtonDefaults.buttonColors(
        containerColor = colorResource(id = com.example.onlysends_compose.R.color.onlySends),
        contentColor = colorResource(id = com.example.onlysends_compose.R.color.white)
    ),
    icon: ImageVector,
    onClick: () -> Unit
) {
    Button(
        colors = colors,
        onClick = { onClick() },
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = modifier
                .fillMaxWidth()
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontSize = 18.sp,
                ),
                modifier = modifier.weight(1f)
            )
            Spacer(modifier = modifier.padding(4.dp))
            Icon(
                imageVector = icon,
                contentDescription = "Icon button ($text)",
                modifier = modifier.size(24.dp)
            )
        }
    }
}