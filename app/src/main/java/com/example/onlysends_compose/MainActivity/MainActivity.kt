package com.example.onlysends_compose.MainActivity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.material.Scaffold
import androidx.compose.material.TopAppBar
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.runtime.LaunchedEffect
import androidx.core.view.WindowCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.onlysends_compose.firestore.Firestore
import com.example.onlysends_compose.firestore.types.User
import com.example.onlysends_compose.ui.profile.ProfileScreen
import com.example.onlysends_compose.ui.sign_in.GoogleAuthUiClient
import com.example.onlysends_compose.ui.sign_in.SignInScreen
import com.example.onlysends_compose.ui.sign_in.SignInViewModel
import com.google.android.gms.auth.api.identity.Identity
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.firestore
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController


private const val TAG = "MainActivity"

class MainActivity : AppCompatActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }
    private val firestore: FirebaseFirestore by lazy {
        Firebase.firestore
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity loaded")

        setContent {
            // navigation bar elements
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()
            val items = listOf("Sign In", "Profile")
            var selectedItem by remember { mutableIntStateOf(0) }

            // Define a state variable to hold the current route
            var currentRoute by remember { mutableStateOf("") }

            Scaffold(
                scaffoldState = scaffoldState,
                topBar = {
                    // TO-DO: style this to have our logo, app-name, and username
                    TopAppBar(
                        title = {
                            Text("Your App Title")
                        },
                        modifier = Modifier
                            .height(60.dp)
                    )
                },
                bottomBar = {
                    Log.d(TAG, "route ${currentRoute.isEmpty()}")
                    // this is not working yet (ideally don't wanna display navbar until signed in)
                    if (!(currentRoute.isEmpty() || currentRoute == "sign_in")) {
                        NavigationBar(
                            modifier = Modifier
                                .height(60.dp)
                        ) {
                            NavigationBar(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                items.forEachIndexed { index, item ->
                                    NavigationBarItem(
                                        icon = {
                                            when (index) {
                                                0 -> Icon(Icons.Filled.Home, contentDescription = "Sign In")
                                                1 -> Icon(Icons.Filled.Person, contentDescription = "Profile")
                                                // Add more icons as needed
                                            }
                                        },
                                        selected = selectedItem == index,
                                        onClick = {
                                            selectedItem = index
                                            when (index) {
                                                0 -> navController.navigate("sign_in")
                                                1 -> navController.navigate("profile")
                                                // Add more navigation destinations as needed
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            ) { innerPadding ->

                NavHost(
                    navController = navController,
                    startDestination = "sign_in",
                ) {
                    // endpoint 1) "sign_in"
                    composable("sign_in") {
                        val viewModel = viewModel<SignInViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle().value

                        // Skip straight to "profile" page (or whatever we end up choosing) if
                        // already signed in.
                        LaunchedEffect(key1 = Unit) {
                            if (googleAuthUiClient.getSignedInUser() != null) {
                                navController.navigate("profile")
                            }
                        }

                        val launcher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.StartIntentSenderForResult(),
                            onResult = { result ->
                                if (result.resultCode == RESULT_OK) {
                                    lifecycleScope.launch {
                                        val signInResult = googleAuthUiClient.signInWithIntent(
                                            intent = result.data ?: return@launch
                                        )
                                        // pass the result to the viewModel so that the state gets properly updated
                                        viewModel.onSignInResult(signInResult)
                                    }
                                }
                            }
                        )

                        LaunchedEffect(key1 = state.isSignInSuccessful) {
                            if (state.isSignInSuccessful) {
                                Toast.makeText(
                                    applicationContext,
                                    "Sign in successful",
                                    Toast.LENGTH_LONG
                                ).show()

                                navController.navigate("profile")
                                Log.d(TAG, "current backstack entry is ${navController.currentBackStackEntry?.destination?.route}")
                                // make sure state is reset (in case user needs to log back in)
                                viewModel.resetState()
                            }
                        }

                        SignInScreen(
                            state = state,
                            onSignInClick = {
                                Log.d(TAG, "starting signIn process")
                                lifecycleScope.launch {
                                    val signInIntentSender = googleAuthUiClient.signIn()
                                    launcher.launch(
                                        IntentSenderRequest.Builder(
                                            signInIntentSender ?: return@launch
                                        ).build()
                                    )
                                }
                            }
                        )
                    }

                    // endpoint 2) "profile"
                    composable(route = "profile") {
                        // Update the currentRoute when navigating to "profile" (or any other page)
                        LaunchedEffect(navController.currentBackStackEntry?.destination?.route) {
                            currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
                        }

                        // perform appropriate db operation (create user if not in db)
                        val userData = googleAuthUiClient.getSignedInUser()

                        // Convert UserData to User
                        val user = userData?.let {
                            User(
                                userId = it.userId,
                                username = it.username,
                                profilePictureUrl = it.profilePictureUrl,
                                // Add other attributes as needed
                            )
                        }

                        // Call createUserDocument here
                        user?.let { Firestore.createUserDocument(firestore, it) }

                        ProfileScreen(
                            userData = userData,
                            onSignOut = {
                                lifecycleScope.launch {
                                    googleAuthUiClient.signOut()
                                    Toast.makeText(
                                        applicationContext,
                                        "Signed out",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    navController.navigate("sign_in")
                                }
                            }
                        )
                    }

                    // add more endpoints other composable functions

                }
            }
        }
    }


    private fun updateCurrentRoute(navController: NavHostController) {

    }

}