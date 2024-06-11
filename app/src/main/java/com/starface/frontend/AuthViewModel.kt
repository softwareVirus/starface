package com.starface.frontend

import android.text.TextUtils
import android.util.Patterns
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.starface.frontend.models.UserLoginRequest
import com.starface.frontend.models.UserResponse
import com.starface.frontend.models.UserSignupRequest
import com.starface.frontend.repository.UserRepository
import com.starface.frontend.utils.NetworkResult
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject
@HiltViewModel
class AuthViewModel @Inject constructor(private val userRepository: UserRepository): ViewModel() {
    val userReponseLiveData: LiveData<NetworkResult<UserResponse>>
        get() = userRepository.userResponseLiveData
    fun registerUser(userRequest: UserSignupRequest) {
        viewModelScope.launch {
            userRepository.registerUser(userRequest)
        }
    }
    fun loginUser(userRequest: UserLoginRequest) {
        viewModelScope.launch {
            userRepository.loginUser(userRequest)
        }
    }

    fun validateCredentials(
        firstname: String,
        lastname: String,
        email: String,
        password: String,
        confirmPassword: String,
        gender: String
    ): Pair<Boolean, String> {
        var result = Pair(true, "")

        if (TextUtils.isEmpty(firstname)
            || TextUtils.isEmpty(lastname)
            || TextUtils.isEmpty(email)
            || TextUtils.isEmpty(password)
            || TextUtils.isEmpty(confirmPassword)
            || TextUtils.isEmpty(gender)) {
            result = Pair(false, "Please provide all credentials")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            result = Pair(false, "Please provide a valid email")
        } else if (password.length < 6) {
            result = Pair(false, "Password must be at least 6 characters")
        } else if (!password.matches(Regex(".*[A-Z].*"))) {
            result = Pair(false, "Password must contain at least one uppercase letter")
        } else if (!password.matches(Regex(".*[a-z].*"))) {
            result = Pair(false, "Password must contain at least one lowercase letter")
        } else if (!password.matches(Regex(".*[!@#\$%^&*(),.?\":{}|<>].*"))) {
            result = Pair(false, "Password must contain at least one symbol")
        } else if (password != confirmPassword) {
            result = Pair(false, "Passwords do not match")
        }

        return result
    }

    fun validateCredentialsLogin(emailAddress: String, password: String): Pair<Boolean, String> {
        var result = Pair(true, "")

        if (TextUtils.isEmpty(emailAddress)
            || TextUtils.isEmpty(password)) {
            result = Pair(false, "Please provide all credentials")
        } else if (!Patterns.EMAIL_ADDRESS.matcher(emailAddress).matches()) {
            result = Pair(false, "Please provide a valid email")
        } else if (password.length < 6) {
            result = Pair(false, "Password must be at least 6 characters")
        } else if (!password.matches(Regex(".*[A-Z].*"))) {
            result = Pair(false, "Password must contain at least one uppercase letter")
        } else if (!password.matches(Regex(".*[a-z].*"))) {
            result = Pair(false, "Password must contain at least one lowercase letter")
        } else if (!password.matches(Regex(".*[!@#\$%^&*(),.?\":{}|<>].*"))) {
            result = Pair(false, "Password must contain at least one symbol")
        }

        return result
    }
}