package com.example.onlysends_compose.components.generic

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.onlysends_compose.R

// NOTE: this custom search bar was inspired by https://stackoverflow.com/questions/64542659/jetpack-compose-custom-textfield-design
@Composable
fun CustomSearchBar(
    searchQuery: String,
    placeHolder: String,
    maxLength: Int,
    onUpdateSearch: (String) -> Unit
) {
    Column {
        val gray = colorResource(id = R.color.searchBarGray)
        val blue = colorResource(id = R.color.onlySends)

        TextField(
            modifier = Modifier.fillMaxWidth(),
            value = searchQuery,
            placeholder = { Text(text = placeHolder) },
            colors = TextFieldDefaults.textFieldColors(
                backgroundColor = gray,
                cursorColor = Color.Black,
                disabledLabelColor = gray,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent
            ),
            onValueChange = {
                if (it.length <= maxLength) onUpdateSearch(it)
            },
            shape = RoundedCornerShape(8.dp),
            singleLine = true,
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onUpdateSearch("") }) {
                        Icon(
                            imageVector = Icons.Outlined.Close,
                            contentDescription = null
                        )
                    }
                }
            }
        )
        Text(
            text = "${searchQuery.length} / $maxLength",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            textAlign = TextAlign.End,
            color = blue
        )
    }
}