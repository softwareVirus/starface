package com.starface.frontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import com.starface.frontend.databinding.FragmentRegisterBinding
import com.starface.frontend.models.UserSignupRequest
import com.starface.frontend.utils.NetworkResult
import com.starface.frontend.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.random.Random

@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private var _binding: FragmentRegisterBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()
    private val searchViewModel by viewModels<SearchViewModel>()
    @Inject
    lateinit var tokenManager: TokenManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentRegisterBinding.inflate(inflater, container, false)
        if(tokenManager.getToken() != null) {
            searchViewModel.getUser()
            findNavController().navigate(R.id.searchFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener{
            val validationResult = validateUserInput()
            if(validationResult.first) {
                val userRequest = getUserRequest()
                authViewModel.registerUser(userRequest)
            } else {
                binding.txtError.text = validationResult.second
            }
        }

        binding.btnLogin.setOnClickListener{
            findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
        }

        bindObservers()
    }
    private fun getUserRequest(): UserSignupRequest {
        val firstName = binding.txtFirstname.text.toString()
        val lastName = binding.txtLastname.text.toString()
        val emailAddress = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        val gender = binding.txtGender.text.toString()
        val randomValue = Random.nextInt(1, 5).toString()

        return UserSignupRequest(firstName,lastName, emailAddress, password, randomValue, gender)
    }
    private fun validateUserInput(): Pair<Boolean, String> {
        val firstName = binding.txtFirstname.text.toString()
        val lastName = binding.txtLastname.text.toString()
        val emailAddress = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        val confirmPassword = binding.txtConfirmPassword.text.toString()
        val gender = binding.txtGender.text.toString()
        return authViewModel.validateCredentials(firstName,lastName, emailAddress, password, confirmPassword, gender)
    }
    private fun bindObservers() {
        authViewModel.userReponseLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible = false
            when(it) {
                is NetworkResult.Success -> {
                    tokenManager.saveToken(it.data!!.token)
                    findNavController().navigate(R.id.action_registerFragment_to_loginFragment)
                }
                is NetworkResult.Error -> {
                    binding.txtError.text = it.message
                }
                is NetworkResult.Loading -> {
                    binding.progressBar.isVisible = true
                }
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}