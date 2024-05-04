package com.example.onlysends_compose.ui.maps.new_height

import android.R
import android.util.Log
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.onlysends_compose.ui.home.theme.RoundedCornerShape
import com.example.onlysends_compose.ui.home.theme.buttonColor

private const val TAG = "AddHeightScreen"

@Composable
fun AddHeightScreen(
    modifier: Modifier = Modifier,
    addHeightUiState: AddHeightUiState,
    onAddHeight: () -> Unit //Need to add a location data class and implement that here
){

    Log.d(TAG, "addHeightUiState is $addHeightUiState")

    var siteNameText = addHeightUiState.siteName
    var notesText = addHeightUiState.notes

    Column (
        modifier = modifier
            .wrapContentSize(Alignment.Center)
            .fillMaxSize()
    ){
        Text(text = "Add New Height",
            style = MaterialTheme.typography.displaySmall,
            modifier = modifier
                .align(Alignment.CenterHorizontally)
                .padding(40.dp)
        )
        Text(
            text = "Site Location:                                             ",
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier
                .padding(2.dp)
                .align(Alignment.CenterHorizontally)
            )
        OutlinedTextField(
            value = siteNameText.value,
            onValueChange = { siteNameText.value = it },
            label = { Text("Add a Location") },
            modifier = Modifier
                .width(300.dp)
                .align(Alignment.CenterHorizontally)
        )
        Spacer(modifier = modifier.padding(20.dp))
        Text(
            text = "Notes:                                                          ",
            style = MaterialTheme.typography.bodyLarge,
            modifier = modifier
                .padding(2.dp)
                .align(Alignment.CenterHorizontally)

        )
        OutlinedTextField(
            value = notesText.value,
            onValueChange = { newValue -> notesText.value = newValue },
            label = { Text("Add a Note") },
            modifier = Modifier.width(300.dp)
                .align(Alignment.CenterHorizontally)
                .height(150.dp)
        )
        Spacer(modifier = modifier.padding(100.dp))
        Button(
            onClick = onAddHeight,
            modifier = modifier
                .padding(10.dp)
                .width(300.dp)
                .align(Alignment.CenterHorizontally),
            shape = RoundedCornerShape(),
            colors = ButtonDefaults.buttonColors(buttonColor)
        ) {
            Row {
                Icon(
                    painter = painterResource(id = R.drawable.ic_input_add),
                    contentDescription = null
                )
                Text(
                    "Place Marker",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}
//@Preview(
//    backgroundColor = 0xFFFFFFFF,
//)
//@Composable
//fun AddHeightScreenPreview() {
//    // Sample mutable state values for site location text and notes text
//    val siteNameText = remember {
//        mutableStateOf("")
//    }
//    val notesText = remember {
//        mutableStateOf("")
//    }
//
//
//    AddHeightScreen(
//        siteNameText = siteNameText,
//        notesText = notesText,
//        onLocationAdded = {}
//    )
//}
