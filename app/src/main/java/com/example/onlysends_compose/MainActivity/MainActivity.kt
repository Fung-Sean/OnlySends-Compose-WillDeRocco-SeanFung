package com.example.onlysends_compose.MainActivity

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material.Scaffold
import androidx.compose.material.TextField
import androidx.compose.material.rememberScaffoldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Face
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.onlysends_compose.R
import com.example.onlysends_compose.ui.add_post.AddPostScreen
import com.example.onlysends_compose.ui.components.CustomTopAppBar
import com.example.onlysends_compose.ui.friends.FriendsScreen
import com.example.onlysends_compose.ui.home.HomeScreen
import com.example.onlysends_compose.ui.home.HomeScreenViewModel
import com.example.onlysends_compose.ui.home.theme.buttonColor
import com.example.onlysends_compose.ui.home.theme.signOutColor
import com.example.onlysends_compose.ui.maps.MapScreen
import com.example.onlysends_compose.ui.maps.defaultCameraPosition
import com.example.onlysends_compose.ui.maps.new_height.AddHeightScreen
import com.example.onlysends_compose.ui.search.SearchScreen
import com.example.onlysends_compose.ui.sign_in.UserData
import com.google.maps.android.compose.rememberCameraPositionState


private const val TAG = "MainActivity"

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

        setContent {
            // navigation bar elements
            val navController = rememberNavController()
            val scaffoldState = rememberScaffoldState()
            val items = listOf("home", "search", "post", "maps", "friends")
            // Define icons map
            val icons = mapOf(
                "home" to Icons.Filled.Home,
                "search" to Icons.Filled.Search,
                "post" to Icons.Filled.AddCircle,
                "maps" to Icons.Filled.LocationOn,
                "friends" to Icons.Filled.Face
            )

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
                }
            }

            // IMPORTANT: Function to update the user state variable (call this any time you update `user` within a composable)
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
                        // Add other attributes as needed
                    )
                }
                // call createUserDocument function (this will UPDATE the `user` state variable automatically)
                // with the updateUser callback function
                user?.let { Firestore.handleCreateUserDocument(it, ::updateUser) }
            }

            Scaffold(
                scaffoldState = scaffoldState,
                topBar = {
                    // If route is not yet defined (aka on sign_in page) -> don't show navigation bar
                    if (!(currentRoute.isEmpty() || currentRoute == "sign_in")) {
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

                    composable(route = "AddHeight") {
                        val siteLocationText = remember {
                            mutableStateOf("")
                        }
                        val notesText = remember {
                            mutableStateOf("")
                        }
                        AddHeightScreen(
                            siteLocationText = siteLocationText,
                            notesText = notesText,
                            onLocationAdded = { /* Handle navigation or other actions */ }
                        )
                    }
                    // add more routes other composable functions
                    // ----------------------- route 1) "home" -----------------------
                    composable(route = getString(R.string.home)) {
                        // Update the currentRoute when navigating to "home" (or any other page)

                        updateCurrentRoute(navController = navController)
                        val viewModel = HomeScreenViewModel(application, user!!)

                        HomeScreen(
                            user = user!!,
                            application = application,
                        )
                    }

                    // ----------------------- route 2) "search" -----------------------
                    composable(route = getString(R.string.search)) {
                        // Update the currentRoute when navigating to "search" (or any other page)
                        updateCurrentRoute(navController = navController)

                        // render SearchScreen composable
                        SearchScreen(
                            user = user!!,
                            onUpdateUser = ::updateUser
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

                    // ----------------------- route 4) "maps" -----------------------
                    composable(route = getString(R.string.maps)) {
                        // Update the currentRoute when navigating to "maps" (or any other page)
                        updateCurrentRoute(navController = navController)
                        val cameraPositionState = rememberCameraPositionState{
                            position = defaultCameraPosition
                        }
                        var isMapLoaded by remember {
                            mutableStateOf(false)
                        }

                        var searchText by remember {
                            mutableStateOf("Search a place")
                        }
                        Column(
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Row (
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ){
                                Text(
                                    text = "New Heights",
                                    style = MaterialTheme.typography.displayMedium,
                                    modifier = Modifier
                                        .padding(16.dp)

                                )
                                Spacer(modifier = Modifier.weight(1f)) // Add a spacer to occupy the available space
                                Button(
                                    onClick = { navController.navigate("AddHeight") },
                                    modifier = Modifier
                                        .align(Alignment.CenterVertically)
                                        .padding(2.dp),
                                    shape = CircleShape,
                                    colors = ButtonDefaults.buttonColors(signOutColor)
                                ) {
                                    Icon(
                                        painter = painterResource(id = android.R.drawable.ic_input_add),
                                        contentDescription = null,
                                    )
                                }
                            }

                            MapScreen(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(550.dp),
                                cameraPositionState = cameraPositionState,
                                onMapLoaded = {
                                    isMapLoaded = true
                                }
                            )
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp), // Adjust padding as needed
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                // Icon
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = "Search",
                                    modifier = Modifier.size(24.dp)
                                )

                                // TextField for search input
                                OutlinedTextField(
                                    value = searchText,
                                    onValueChange = { searchText = it },
                                    modifier = Modifier
                                        .weight(1f)
                                        .padding(start = 8.dp)
                                )

                                // Button for search action
                                Button(
                                    onClick = { /* Handle search action */ },
                                    modifier = Modifier.padding(start = 8.dp),
                                    colors = ButtonDefaults.buttonColors(buttonColor)
                                ) {
                                    Text(text = "Search")
                                }
                            }
                        }



                    }

                    // ----------------------- route 5) "friends" -----------------------
                    composable(route = getString(R.string.friends)) {
                        // Update the currentRoute when navigating to "friends" (or any other page)
                        updateCurrentRoute(navController = navController)

                        FriendsScreen(
                            user = user!!,
                            onUpdateUser = ::updateUser
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