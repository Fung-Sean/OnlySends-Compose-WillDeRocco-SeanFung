package com.example.onlysends_compose.ui.sign_in

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

class SignInViewModel: ViewModel() {
    private val _state = MutableStateFlow(SignInState())
    val state = _state.asStateFlow()

    fun onSignInResult(result: SignInResult) {
        _state.update { it.copy(
            isSignInSuccessful = result.data != null,
            isLoading = false,
            signInError = result.errorMessage
        ) }
    }

    // ensures state is now viewed as successful after moving to diff page
    fun resetState() {
        _state.update { SignInState() }
    }

    fun startLoading() {
        _state.update {
            it.copy(isLoading = true)
        }
    }
}