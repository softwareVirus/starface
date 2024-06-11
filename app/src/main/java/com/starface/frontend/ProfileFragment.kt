package com.starface.frontend

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.starface.frontend.R

class ProfileFragment : Fragment() {

    private lateinit var avatarImageView: ImageView
    private lateinit var firstNameEditText: EditText
    private lateinit var lastNameEditText: EditText
    private lateinit var emailEditText: EditText
    private lateinit var emailVerificationStatusTextView: TextView
    private lateinit var verifyEmailButton: Button
    private lateinit var passwordEditText: EditText
    private lateinit var confirmPasswordEditText: EditText
    private lateinit var saveButton: Button
    private val authViewModel by viewModels<SearchViewModel>()
    private var user: com.starface.frontend.models.User? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_profile, container, false)

        avatarImageView = view.findViewById(R.id.avatar_image)
        firstNameEditText = view.findViewById(R.id.first_name)
        lastNameEditText = view.findViewById(R.id.last_name)
        emailEditText = view.findViewById(R.id.email)
        emailVerificationStatusTextView = view.findViewById(R.id.email_verification_status)
        verifyEmailButton = view.findViewById(R.id.verify_email_button)
        passwordEditText = view.findViewById(R.id.password)
        confirmPasswordEditText = view.findViewById(R.id.confirm_password)
        saveButton = view.findViewById(R.id.save_button)

        // Load user details and set up views
        loadUserDetails()

        verifyEmailButton.setOnClickListener {
            // Handle email verification
            sendVerificationEmail()
        }

        saveButton.setOnClickListener {
            // Handle save button click
            saveProfile()
        }

        return view
    }

    private fun loadUserDetails() {
        // Replace with your actual logic to load user details
        val user = getUserDetails()

        firstNameEditText.setText(user.firstName)
        lastNameEditText.setText(user.lastName)
        emailEditText.setText(user.email)

        // Check if the email is verified
        if (user.isEmailVerified) {
            emailVerificationStatusTextView.text = getString(R.string.email_verified)
            emailVerificationStatusTextView.setTextColor(resources.getColor(R.color.black))
            verifyEmailButton.visibility = View.GONE
        } else {
            emailVerificationStatusTextView.text = getString(R.string.email_not_verified)
            emailVerificationStatusTextView.setTextColor(resources.getColor(R.color.red))
            verifyEmailButton.visibility = View.VISIBLE
        }
    }

    private fun sendVerificationEmail() {
        // Replace with your actual logic to send a verification email
        // Example: Send verification email to the user
    }

    private fun saveProfile() {
        // Replace with your actual logic to save the profile
        // Example: Save user details
    }

    private fun getUserDetails(): User {
        // Replace with your actual logic to get the logged-in user's details
        return User("John", "Doe", "john.doe@example.com", false) // Example user details
    }

    data class User(val firstName: String, val lastName: String, val email: String, val isEmailVerified: Boolean)
}
