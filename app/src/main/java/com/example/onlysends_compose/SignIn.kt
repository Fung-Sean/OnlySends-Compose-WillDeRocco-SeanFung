package com.example.onlysends_compose

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import com.example.onlysends_compose.ui.MainActivity
import com.google.android.gms.common.SignInButton
import com.google.firebase.auth.FirebaseAuth
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.firebase.Firebase
import com.google.firebase.auth.auth


class SignIn : ComponentActivity(){
    private val TAG = "Sign In"
    private lateinit var auth: FirebaseAuth
    private lateinit var loginButton: SignInButton
    private lateinit var googleSignInClient: GoogleSignInClient
    private lateinit var signInLauncher: ActivityResultLauncher<Intent>


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent{
            SignUpScreen()
        }
        auth = Firebase.auth
        if (auth.currentUser!= null){
            hostActivity()
        }
    }

    private fun hostActivity(){
        val i = Intent(this, MainActivity::class.java)
//        start

    }
}
