package com.example.onlysends_compose.ui.sign_in

import android.widget.Space
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.onlysends_compose.R
import com.example.onlysends_compose.ui.home.theme.OnlySendsTheme
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
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(brush = gradientBrush),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        
        Text(
            text = "OnlySends",
            style = MaterialTheme.typography.headlineLarge,
            fontFamily = FontFamily(Font(R.font.lexend_medium))
        )
        Image(
            painter = painterResource(id = R.drawable.temp_logo),
            contentDescription = "Logo for app",
            modifier = modifier
                .size(300.dp) // Adjust the size as needed
        )
        
        Spacer(modifier = Modifier.height(100.dp))
        
        // Your sign-in button
        Button(
            onClick = onSignInClick,
            colors = ButtonDefaults.buttonColors(signOutColor),
        ) {
            Text(text = "Sign in with Google")
        }
    }
}

@Preview
@Composable
fun SignInScreenPreview() {
    OnlySendsTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            SignInScreen(
                state = SignInState(),
                onSignInClick = { }
            )
        }
    }
}