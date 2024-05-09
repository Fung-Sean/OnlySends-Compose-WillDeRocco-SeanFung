package com.example.onlysends_compose.ui.maps

import android.content.Context
import android.content.pm.PackageManager // Import PackageManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.onlysends_compose.ui.maps.LocationViewModel
import android.Manifest

//This function handles whether or not the app has access to user location
@Composable
fun LocationPermissionHandler(
    viewModel: LocationViewModel = viewModel(),
    context: Context
) {
    val permissionGranted = (
            ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
            )

    LaunchedEffect(Unit) {
        if (!permissionGranted) {
            viewModel.getCurrentLocation()
        }
    }
}