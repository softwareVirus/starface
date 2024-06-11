package com.starface.frontend

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.starface.frontend.databinding.FragmentEnterScreenBinding

class EnterScreenFragment : Fragment() {
    private var _binding: FragmentEnterScreenBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentEnterScreenBinding.inflate(inflater, container, false)
        // Inflate the layout for this fragment

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}