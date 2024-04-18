package com.example.onlysends_compose.ui.sign_in

import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.fontResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import com.example.onlysends_compose.R
import com.example.onlysends_compose.ui.home.theme.buttonColor
import com.example.onlysends_compose.ui.home.theme.signOutColor

// SignInScreen : composable to render screen for user to login with Google (calls onSignInClick
// via lambda to update the state
@Composable
fun SignInScreen(
    state: SignInState,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val gradientBrush = Brush.verticalGradient(
        colors = listOf(
            colorResource(id = R.color.onlySendsBlue),
            colorResource(id = R.color.white)
        )
    )
    Box(
        modifier = Modifier.fillMaxSize().background(brush = gradientBrush),
        contentAlignment = Alignment.Center
    ) {
        
        Text(text = "OnlySends", modifier = modifier
            .padding(top = 40.dp)
            .align(Alignment.TopCenter),
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = FontFamily(Font(R.font.lexend_medium))
        )
        Spacer(modifier = modifier.padding(40.dp))
        Image(
            painter = painterResource(id = R.drawable.temp_logo),
            contentDescription = "Logo for app",
            modifier = modifier
                .padding(top = 100.dp)
                .align(Alignment.TopCenter)
                .size(300.dp) // Adjust the size as needed
        )
        Box(
            modifier = Modifier.fillMaxSize(), // Fill the entire screen
            contentAlignment = Alignment.Center,
        ) {

            // Your sign-in button
            Button(
                onClick = onSignInClick,
                colors = ButtonDefaults.buttonColors(signOutColor),
                modifier = modifier.padding(top=200.dp)
            ) {
                Text(text = "Sign in with Google")
            }
        }
    }
}