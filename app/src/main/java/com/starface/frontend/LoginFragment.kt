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
import com.starface.frontend.databinding.FragmentLoginBinding
import com.starface.frontend.models.UserLoginRequest
import com.starface.frontend.utils.NetworkResult
import com.starface.frontend.utils.TokenManager
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class LoginFragment : Fragment() {
    private var _binding: FragmentLoginBinding? = null
    private val binding get() = _binding!!
    private val authViewModel by viewModels<AuthViewModel>()
    private val searchViewModel by viewModels<SearchViewModel>()
    @Inject
    lateinit var tokenManager: TokenManager
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment
        if(tokenManager.getToken() != null) {
            searchViewModel.getUser()
            findNavController().navigate(R.id.searchFragment)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.btnSignUp.setOnClickListener{
            findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
        }

        binding.btnLogin.setOnClickListener{
            val validationResult = validateUserInput()
            if(validationResult.first) {
                val userRequest = getUserRequest()
                authViewModel.loginUser(userRequest)
            } else {
                binding.txtError.text = validationResult.second
            }
        }


        bindObservers()
    }
    private fun getUserRequest(): UserLoginRequest {
        val emailAddress = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()

        return UserLoginRequest(emailAddress, password)
    }
    private fun validateUserInput(): Pair<Boolean, String> {
        val emailAddress = binding.txtEmail.text.toString()
        val password = binding.txtPassword.text.toString()
        return authViewModel.validateCredentialsLogin(emailAddress, password)
    }
    private fun bindObservers() {
        authViewModel.userReponseLiveData.observe(viewLifecycleOwner, Observer {
            binding.progressBar.isVisible = false
            when(it) {
                is NetworkResult.Success -> {
                    tokenManager.saveToken(it.data!!.token)
                    findNavController().navigate(R.id.action_loginFragment_to_registerFragment)
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