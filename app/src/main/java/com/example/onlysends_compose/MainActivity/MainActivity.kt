package com.example.onlysends_compose.MainActivity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.Scaffold
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import kotlinx.coroutines.launch
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.navArgument
import com.example.onlysends_compose.R
import com.example.onlysends_compose.ui.add_post.AddPostScreen
import com.example.onlysends_compose.components.navigation.CustomTopAppBar
import com.example.onlysends_compose.ui.friends.FriendsScreen
import com.example.onlysends_compose.ui.friends.FriendsViewModel
import com.example.onlysends_compose.ui.home.HomeScreen
import com.example.onlysends_compose.ui.home.HomeViewModel
import com.example.onlysends_compose.ui.home.theme.buttonColor
import com.example.onlysends_compose.ui.home.theme.signOutColor
import com.example.onlysends_compose.ui.maps.MapDisplay
import com.example.onlysends_compose.ui.maps.MapScreen
import com.example.onlysends_compose.ui.maps.defaultCameraPosition
import com.example.onlysends_compose.ui.maps.new_height.AddHeightScreen
import com.example.onlysends_compose.ui.maps.new_height.AddHeightViewModel
import com.example.onlysends_compose.ui.search.SearchScreen
import com.example.onlysends_compose.ui.search.SearchViewModel
import com.example.onlysends_compose.ui.sign_in.UserData
import com.google.android.gms.maps.MapsInitializer
import com.google.android.libraries.places.api.Places
import com.google.maps.android.compose.rememberCameraPositionState


private const val TAG = "MainActivity"
object Destinations {
    const val AddHeight = "AddHeight"
}
class MainActivity : AppCompatActivity() {
    private val googleAuthUiClient by lazy {
        GoogleAuthUiClient(
            context = applicationContext,
            oneTapClient = Identity.getSignInClient(applicationContext)
        )
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "MainActivity loaded")
        MapsInitializer.initialize(getApplicationContext());
        Places.initialize(applicationContext, "AIzaSyDJgK-hMQ1v8qgXXUg1NFgZgfV7nlZeFCo")
        setContent {
            // navigation bar elements
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()
            val navItems = listOf(
                getString(R.string.home),
                getString(R.string.search),
                getString(R.string.post),
                getString(R.string.maps),
                getString(R.string.friends)
            )
            // Define icons map dynamically
            val icons = navItems.associateWith { item ->
                when (item) {
                    getString(R.string.home) -> Icons.Filled.Home
                    getString(R.string.search) -> Icons.Filled.Search
                    getString(R.string.post) -> Icons.Filled.AddCircle
                    getString(R.string.maps) -> Icons.Filled.LocationOn
                    getString(R.string.friends) -> Icons.Filled.Face
                    else -> error("Unsupported navigation item: $item")
                }
            }

            // should not have any item selected on navBar at first
            var selectedItem by remember { mutableIntStateOf(-1) }

            // keep track of user state (can be passed into other composable functions)
            var user by remember { mutableStateOf<User?>(null) }

            // Define a state variable to hold the current route
            var currentRoute by remember { mutableStateOf("") }

            // Function to update current route
            @Composable
            fun updateCurrentRoute(navController: NavHostController) {
                LaunchedEffect(navController.currentBackStackEntry?.destination?.route) {
                    currentRoute = navController.currentBackStackEntry?.destination?.route ?: ""

                    // if currentRoute profile, selectedItem is -1
                    if (currentRoute == getString(R.string.profile)) {
                        selectedItem = -1
                    }
                }
            }

            // IMPORTANT: Function to update the user state variable (call this any time you update `user` within a composable)
            // NOTE: i'm thinking to veer away from this approach (not really necessary if using ViewModels)
            fun updateUser(newUser: User) {
                user = newUser
            }

            // Function to create user and call createUserDocument
            fun createUserAndDocument(userData: UserData?) {
                user = userData?.let {
                    User(
                        userId = it.userId,
                        username = it.username,
                        profilePictureUrl = it.profilePictureUrl,
                    )
                }
                // call createUserDocument function (this will UPDATE the `user` state variable automatically)
                // with the updateUser callback function
                user?.let { Firestore.handleCreateUserDocument(it, ::updateUser) }
            }

            // Function to check if navigation topBar/bottomBar should be displayed
            fun showBar(): Boolean {
                return !(currentRoute.isEmpty() || currentRoute == getString(R.string.sign_in))
            }

            Scaffold(
                scaffoldState = scaffoldState,
                topBar = {
                    // If route is not yet defined (aka on sign_in page) -> don't show navigation bar
                    if (showBar()) {
                        CustomTopAppBar(
                            user = user,
                            navController = navController,
                            context = applicationContext
                        )
                    }
                },
                bottomBar = {
                    Log.d(TAG, "route ${currentRoute.isEmpty()}")
                    // If route is not yet defined (aka on sign_in page) -> don't show navigation bar
                    if (showBar()) {
                        NavigationBar(
                            modifier = Modifier
                                .height(60.dp)
                        ) {
                            NavigationBar(
                                modifier = Modifier.fillMaxSize()
                            ) {
                                navItems.forEachIndexed { index, item ->
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
                    // ----------------------- route 0) "sign_in" -----------------------
                    composable(getString(R.string.sign_in)) {
                        val viewModel = viewModel<SignInViewModel>()
                        val state = viewModel.state.collectAsStateWithLifecycle().value

                        // Skip straight to "profile" page (or whatever we end up choosing) if
                        // already signed in.
                        LaunchedEffect(key1 = Unit) { // since key1 = Unit -> function only runs once (when composable is composed)
                            // obtain user from database
                            val userData = googleAuthUiClient.getSignedInUser()
                            if (userData != null) {
                                // perform appropriate db operation (create user if not in db)
                                createUserAndDocument(userData)

                                // navigate to profile page on successful login
                                navController.navigate(getString(R.string.home))
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

                                // obtain user from database
                                val userData = googleAuthUiClient.getSignedInUser()
                                if (userData != null) {
                                    // perform appropriate db operation (create user if not in db)
                                    createUserAndDocument(userData)

                                    // navigate to profile page on successful login
                                    navController.navigate(getString(R.string.profile))

                                    // make sure state is reset (in case user needs to log back in)
                                    viewModel.resetState()
                                }
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

                    // add more routes other composable functions
                    // ----------------------- route 1) "home" -----------------------
                    composable(route = getString(R.string.home)) {
                        Log.d(TAG, "entering home page, user is: $user")

                        // Update the currentRoute when navigating to "home" (or any other page)
                        updateCurrentRoute(navController = navController)

                        // initialize viewModel
                        val viewModel: HomeViewModel = remember {
                            HomeViewModel(application, user!!)
                        }

                        // render composable (pass-in fetchData from viewModel)
                        HomeScreen(
                            user = user!!,
                            postsUiState = viewModel.postsUiState.value,
                            fetchMoreData = viewModel::fetchData
                        )
                    }

                    // ----------------------- route 2) "search" -----------------------
                    composable(route = getString(R.string.search)) {
                        // Update the currentRoute when navigating to "search" (or any other page)
                        updateCurrentRoute(navController = navController)

                        // initialize viewModel
                        val viewModel: SearchViewModel = remember {
                            SearchViewModel(application, user!!)
                        }

                        // render composable (pass-in fetchData from viewModel)
                        SearchScreen(
//                            modifier = Modifier.padding(innerPadding),
                            searchUiState = viewModel.searchUiState.value,
                            fetchMoreData = viewModel::fetchData,
                            onFollowFriend = viewModel::followFriend,
                            onAcceptFriend = viewModel::acceptFriend
                        )
                    }

                    // ----------------------- route 3) "post" -----------------------
                    composable(route = getString(R.string.post)) {
                        // Update the currentRoute when navigating to "post" (or any other page)
                        updateCurrentRoute(navController = navController)
                        AddPostScreen(
                            context = applicationContext,
                            user = user!!,
                            navController = navController,
                        )
                    }

                    // ----------------------- route 4.1) "maps" -----------------------
                    composable(route = getString(R.string.maps)) {
                        // Update the currentRoute when navigating to "maps" (or any other page)
                        updateCurrentRoute(navController = navController)

                        MapScreen(
                            navController,
                            context = applicationContext,
                            activity = this@MainActivity,
                        )
                    }

                    // ----------------------- route 4.2) "AddHeight/siteLocation" -----------------------
                    composable(
                        route = "${Destinations.AddHeight}/{siteLocation}",
                        arguments = listOf(navArgument("siteLocation") { defaultValue = "" })
                    ) { backStackEntry ->
                        val siteLocation = remember {
                            mutableStateOf(backStackEntry.arguments?.getString("siteLocation") ?: "")
                        }

                        // initialize viewModel
                        val viewModel: AddHeightViewModel = remember {
                            AddHeightViewModel(
                                application = application,
                                user = user!!,
                                siteLocation = siteLocation,
                                onSuccess = {}
                            )
                        }

                        // Call AddHeightScreen and pass the necessary parameters
                        AddHeightScreen(
                            addHeightUiState = viewModel.addHeightUiState.value,
                            onAddHeight = viewModel::addHeight
                        )
                    }

                    // ----------------------- route 5) "friends" -----------------------
                    composable(route = getString(R.string.friends)) {
                        // Update the currentRoute when navigating to "friends" (or any other page)
                        updateCurrentRoute(navController = navController)

                        // initialize viewModel
                        val viewModel: FriendsViewModel = remember {
                            FriendsViewModel(application, user!!)
                        }

                        FriendsScreen(
                            friendsUiState = viewModel.friendsUiState.value,
                            fetchMoreData = viewModel::fetchData,
                            onRemoveFriend = viewModel::removeFriend
                        )

                    }

                    // ----------------------- route 6) "profile" -----------------------
                    composable(route = getString(R.string.profile)) {
                        Log.d(TAG, "entering profile page, user is: $user")

                        // Update the currentRoute when navigating to "profile" (or any other page)
                        updateCurrentRoute(navController = navController)

                        // render ProfileScreen composable
                        ProfileScreen(
                            user = user!!,
                            onSignOut = {
                                lifecycleScope.launch {
                                    googleAuthUiClient.signOut()
                                    Toast.makeText(
                                        applicationContext,
                                        "Signed out",
                                        Toast.LENGTH_LONG
                                    ).show()

                                    // navigate back to sign_in page on `signOut`
                                    navController.navigate(getString(R.string.sign_in))
                                }
                            },
                            onUpdateUser = ::updateUser
                        )
                    }

                }
            }
        }
    }

}