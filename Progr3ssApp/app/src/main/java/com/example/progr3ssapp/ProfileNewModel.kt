package com.example.progr3ssapp

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.progr3ssapp.network.AuthRepository
import com.example.progr3ssapp.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ProfileUiState(
    val userId: Long = -1,
    val username: String? = null,
    val email: String? = null,
    val isLoggingOut: Boolean = false,
    val isLoggedOut: Boolean = false
)

class ProfileViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    private val sessionManager = SessionManager.getInstance(application)

    private val _uiState = MutableStateFlow(loadSession())
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Re-read the saved user each time the screen opens
    fun refresh() {
        _uiState.update {
            it.copy(
                userId = sessionManager.userId,
                username = sessionManager.username,
                email = sessionManager.email
            )
        }
    }

    fun logout() {
        if (_uiState.value.isLoggingOut) return
        _uiState.update { it.copy(isLoggingOut = true) }

        viewModelScope.launch {
            repository.logout(sessionManager.accessToken) // tell the backend
            sessionManager.clear()                        // forget the tokens on the phone
            _uiState.update { it.copy(isLoggingOut = false, isLoggedOut = true) }
        }
    }

    private fun loadSession() = ProfileUiState(
        userId = sessionManager.userId,
        username = sessionManager.username,
        email = sessionManager.email
    )
}