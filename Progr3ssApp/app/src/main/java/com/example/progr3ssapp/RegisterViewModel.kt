package com.example.progr3ssapp

import android.app.Application
import android.util.Patterns
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.progr3ssapp.network.AuthRepository
import com.example.progr3ssapp.network.models.LoginResponse
import com.example.progr3ssapp.utils.SessionManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class RegisterUiState(
    val username: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegistered: Boolean = false
) {
    // Red borders appear once the user starts typing in "Confirm password"
    val passwordsMismatch: Boolean
        get() = confirmPassword.isNotEmpty() && password != confirmPassword
}

class RegisterViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AuthRepository()
    private val sessionManager = SessionManager.getInstance(application)

    private val _uiState = MutableStateFlow(RegisterUiState())
    val uiState: StateFlow<RegisterUiState> = _uiState.asStateFlow()

    fun onUsernameChange(value: String) {
        _uiState.update { it.copy(username = value, error = null) }
    }

    fun onEmailChange(value: String) {
        _uiState.update { it.copy(email = value, error = null) }
    }

    fun onPasswordChange(value: String) {
        _uiState.update { it.copy(password = value, error = null) }
    }

    fun onConfirmPasswordChange(value: String) {
        _uiState.update { it.copy(confirmPassword = value, error = null) }
    }

    fun register() {
        val state = _uiState.value
        val username = state.username.trim()
        val email = state.email.trim()

        validate(username, email, state.password, state.confirmPassword)?.let { message ->
            _uiState.update { it.copy(error = message) }
            return
        }

        if (state.isLoading) return
        _uiState.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            repository.register(username = username, email = email, password = state.password)
                .onSuccess { response ->
                    // Signup returns tokens too, so the user is logged in right away
                    persistSession(response)
                    _uiState.update { it.copy(isLoading = false, isRegistered = true) }
                }
                .onFailure { throwable ->
                    val message = throwable.message.orEmpty()
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = when {
                                // The backend sends a raw database error when the email is taken
                                "duplicate key" in message -> "This email is already registered"
                                message.isNotBlank() -> message
                                else -> "Registration failed. Please try again."
                            }
                        )
                    }
                }
        }
    }

    private fun persistSession(response: LoginResponse) {
        sessionManager.saveSession(
            accessToken = response.accessToken,
            refreshToken = response.refreshToken,
            userId = response.user?.id,
            email = response.user?.email,
            username = response.user?.profile?.username
        )
    }

    private fun validate(
        username: String,
        email: String,
        password: String,
        confirmPassword: String
    ): String? = when {
        username.isEmpty() -> "Username is required"
        email.isEmpty() -> "Email is required"
        !Patterns.EMAIL_ADDRESS.matcher(email).matches() -> "Enter a valid email address"
        password.length < 6 -> "Password must be at least 6 characters"
        password != confirmPassword -> "Passwords do not match"
        else -> null
    }
}