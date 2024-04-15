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
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
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
import com.example.onlysends_compose.R
import com.example.onlysends_compose.ui.sign_in.UserData


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
            val items = listOf("home", "search", "post", "maps", "profile")
            // Define icons map
            val icons = mapOf(
                "home" to Icons.Filled.Home,
                "search" to Icons.Filled.Search,
                "post" to Icons.Filled.AddCircle,
                "maps" to Icons.Filled.LocationOn,
                "profile" to Icons.Filled.Person
            )

            var selectedItem by remember { mutableIntStateOf(0) }

            // if logged in, selectedItem = profile
            if (googleAuthUiClient.getSignedInUser() != null) {
                selectedItem = 4
            }

            // keep track of user state (can be passed into other composable functions)
            var user by remember { mutableStateOf<User?>(null) }

            // Define a state variable to hold the current route
            var currentRoute by remember { mutableStateOf("") }

            // Function to update current route
            @Composable
            fun updateCurrentRoute(navController: NavHostController) {
                LaunchedEffect(navController.currentBackStackEntry?.destination?.route) {
                    currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""
                }
            }

            // Function to create user and call createUserDocument
            fun createUserAndDocument(userData: UserData?) {
                user = userData?.let {
                    User(
                        userId = it.userId,
                        username = it.username,
                        profilePictureUrl = it.profilePictureUrl,
                        // Add other attributes as needed
                    )
                }
                user?.let { Firestore.createUserDocument(firestore, it) }
            }

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
                    // If route is not yet defined (aka on sign_in page) -> don't show navigation bar
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
                                            icons[item]?.let { Icon(it, contentDescription = item) }
                                        },
                                        selected = selectedItem == index,
                                        onClick = {
                                            selectedItem = index
                                            Log.d(TAG, "route is: $item")
                                            navController.navigate(item)
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
                    startDestination = getString(R.string.sign_in),
                ) {
                    // ----------------------- endpoint 0) "sign_in" -----------------------
                    composable(getString(R.string.sign_in)) {
                        val viewModel = viewModel<SignInViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle().value

                        // Skip straight to "profile" page (or whatever we end up choosing) if
                        // already signed in.
                        LaunchedEffect(key1 = Unit) {
                            if (googleAuthUiClient.getSignedInUser() != null) {
                                navController.navigate(getString(R.string.profile))
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


                    // add more endpoints other composable functions
                    // ----------------------- endpoint 1) "home" -----------------------
                    composable(route = getString(R.string.home)) {
                        // Update the currentRoute when navigating to "profile" (or any other page)
                        updateCurrentRoute(navController = navController)

                    }

                    // ----------------------- endpoint 2) "search" -----------------------
                    composable(route = getString(R.string.search)) {
                        // Update the currentRoute when navigating to "profile" (or any other page)
                        updateCurrentRoute(navController = navController)

                    }

                    // ----------------------- endpoint 3) "post" -----------------------
                    composable(route = getString(R.string.post)) {
                        // Update the currentRoute when navigating to "profile" (or any other page)
                        updateCurrentRoute(navController = navController)

                    }

                    // ----------------------- endpoint 4) "maps" -----------------------
                    composable(route = getString(R.string.maps)) {
                        // Update the currentRoute when navigating to "profile" (or any other page)
                        updateCurrentRoute(navController = navController)

                    }

                    // ----------------------- endpoint 4) "friends" -----------------------
                    composable(route = getString(R.string.friends)) {
                        // Update the currentRoute when navigating to "profile" (or any other page)
                        updateCurrentRoute(navController = navController)

                    }

                    // ----------------------- endpoint 6) "profile" -----------------------
                    composable(route = getString(R.string.profile)) {
                        // Update the currentRoute when navigating to "profile" (or any other page)
                        updateCurrentRoute(navController = navController)

                        // perform appropriate db operation (create user if not in db)
                        val userData = googleAuthUiClient.getSignedInUser()

                        // call homemade Firestore method to log the user (and update the db)
                        createUserAndDocument(userData)

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

                                    navController.navigate(getString(R.string.sign_in))
                                }
                            }
                        )
                    }

                }
            }
        }
    }

}